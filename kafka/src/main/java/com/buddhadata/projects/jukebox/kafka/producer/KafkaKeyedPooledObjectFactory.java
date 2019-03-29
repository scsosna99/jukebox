package com.buddhadata.projects.jukebox.kafka.producer;

import org.apache.commons.pool.BaseKeyedPoolableObjectFactory;
import org.apache.commons.pool.KeyedObjectPoolFactory;
import org.apache.commons.pool.KeyedPoolableObjectFactory;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Properties;

/**
 * Kafka consumer-specific factory for managing consumers in the pool
 */
public class KafkaKeyedPooledObjectFactory extends BaseKeyedPoolableObjectFactory<Properties,Consumer> {

  /**
   * Singleton instance of the factory, since it's stateless and can reused.
   */
  public static final KeyedPoolableObjectFactory<Properties,Consumer> INSTANCE = new KafkaKeyedPooledObjectFactory();

  /**
   * Private constructor to prevent a lot of instantiation.
   */
  private KafkaKeyedPooledObjectFactory() {
    return;
  }

  /**
   * Destroy the object currently existing in the pool
   * @param key key for the pooled object
   * @param value pooled object being destroyed
   */
  public void destroyObject (Properties key, Consumer value) {
    value.close();
  }

  /**
   * Create a Kafka consumer
   * @param key the set of properies used to construct the consumer
   * @return instantiated consumer
   */
  public Consumer<?,?> makeObject (Properties key) {
    return new KafkaConsumer<Object, Object>(key);
  }

}
