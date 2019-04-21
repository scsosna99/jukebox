/*
 * Copyright (c) 2019  Scott C. Sosna  ALL RIGHTS RESERVED
 *
 */

package com.buddhadata.projects.jukebox.kafka.consumer;

import com.buddhadata.projects.jukebox.kafka.producer.KafkaKeyedPooledObjectFactory;
import org.apache.commons.pool.KeyedObjectPool;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.*;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Create a consumer factory based on the parameters passed in.
 */
public enum ConsumerFactory {

  instance;

  /**
   * Pool of consumers, which can be reused when necessary
   */
  final KeyedObjectPool<Properties,Consumer> pool;

  /**
   * Cache of currently borrowed objects, which allows us to know the key used to create/get the consumer in the first place
   */
  final Map<Consumer,Properties> borrowed = new ConcurrentHashMap<>();

  private ConsumerFactory () {
    pool = new GenericKeyedObjectPool<Properties, Consumer>(KafkaKeyedPooledObjectFactory.INSTANCE);
  }

  /**
   * Get a consumer from the object pool
   * @param broker the broker hostname which to connect
   * @param groupId identifies the consumer uniquely
   * @param topicName the Kafka topic from which messages are being consumed
   * @param keyClazz the class for the key
   * @param valueClazz the class for the value
   * @return Kafka consumer
   */
  public Consumer<?, ?> get (String broker,
                             String groupId,
                             String topicName,
                             Class keyClazz,
                             Class valueClazz) {
    return get (broker, groupId, topicName, keyClazz, valueClazz, true);
  }
  /**
   * Get a consumer from the object pool
   * @param broker the broker hostname which to connect
   * @param groupId identifies the consumer uniquely
   * @param topicName the Kafka topic from which messages are being consumed
   * @param keyClazz the class for the key
   * @param valueClazz the class for the value
   * @param resetToEarliest should we get all back messages are only the ones going forward?
   * @return Kafka consumer
   */
  public Consumer<?, ?> get (String broker,
                             String groupId,
                             String topicName,
                             Class keyClazz,
                             Class valueClazz,
                             boolean resetToEarliest) {

    Consumer<?, ?> toReturn = null;

    try {
      //  Create properties that will be used for creating the consumer.
      Properties props = new Properties();
      props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, broker);
      props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
      props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, getDeserializerClass(keyClazz));
      props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, getDeserializerClass(valueClazz));
      props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
      props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, resetToEarliest ? "earliest" : "latest");

      //  Create the consumer and stick it in the cache of currently-borrowed objects
      toReturn = pool.borrowObject(props);
      borrowed.put(toReturn, props);

      //  Subscribe to the topic.
      toReturn.subscribe(Collections.singletonList(topicName));
    } catch (Exception e) {
      System.out.println ("something bad happened: " + e);
    }

    // return the consumer
    return toReturn;
  }

  /**
   * Return the object to the pool
   * @param value Kafka consumer
   */
  public void returnObject (Consumer<?,?> value) {
    try {
      //  Unsubscribe the consumer so it's not monitoring any topic or any other weirdness.
      value.unsubscribe();

      //  Return the object to the pool.  Because the key was generated originally, we'll get it out of the queue.
      Properties key = borrowed.remove(value);
      if (key != null) {
        pool.returnObject(key, value);
      }
    } catch (Exception e) {
      System.out.println ("Exception returning object: " + e);
    }
  }

  /**
   * Creates the correct Kafka deserializer
   * @param clazz the class for which a deserializer is needed
   * @return deserializer
   */
  private String getDeserializerClass(Class clazz) {

    if (clazz == Double.class) {
      return DoubleDeserializer.class.getName();
    } else if (clazz == Integer.class) {
      return IntegerDeserializer.class.getName();
    } else if (clazz == Long.class) {
      return LongDeserializer.class.getName();
    } else if (clazz == String.class) {
      return StringDeserializer.class.getName();
    } else if (clazz == ByteBuffer.class) {
      return ByteArrayDeserializer.class.getName();
    } else if (clazz == byte[].class) {
      return ByteBufferDeserializer.class.getName();
    }

    // this is really bad
    return null;
  }

}
