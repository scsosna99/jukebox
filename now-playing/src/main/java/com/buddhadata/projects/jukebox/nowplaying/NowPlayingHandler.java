package com.buddhadata.projects.jukebox.nowplaying;

import com.buddhadata.projects.jukebox.*;
import com.buddhadata.projects.jukebox.kafka.producer.ProducerFactory;
import com.buddhadata.projects.jukebox.subsonic.client.SubsonicHelper;
import com.buddhadata.projects.jukebox.subsonic.client.services.AlbumSongServices;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.subsonic.restapi.Child;
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
   * The Kafka services name used when creating a producer
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
        Optional<NowPlayingEntry> player = SubsonicHelper.instance.getNowPlayingForPlayer(response.body().getNowPlaying(), subsonicClientName, subsonicUsername);
        if (player.isPresent()) {
          NowPlayingEntry entry = player.get();
          if (previousEntry != null && !entry.getId().equals(previousEntry.getId())) {
            System.out.println ("Song has changed: " + entry.getTitle());

            //  Publish a kafak message with information about the song being played.
            try {
              kafkaProducer.send(new ProducerRecord<>(kafkaTopicName, System.currentTimeMillis(),
                om.writeValueAsString(SubsonicHelper.instance.createEvent(entry, EventTypeEnum.PLAY)))).get();
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
   * Post-create steps to setup the controller before use, such as creating the various services required.
   */
  @PostConstruct
  public void init() {
    //  Set up everything needed to communicate with subsonic device.
    album = SubsonicHelper.instance.createService (subsonicHostName, AlbumSongServices.class);

    //  Create the Kafka producer to use through the life.
    kafkaProducer = (Producer<Long, String>) ProducerFactory.instance.get(kafkaBroker, kafkaClientName, Long.class, String.class);

    //  The object mapper is thread-safe and serializes the now-playing event into a JSON string.
    om = new ObjectMapper();
  }
}
