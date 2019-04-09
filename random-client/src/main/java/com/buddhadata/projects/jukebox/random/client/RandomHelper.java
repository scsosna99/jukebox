package com.buddhadata.projects.jukebox.random.client;

import com.buddhadata.projects.jukebox.random.client.services.RandomService;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import java.io.IOException;

/**
 * Simplify working with random number generator.
 */
public enum RandomHelper {

  instance;

  /**
   * Create Retrofit servces
   * @param hostNameAndPort the host name to which we are connecting with a port, e.g. myhost:8080
   * @param type the service interface being instantiated
   * @return (hopefully) an instantiated service on which we can make calls.
   */
  public <T extends Object> T createService (String hostNameAndPort,
                                             Class<T> type) {

    //  Create the retrofit builder specific to the host.  Could cache, but doubt it
    //  really will help much
    Retrofit retrofit = new Retrofit.Builder()
      .baseUrl("http://" + hostNameAndPort + "/random/")
      .addConverterFactory(ScalarsConverterFactory.create())
      .build();

    //  Return the creted service
    return retrofit.create(type);
  }

  /**
   * Quick test method, just substitute your hostname and port.
   * @param args
   */
  public final static void main (String[] args) {
    RandomService r = RandomHelper.instance.createService ("10.10.9.27:8000", RandomService.class);
    try {
      System.out.println(r.getIntegerBound(100).execute().body());
      System.out.println(r.getBoolean().execute().body());
    } catch (IOException ioe) {
      System.out.println (ioe);
    }
  }
}
