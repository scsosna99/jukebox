package com.buddhadata.projects.jukebox.nowplaying;

import com.buddhadata.projects.jukebox.*;
import com.buddhadata.projects.jukebox.kafka.producer.ProducerFactory;
import com.buddhadata.projects.jukebox.subsonic.client.AlbumSongServices;
import com.buddhadata.projects.jukebox.subsonic.client.JukeboxService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.subsonic.restapi.NowPlayingEntry;
import org.subsonic.restapi.SubsonicResponse;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jaxb.JaxbConverterFactory;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.Comparator;
import java.util.Optional;

/**
 * Tracks what is currently playing on the jukebox and publishes any changes
 */
@Component
@EnableScheduling
public class NowPlayingHandler {

  /**
   * Retrofit service for making calls to the Subsonic AlbumSong API
   */
  private AlbumSongServices album;

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
   * Global Retrofit instance from which individual services will be created.
   */
  private Retrofit retrofit = null;

  /**
   * The constructed/expected player name, assuming everything goes well.
   */
  private String playerName;

  /**
   * The Kafka instance/cluseter to which we're publishing events
   */
  @Value("${jukebox.kafka.broker}")
  private String kafkaBroker;

  /**
   * The Kafka to which the now playing events are published.
   */
  @Value("${jukebox.kafka.topic}")
  private String kafkaTopicName;

  /**
   * The Kafka client name used when creating a producer
   */
  @Value("${jukebox.kafka.clientname}")
  private String kafkaClientName;

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
   * Scheduled method that kicks in every 30 seconds looking for a change in the currently playing song.
   */
  @Scheduled (fixedDelay=30000)
  public void checkNowPlaying () {

    try {
      Response<SubsonicResponse> response =
        album.getNowPlaying(subsonicUsername, subsonicPassword, "1.16.0", subsonicClientName).execute();
      if (response.isSuccessful()) {

        //  Filter for what we expect to be the player name.
        Optional<NowPlayingEntry> player = response.body().getNowPlaying().getEntry().stream()
          .filter(one -> one.getUsername().equals(subsonicUsername))
          .sorted(Comparator.comparingInt(NowPlayingEntry::getMinutesAgo))
          .filter(one -> one.getPlayerName().equals(playerName))
          .findFirst();
        if (player.isPresent()) {
          NowPlayingEntry entry = player.get();
          if (previousEntry != null && !entry.getId().equals(previousEntry.getId())) {
            System.out.println ("Song has changed: " + entry.getTitle());

            //  Publish a kafak message with information about the song being played.
            try {
              kafkaProducer.send(new ProducerRecord<>(kafkaTopicName, System.currentTimeMillis(), om.writeValueAsString(createEvent(entry)))).get();
            } catch (Throwable t) {
              System.out.println ("Exception while writing out Kafka information: " + t);
            }
          }

          //  Save the current song so we have something to compare against next time.
          previousEntry = entry;
        } else {
          System.out.println ("Did not find Jukebox player.");
        }
      } else {
        System.out.println ("Error calling Subsonic service: " + response.errorBody().string());
      }
    } catch (IOException ioe) {
      //  Something really bad happened.
      System.out.println ("Exception occurred making call to Subsonic: " + ioe);
    }


    return;
  }


  /**
   * Create the jukebox entry that is going to be published, which is a subset of information about a song.
   * @param entry the song currently playing
   * @return the populated song event.
   */
  private JukeboxEvent createEvent (NowPlayingEntry entry) {

    //  Artist
    ArtistType artist = new ArtistType();
    artist.setId (entry.getArtistId());
    artist.setName(entry.getArtist());

    //  ALbum
    AlbumType album = new AlbumType();
    album.setId(entry.getAlbumId());
    album.setName(entry.getAlbum());

    //  Song
    SongType song = new SongType();
    song.setId (entry.getId());
    song.setTitle(entry.getTitle());
    song.setArtist(artist);
    song.setAlbum(album);
    song.setTrack(entry.getTrack());
    song.setDuration(entry.getDuration());
    song.setGenre(entry.getGenre());
    song.setYear(entry.getYear());

    //  Jukebox event to send
    JukeboxEvent toReturn = new JukeboxEvent();
    toReturn.setEvent(EventTypeEnum.PLAY);
    toReturn.setSong (song);

    return toReturn;
  }


  /**
   * Post-create steps to setup the controller before use, such as creating the various services required.
   */
  @PostConstruct
  public void init() {
    //  Set up everything needed to communicate with subsonic device.
    try {
      JAXBContext context = JAXBContext.newInstance("org.subsonic.restapi");
      retrofit = new Retrofit.Builder()
        .baseUrl("http://" + subsonicHostName + "/rest/")
        .addConverterFactory(JaxbConverterFactory.create(context))
        .build();
    } catch (JAXBException e) {
      System.out.println ("Exception setting up retrofit: " + e);
    }

    //  Create the individual services required
    album = retrofit.create(AlbumSongServices.class);
    jukebox = retrofit.create(JukeboxService.class);

    //  The player name is based on the user and client names.
    playerName = subsonicClientName + "-" + subsonicUsername;

    //  Create the Kafka producer to use through the life.
    kafkaProducer = (Producer<Long, String>) ProducerFactory.instance.get(kafkaBroker, kafkaClientName, Long.class, String.class);

    //  The object mapper is thread-safe and serializes the now-playing event into a JSON string.
    om = new ObjectMapper();
  }
}
