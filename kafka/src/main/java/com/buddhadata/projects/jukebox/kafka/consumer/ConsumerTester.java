package com.buddhadata.projects.jukebox.kafka.consumer;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;

/**
 * Just a crappy way to test the functionality provided by this module
 */
public class ConsumerTester {

  public static void main (String[] args) {

    Consumer<Long, String> consumer = (Consumer<Long, String>) ConsumerFactory.instance.get("10.10.9.8:9092", "myGroup", "topictopictopic", Long.class, String.class);
    while (true) {
      ConsumerRecords<Long,String> recs = consumer.poll(Long.MAX_VALUE);
      if (recs.count() > 0) {
        recs.forEach (record -> {
          System.out.println("Record Key " + record.key());
          System.out.println("Record value " + record.value());
          System.out.println("Record partition " + record.partition());
          System.out.println("Record offset " + record.offset());
        });

        //  Commit the offset of the broker once the records are processed.
        consumer.commitAsync();
      } else {
        break;
      }
    }
  }
}
