package com.buddhadata.projects.jukebox.scheduler;

import com.buddhadata.projects.jukebox.EventTypeEnum;
import com.buddhadata.projects.jukebox.kafka.producer.ProducerFactory;
import com.buddhadata.projects.jukebox.random.client.RandomHelper;
import com.buddhadata.projects.jukebox.random.client.services.RandomService;
import com.buddhadata.projects.jukebox.subsonic.client.SubsonicHelper;
import com.buddhadata.projects.jukebox.subsonic.client.services.AlbumSongService;
import com.buddhadata.projects.jukebox.subsonic.client.services.JukeboxService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.subsonic.restapi.Child;
import org.subsonic.restapi.NowPlayingEntry;
import org.subsonic.restapi.Songs;
import org.subsonic.restapi.SubsonicResponse;
import retrofit2.Response;
import retrofit2.Retrofit;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;

/**
 * Tracks what is currently playing on the jukebox and publishes any changes
 */
@Component
@EnableScheduling
public class SchedulerService {

  /**
   * Retrofit service for making calls to the Subsonic AlbumSong API
   */
  private AlbumSongService album;

  @Value("${jukebox.kafka.enabled:false}")
  private boolean isKafkaEnabled;

  /**
   * What's the maximum number of songs which we want to deal with.
   */
  @Value("${jukebox.scheduler.playlist.maxrandom:250}")
  private int maxSongCount;

  /**
   * If current playlist is smaller than this, we'll queue random songs.
   */
  @Value("${jukebox.scheduler.playlist.minsize}")
  private int minPlaylistSize;

  /**
   * Retrofit service for making calls to the Subsonic Jukebox API
   */
  private JukeboxService jukebox;

  /**
   * Track what was the last entry found for what is currently playing.
   */
  private NowPlayingEntry previousEntry = null;

  /**
   * Object mapper used to convert object into JSON/string that can be serialized.
   */
  private ObjectMapper om;

  /**
   * Kafka producer to use for each event published.
   */
  private Producer<Long, String> kafkaProducer;

  /**
   * Retrofit interface for generating random numbers.
   */
  private RandomService random;

  /**
   * The Kafka instance/cluseter to which we're publishing events
   */
  @Value("${jukebox.kafka.broker}")
  private String kafkaBroker;

  /**
   * The Kafka services name used when creating a producer
   */
  @Value("${jukebox.kafka.clientname}")
  private String kafkaClientName;

  /**
   * The Kafka to which the now playing events are published.
   */
  @Value("${jukebox.kafka.topic}")
  private String kafkaTopicName;

  /**
   * Host where the random-number generator service can be reached.
   */
  @Value("${jukebox.random.hostname}")
  private String randomHostName;

  /**
   * Client name to use when connecting to subsonic.
   */
  @Value("${jukebox.clientname:unknown}")
  private String subsonicClientName;

  /**
   * Host name or IP address for subsonic server
   */
  @Value("${jukebox.hostname:localhost}")
  private String subsonicHostName;

  /**
   * Password for authenticating to Subsonic server
   */
  @Value("${jukebox.credentials.password:admin}")
  private String subsonicPassword;

  /**
   * User name for authenticating to Subsonic server
   */
  @Value("${jukebox.credentials.username:admin}")
  private String subsonicUsername;

  /**
   * Determine if the playlist size is too small and, if so, start queue stuff randomly.
   */
  @Scheduled (initialDelay=60000, fixedDelay = 30000)
  public void scheduleIfNecessary() {

    //  Determine if there's a need to schedule, either the playlist is empty - most likely indicating no songs were
    //  ever added - or we've progressed far enough into the playlist the more songs need to be scheduled.
    int songsInPlaylist = getCurrentPlaylist().size();
    if (songsInPlaylist == 0 || (songsInPlaylist - getPlaylistPosition()) < minPlaylistSize) {

      //  Pick a random number of songs to queue.
      int songsToQueue;
      try {
        songsToQueue = (int) (maxSongCount * random.getDouble().execute().body());
      } catch (IOException ioe) {
        songsToQueue = minPlaylistSize;
      }

      //  Now that we have a count, randomly queue songs.
      for (int i = 0; i < songsToQueue; i++) {
        queueRandomSong();
      }

      //  If initially the playlist was empty, assume that we starting from scratch and need to start playing songs.
      if (songsInPlaylist == 0) {
        startJukebox();
      }
    }
  }

  /**
   * Get the current jukebox playlist.
   * @return List of zero or more songs currently queued up to be played.
   */
  private List<Child> getCurrentPlaylist() {

    List<Child> toReturn;
    try {
      Response<SubsonicResponse> response =
        jukebox.jukeboxControl(subsonicUsername, subsonicPassword, SubsonicHelper.SUBSONIC_API_VERSION, subsonicClientName, "get", null, null, null, null).execute();
      toReturn = response.body().getJukeboxPlaylist().getEntry();
    } catch (IOException ioe) {

      //  Something really bad happened, we'll just return an empty collection
      System.out.println ("Exception occurred making call to Subsonic: " + ioe);
      toReturn = Collections.EMPTY_LIST;
    }


    return toReturn;
  }


  /**
   * The playlist position of the currently playing song.
   * @return playlist position
   */
  private int getPlaylistPosition() {

    int toReturn;
    try {
      Response<SubsonicResponse> response =
        jukebox.jukeboxControl(subsonicUsername, subsonicPassword, SubsonicHelper.SUBSONIC_API_VERSION, subsonicClientName, "status", null, null, null, null).execute();
        toReturn = response.body().getJukeboxStatus().getCurrentIndex();
    } catch (IOException ioe) {

      //  Something really bad happened, we'll just return an empty collection
      System.out.println ("Exception occurred making call to Subsonic: " + ioe);
      toReturn = 0;
    }


    return toReturn;
  }

  /**
   * Queue a random song from the list
   */
  private void queueRandomSong () {

    try {
      //  Let Subsonic provide with a set of random songs, however it determines it; our randomness is how many songs should be returned.
      List<Child> songs = album.getRandomSongs(subsonicUsername, subsonicPassword, SubsonicHelper.SUBSONIC_API_VERSION, subsonicClientName, getBoundedInt(maxSongCount), null, null, null, null)
        .execute()
        .body()
        .getRandomSongs()
        .getSong();

      //  Do various sorting to further confuse things.
      songs.sort(Comparator
        .comparingLong(Child::getPlayCount)
        .thenComparing((Child c) -> c.getTitle())
        .reversed()
        .thenComparing((Child c) -> c.getArtist()));


      //  Out of the now-sorted-resorted-unsorted collection of songs, attempt to pick random.
      Child selected = songs.get(getBoundedInt(songs.size()));

      //  And because we can, one last randomness to inject to determine if this song is in fact going to be queued up,
      //  only when we end up with zero will we not queue
      if (((Long.valueOf(selected.getId()) & getPositiveLong()) & 0b0011) != 0) {

        //  Add selected song to the queue.
        Response<SubsonicResponse> response = jukebox.jukeboxControl(subsonicUsername, subsonicPassword, SubsonicHelper.SUBSONIC_API_VERSION, subsonicClientName, "add", null, null, selected.getId(), null).execute();
        if (response.isSuccessful()) {

          System.out.println ("Song queued: " + selected.getTitle() + " by " + selected.getArtist());
          if (isKafkaEnabled) {
            //  Publish a kafka message with information about the song being played.
            try {
              kafkaProducer.send(new ProducerRecord<>(kafkaTopicName, System.currentTimeMillis(),
                om.writeValueAsString(SubsonicHelper.instance.createEvent(selected, EventTypeEnum.QUEUE)))).get();
            } catch (Throwable t) {
              System.out.println("Exception while writing out Kafka information: " + t);
            }
          }
        } else {
          System.out.println("Failure to add song: " + response.errorBody());
        }
      }
    } catch (Exception e) {
      System.out.println("Exception occurred making call to Subsonic: " + e);
    }
  }

  /**
   * Post-create steps to setup the controller before use, such as creating the various services required.
   */
  @PostConstruct
  public void init() {

    //  Set up everything needed to communicate with subsonic device.
    album = SubsonicHelper.instance.createService (subsonicHostName, AlbumSongService.class);
    jukebox = SubsonicHelper.instance.createService (subsonicHostName, JukeboxService.class);

    if (isKafkaEnabled) {
      //  Create the Kafka producer to use through the life.
      kafkaProducer = (Producer<Long, String>) ProducerFactory.instance.get(kafkaBroker, kafkaClientName, Long.class, String.class);
    }

    //  Client-side for random number service.
    random = RandomHelper.instance.createService (randomHostName, RandomService.class);

    //  The object mapper is thread-safe and serializes the now-playing event into a JSON string.
    om = new ObjectMapper();
  }

  /**
   * Get a random bounded integer for various purposes throughout scheduling
   * @return randomly-generated integer.
   */
  private int getBoundedInt (int bound) {

    int toReturn;
    try {
      toReturn = random.getIntegerBound(bound).execute().body();
    } catch (IOException ioe) {
      //  On exceptions, revert back to standard Java random number generator.
      toReturn = new Random().nextInt(bound);
    }


    return toReturn;
  }

  /**
   * Get a random long for various purposes throughout scheduling
   * @return
   */
  private long getPositiveLong () {

    long toReturn;
    try {
      toReturn = Math.abs(random.getLong().execute().body());
    } catch (IOException ioe) {
      //  On exceptions, revert back to standard Java random number generator.
      toReturn = new Random().nextLong();
    }


    return toReturn;

  }
  /**
   * Start the jukebox playing, if possible
   */
  private void startJukebox() {

    try {
      //  Attempt to make the call to the Subsonic server
      Response<SubsonicResponse> response = jukebox.jukeboxControl(subsonicUsername, subsonicPassword, SubsonicHelper.SUBSONIC_API_VERSION, subsonicClientName, "start", 0, null, null, null).execute();

      //  Did we succeed or not?
      if (response.isSuccessful()) {
        System.out.println("Jukebox started.");
      } else {
        System.out.println("Jukebox NOT started.");
      }
    } catch (IOException ioe) {
      System.out.println ("Exception while trying to start jukebox: " + ioe);
    }
  }

}
