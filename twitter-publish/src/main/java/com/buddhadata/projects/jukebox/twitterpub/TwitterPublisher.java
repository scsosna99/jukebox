package com.buddhadata.projects.jukebox.twitterpub;

import com.buddhadata.projects.jukebox.JukeboxEvent;
import com.buddhadata.projects.jukebox.kafka.consumer.ConsumerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * Service for consuming from the Kafka topic and publishing to the Twitter jukebox user.
 */
@Component
@EnableScheduling
public class TwitterPublisher {

  /**
   * Consumes messages from the Kafka topic.
   */
  private Consumer<Long, String> kafkaConsumer;

  /**
   * Object mapper used for converting JSON in message back into object.
   */
  private ObjectMapper om;

  /**
   * The Kafka instance/cluseter to which we're publishing events
   */
  @Value("${jukebox.kafka.broker}")
  private String kafkaBroker;

  /**
   * The Kafka topic to which the now playing events are published.
   */
  @Value("${jukebox.kafka.topic}")
  private String kafkaTopicName;

  /**
   * Twitter OAuth Access Token password
   */
  @Value("${jukebox.twitter.access.secret}")
  private String twitterAccessSecret;

  /**
   * Twitter OAuth Access Token screen/user name
   */
  @Value("${jukebox.twitter.access.token}")
  private String twitterAccessToken;

  /**
   * Twitter OAuth consumer key
   */
  @Value("${jukebox.twitter.consumer.key}")
  private String twitterOauthConsumerKey;

  /**
   * Twitter OAuth consumer secret
   */
  @Value("${jukebox.twitter.consumer.secret}")
  private String twitterOauthConsumerSecret;


  /**
   * Twitter instance to set status.
   */
  private Twitter twitter;

  /**
   * The group name used by Kafka to identify a consumer
   */
  @Value("${jukebox.kafka.groupname}")
  private String kafkaGroupName;

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
              StringBuffer buffer = new StringBuffer(1024);
              switch (event.getEvent()) {

                case BACK:
                  buffer.append ("PREVIOUS SONG");
                  break;
                case DEQUEUE:
                  buffer.append ("SONG DEQUEUED: ").append (buildSongInfo(event));
                  break;
                case PLAY:
                  buffer.append ("SONG NOW PLAYING: ").append (buildSongInfo(event));
                  break;
                case QUEUE:
                  buffer.append ("SONG QUEUED: ").append (buildSongInfo(event));
                  break;
                case RESTART:
                  buffer.append ("RESTARTING SONG");
                  break;
                case SKIP:
                  buffer.append ("SKIPPING SONG");
                  break;
                case START:
                  buffer.append ("JUKEBOX STOPPED");
                  break;
                case STOP:
                  buffer.append ("JUKEBOX STARTED");
                  break;
              }

              //  Publish the tweet.
              twitter.updateStatus(buffer.toString());
            } catch (IOException ioe) {
              System.out.println ("Exception attempting to deserialize the event info: " + ioe);
            } catch (TwitterException te) {
              System.out.println ("Exception while tweeting: " + te);
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
   * Post-create steps to setup the controller before use, such as creating the various services required.
   */
  @PostConstruct
  public void init() {

    //  Create the Kafka consumer to be used through the lifetime of this service.
    kafkaConsumer = (Consumer<Long, String>) ConsumerFactory.instance.get(kafkaBroker, kafkaGroupName, kafkaTopicName, Long.class, String.class);

    //  The object mapper is thread-safe and serializes the now-playing event into a JSON string.
    om = new ObjectMapper();

    //  Create/initialize the Twitter instance used to update status and publish events
    twitter = new TwitterFactory().getInstance();
    twitter.setOAuthConsumer(twitterOauthConsumerKey, twitterOauthConsumerSecret);
    twitter.setOAuthAccessToken(new AccessToken(twitterAccessToken, twitterAccessSecret));
  }

  /**
   * Formats the song for which the event was for, very little formatting, just getting something out there.
   * @param event event received from Kafka
   * @return the song information in a readable format
   */
  private String buildSongInfo (JukeboxEvent event) {
    return "From the album \"" + event.getSong().getAlbum().getName() + "\", it's \"" + event.getSong().getTitle() + "\" by " + event.getSong().getArtist().getName() + ".";
  }
}
