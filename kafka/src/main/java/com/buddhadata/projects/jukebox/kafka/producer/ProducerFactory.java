package com.buddhadata.projects.jukebox.kafka.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.*;

import java.nio.ByteBuffer;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * As needed, create new Kafka producers or use existing ones.  Kafka producers are thread-safe so don't need to create
 * new ones for every unique producer
 */
public enum ProducerFactory {
  instance;

  /**
   * Cache of producers that can be reused as similar requests come in.
   */
  private final ConcurrentMap<Properties, Producer<? extends Deserializer, ? extends Deserializer>> producers = new ConcurrentHashMap<>();

  /**
   * Get a producer for the caller, which may be created or may be new.
   * @param broker The Kafka broker to which message is sent
   * @param clientId uniquely identifies the client requesting a producer
   * @param keyClazz class of the message key
   * @param valueClazz class of the message value
   * @return
   */
  public Producer<?, ?> get (String broker,
                             String clientId,
                             Class keyClazz,
                             Class valueClazz) {

    //  Create the properties collections for the producer.
    Properties props = new Properties();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, broker);
    props.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, getSerializerClass(keyClazz));
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, getSerializerClass(valueClazz));

    //  Check the producers and pass in the lambda to create if not already exists.
    return producers.computeIfAbsent(props, (k) -> new KafkaProducer<Deserializer, Deserializer>(k));
  }

  /**
   * Creates the correct Kafka serializer
   * @param clazz the class for which a serializer is needed
   * @return serializer
   */
  private String getSerializerClass(Class clazz) {

    if (clazz == Double.class) {
      return DoubleSerializer.class.getName();
    } else if (clazz == Integer.class) {
      return IntegerSerializer.class.getName();
    } else if (clazz == Long.class) {
      return LongSerializer.class.getName();
    } else if (clazz == String.class) {
      return StringSerializer.class.getName();
    } else if (clazz == ByteBuffer.class) {
      return ByteArraySerializer.class.getName();
    } else if (clazz == byte[].class) {
      return ByteBufferSerializer.class.getName();
    }

    // this is really bad
    return null;
  }
}
