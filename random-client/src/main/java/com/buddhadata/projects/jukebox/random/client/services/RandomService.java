/*
 * Copyright (c) 2019  Scott C. Sosna  ALL RIGHTS RESERVED
 *
 */

package com.buddhadata.projects.jukebox.random.client.services;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Interface defining the functionality exposed by the Random Service
 */
public interface RandomService {

  /**
   * getter
   * @return boolean from random service
   */
  @GET("boolean")
  Call<Boolean> getBoolean();

  /**
   * getter
   * @return double from random service
   */
  @GET("double")
  Call<Double> getDouble();

  /**
   * getter
   * @return gaussian from random service
   */
  @GET("gaussian")
  Call<Double> getGaussian();

  /**
   * getter
   * @return integer from random service
   */
  @GET("int")
  Call<Integer> getInteger();

  /**
   * getter
   * @param bound limit of integer to be returned
   * @return bound integer from random service
   */
  @GET("int/{bound}")
  Call<Integer> getIntegerBound(@Path("bound") int bound);

  /**
   * getter
   * @return long from random service
   */
  @GET("long")
  Call<Long> getLong();
}
