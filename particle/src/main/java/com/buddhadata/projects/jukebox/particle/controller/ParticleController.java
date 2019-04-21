/*
 * Copyright (c) 2019  Scott C. Sosna  ALL RIGHTS RESERVED
 *
 */

package com.buddhadata.projects.jukebox.particle.controller;

import com.buddhadata.projects.jukebox.EventTypeEnum;
import com.buddhadata.projects.jukebox.JukeboxEvent;
import com.buddhadata.projects.jukebox.kafka.consumer.ConsumerFactory;
import com.buddhadata.projects.jukebox.kafka.producer.ProducerFactory;
import com.buddhadata.projects.jukebox.particle.event.VolumeChangeEvent;
import com.buddhadata.projects.jukebox.subsonic.client.SubsonicHelper;
import com.buddhadata.projects.jukebox.subsonic.client.services.JukeboxService;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.infcomtec.jparticle.Cloud;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.subsonic.restapi.Child;
import org.subsonic.restapi.JukeboxStatus;
import org.subsonic.restapi.SubsonicResponse;
import retrofit2.Response;

import javax.annotation.PostConstruct;
import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Controller that interacts with the Particle Photon IoT
 */
@RestController
@RequestMapping("particle")
@EnableScheduling
public class ParticleController {

  /**
   * flag indicating if Kafka integration is enabled or not
   */
  @Value("${jukebox.kafka.enabled:false}")
  private boolean isKafkaEnabled;

  /**
   * Manages the interactions with the Particle cloud
   */
  private Cloud particleCloud;

  /**
   * Consumes messages from the Kafka topic.
   */
  private Consumer<Long, String> kafkaConsumer = null;

  /**
   * Retrofit service for making calls to the Subsonic Jukebox API
   */
  private JukeboxService jukebox;

  /**
   * Object mapper used for converting JSON in message back into object.
   */
  private ObjectMapper om;

  /**
   * Kafka producer to use for each event published.
   */
  private Producer<Long, String> kafkaProducer = null;

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
   * The Kafka instance/cluseter to which we're publishing events
   */
  @Value("${jukebox.kafka.broker}")
  private String kafkaBroker;

  /**
   * The name provided to Kafka to uniquely identify a client
   */
  @Value("${jukebox.kafka.clientname}")
  private String kafkaClientName;

  /**
   * The group name used by Kafka to identify a consumer
   */
  @Value("${jukebox.kafka.groupname}")
  private String kafkaGroupName;

  /**
   * The Kafka topic to which the now playing events are published.
   */
  @Value("${jukebox.kafka.topic}")
  private String kafkaTopicName;

  /**
   * Access token that provide access to the Particle cloud event system
   */
  @Value("${jukebox.particle.token}")
  private String particleAccessCode;

  /**
   * Identifies the specific particle device to the Particle cloud
   */
  @Value("${jukebox.particle.device}")
  private String particleDeviceId;

  /**
   * the fully-qualified host for Particle cloud functionality
   */
  @Value("${jukebox.particle.hostname:api.particle.io}")
  private String particleHostName;

  /**
   * the protocol used to access the Particle cloud functionality
   */
  @Value("${jukebox.particl.protocol:https}")
  private String particleProtocol = "https";

  /**
   * URL for sending command to the function controlling the green LED
   */
  private URL particleFunctionGreen;

  /**
   * URL for sending command to the function controlling the red LED
   */
  private URL particleFunctionRed;

  /**
   * Singleton instance for handling volume change events received from particle cloud
   */
  @Autowired
  private VolumeChangeEvent eventHandler;

  /**
   * command arguments to the LED calls to turn the LED off
   */
  private static final byte[] PARTICLE_LED_COMMAND_OFF = "args=off".getBytes();

  /**
   * command arguments to the LED calls to turn the LED on
   */
  private static final byte[] PARTICLE_LED_COMMAND_ON = "args=on".getBytes();

  /**
   * Event type for sending a miscellaneous message to the Photon
   */
  private static final String EVENT_ANY_MESSAGE = "jukebox.message";

  /**
   * Event type for sending a song-changed event to the Photon.
   */
  private static final String EVENT_SONG_CHANGED = "jukebox.song.changed";

  /**
   * Formatted URL with a few substitutions needed for specific device, command, etc.
   */
  private static final String PARTICLE_URL_FORMAT = "/v1/devices/%s/%s?access_token=%s";


  @PutMapping(value = "/message", consumes = {MediaType.TEXT_PLAIN_VALUE}, produces = {MediaType.TEXT_PLAIN_VALUE})
  public String publishMessage(@RequestBody String message) {

    particleCloud.publish(EVENT_ANY_MESSAGE, message);
    return "Message published";
  }

  /**
   * Mute or umute the jukebox, depending on its current status.
   */
  @GetMapping("/muteUnmute")
  public String muteUnmute() {

    try {
      //  Get the current status of the jukebox, which includes the gain.
      JukeboxStatus status = SubsonicHelper.instance.jukeboxStatus(jukebox, subsonicUsername, subsonicPassword, subsonicClientName);
      if (status != null) {

        //  Determine what the new gain should be
        Double gain = (status.getGain() > 0.0f) ? 0.0 : 1.0;

        Response<SubsonicResponse> response = jukebox.jukeboxControl(subsonicUsername, subsonicPassword, SubsonicHelper.SUBSONIC_API_VERSION, subsonicClientName, "setGain", null, null, null, gain).execute();
        if (response.isSuccessful()) {
          System.out.println ("Successfully mute/unmute");
        } else {
          System.out.println ("Unsuccessfully mute/unmuting");
        }
      } else {
        System.out.println ("Unable to determine status of jukebox");
      }
    } catch (IOException ioe) {
      System.out.println ("Exception muting volume: " + ioe);
    }


    return "muteUnmute";
  }

  @GetMapping("/next")
  public String next() {

    try {
      //  First, get the status so we know index of current song.
      JukeboxStatus status = SubsonicHelper.instance.jukeboxStatus(jukebox, subsonicUsername, subsonicPassword, subsonicClientName);
      if (status != null) {
        Response<SubsonicResponse> response = jukebox.jukeboxControl(subsonicUsername, subsonicPassword, SubsonicHelper.SUBSONIC_API_VERSION, subsonicClientName, "skip", status.getCurrentIndex() + 1, null, null, null).execute();
        if (response.isSuccessful()) {
          publishEvent (null, EventTypeEnum.SKIP);
        } else {
          System.out.println ("Error skipping song.");
        }
      } else {
        System.out.println("Unable to determine jukebox status");
      }
    } catch (IOException ioe) {
      System.out.println ("Exception attempting to skip song: " + ioe);
    }


    return "next";
  }

  @GetMapping("/startStop")
  public String startStop() {
    try {
      //  Determine current state of jukebox.
      JukeboxStatus status = SubsonicHelper.instance.jukeboxStatus(jukebox, subsonicUsername, subsonicPassword, subsonicClientName);
      if (status == null || !status.isPlaying()) {
        startJukebox();
      } else {
        stopJukebox();
      }
      return "stopStart";
    } catch (IOException ioe) {
      //  Something really bad happened.
      System.out.println("Exception occurred making call to Subsonic: " + ioe);
      return "Exception start/stop";
    }
  }

  /**
   * Post-create steps to setup the controller before use, such as creating the various services required.
   */
  @PostConstruct
  public void init() {

    //  Create the individual services required
    jukebox = SubsonicHelper.instance.createService(subsonicHostName, JukeboxService.class);

    if (isKafkaEnabled) {
      //  Create the Kafka consumer to be used through the lifetime of this service.
      kafkaConsumer = (Consumer<Long, String>) ConsumerFactory.instance.get(kafkaBroker, kafkaGroupName, kafkaTopicName, Long.class, String.class, false);

      //  Create the Kafka producer to use through the life.
      kafkaProducer = (Producer<Long, String>) ProducerFactory.instance.get(kafkaBroker, kafkaClientName, Long.class, String.class);
    }

    //  The object mapper is thread-safe and serializes the now-playing event into a JSON string.
    om = new ObjectMapper();

    //  Create instance of the JParticle Cloud to gain access to the Particle cloud infrastructure.
    particleCloud = new Cloud (particleAccessCode, true, false);

    //  Subscribe to the events being published by Particle IoT
    particleCloud.subscribe(eventHandler);

    //  Create the URLs needed for controlling the red and green LEDs.
    try {
      particleFunctionGreen = new URL(particleProtocol, particleHostName, String.format(PARTICLE_URL_FORMAT, particleDeviceId, "green", particleAccessCode));
      particleFunctionRed = new URL(particleProtocol, particleHostName, String.format(PARTICLE_URL_FORMAT, particleDeviceId, "red", particleAccessCode));
    } catch (MalformedURLException e) {
      System.out.println ("Malformed URL: " + e);
    }
  }

  @Scheduled(initialDelay = 5000, fixedDelay = 15000)
  private void processEvents() {

    try {
      while (true) {

        //  Attempt to consume events from Kafka.  There should be no reason to periodically break out of the
        //  polling, so
        ConsumerRecords<Long, String> recs = kafkaConsumer.poll(Long.MAX_VALUE);

        //  Was records/events returned (which should be a rhetorical question since the only reason poll()
        //  should return is when there's something available.
        if (recs.count() > 0) {

          //  Process each event.
          recs.forEach(record -> {

            //  Deserialize the JSON string into an object.
            try {
              JukeboxEvent event = om.readValue(record.value(), JukeboxEvent.class);

              //  Build the status update
              switch (event.getEvent()) {

                case PLAY:
                  System.out.println ("Song changed: " + event.getSong().getTitle());
                  particleCloud.publish(EVENT_SONG_CHANGED, event.getSong().getTitle());
                  break;

                case QUEUE:
                  //  For every song queue, toggle the lights
                  System.out.println ("Song queued");
                  sendLEDCommand(particleFunctionRed, true);
                  sendLEDCommand(particleFunctionRed, false);
                  break;

                case SKIP:
                  break;

                case START:
                  //  When playing, we want the green LED on
                  System.out.println ("Jukebox started");
                  sendLEDCommand(particleFunctionGreen, true);
                  break;

                case STOP:
                  //  When stopped, we want the green LED off
                  System.out.println ("Jukebox stopped");
                  sendLEDCommand(particleFunctionGreen, false);
                  break;

                //  Anything else we just ignore.
                default:
                  break;

              }
            } catch (IOException ioe) {
              System.out.println ("Exception attempting to deserialize the event info: " + ioe);
            }
          });

          //  Commit the offset of the broker once the records are processed.
          kafkaConsumer.commitAsync();
        } else {
          break;
        }
      }
    } catch (Throwable t) {
      //  Catch anything bad that might happen and just plan on retrying the next time the scheduler kicks this off
      System.out.println ("Exception occurred while processing messages: " + t);
    }
  }

  /**
   * Publish jukebox event for the song, if provided
   * @param entry the Child node, or song, about which an event has occurred
   * @param eventType the type of event
   */
  private void publishEvent (Child entry,
                             EventTypeEnum eventType) {

    if (isKafkaEnabled) {
      //  Publish a kafak message with information about the song being played.
      try {
        kafkaProducer.send(new ProducerRecord<>(kafkaTopicName, System.currentTimeMillis(),
          om.writeValueAsString(SubsonicHelper.instance.createEvent(entry, eventType)))).get();
      } catch (Throwable t) {
        System.out.println("Exception while writing out Kafka information: " + t);
      }
    }
  }

  /**
   * Sends an on/off command to the Particle device based on the URL sent.
   * @param url calls either the green or red LED function
   * @param on if we're not turning on the light, we're turning it off.
   */
  private void sendLEDCommand (URL url,
                               boolean on) {

    try {
      //  create a connection for the specific URL passed in.
      HttpsURLConnection connect = (HttpsURLConnection) url.openConnection();

      //  Configure the connection
      connect.setDoOutput(true);
      connect.setRequestMethod("POST");

      //  Define the command being sent
      connect.getOutputStream().write(on ? PARTICLE_LED_COMMAND_ON : PARTICLE_LED_COMMAND_OFF);

      //  Getting the input stream forces the command to be sent.
      connect.getInputStream();
    } catch (IOException ioe) {
      //  Exceptions or other weirdnesses don't concern me, just log and forget (unfortunately).
      System.out.println ("Exception attempting to call LED functions: " + ioe);
    }
  }

  /**
   * Start the jukebox playing
   */
  private void startJukebox() throws IOException {

    //  Attempt to make the call to the Subsonic server
    Response<SubsonicResponse> response = jukebox.jukeboxControl(subsonicUsername, subsonicPassword, SubsonicHelper.SUBSONIC_API_VERSION, subsonicClientName, "start", 0, null, null, null).execute();

    //  Did we succeed or not?
    if (response.isSuccessful()) {
      publishEvent (null, EventTypeEnum.START);
    } else {
      System.out.println ("Jukebox not started.");
    }
  }

  /**
   * stop the jukebox from playing
   */
  private void stopJukebox() throws IOException {

    Response<SubsonicResponse> response = jukebox.jukeboxControl(subsonicUsername, subsonicPassword, SubsonicHelper.SUBSONIC_API_VERSION, subsonicClientName, "stop", null, null, null, null).execute();

    //  Did we succeed or not?
    if (response.isSuccessful()) {
      publishEvent (null, EventTypeEnum.STOP);
    } else {
      System.out.println ("Jukebox not stopped.");
    }
  }
}

