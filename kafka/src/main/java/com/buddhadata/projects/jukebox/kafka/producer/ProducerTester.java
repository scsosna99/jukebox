/*
 * Copyright (c) 2019  Scott C. Sosna  ALL RIGHTS RESERVED
 *
 */

package com.buddhadata.projects.jukebox.kafka.producer;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

/**
 * Just a crappy way to test the functionality provided by this module
 */
public class ProducerTester {

  public static void main (String[] args) {
    Producer<Long, String> producer = (Producer<Long, String>) ProducerFactory.instance.get("10.10.9.8:9092", "testingClient", Long.class, String.class);

    try {
      producer.send(new ProducerRecord<>("topictopictopic", 333L, "Hello Muther")).get();
    } catch (Throwable t) {
      System.out.println (t);
    }
  }
}
