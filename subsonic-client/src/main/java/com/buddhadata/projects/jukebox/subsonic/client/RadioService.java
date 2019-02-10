package com.buddhadata.projects.jukebox.subsonic.client;

import org.subsonic.restapi.SubsonicResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Service for Subsonic's internet radio calls, as documented <a href="http://www.subsonic.org/pages/api.jsp">here</a>.
 *
 * @author Scott C Sosna
 */
public interface RadioService {

    /**
     * http://www.subsonic.org/pages/api.jsp#getInternetRadioStations
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of client making the call.
     * @return Subsonic response
     */
    @GET("getInternetRadioStations")
    Call<SubsonicResponse> getInternetRadioStations(@Query("u") String user,
                                                    @Query("p") String password,
                                                    @Query("v") String version,
                                                    @Query("c") String client);

    /**
     * http://www.subsonic.org/pages/api.jsp#createInternetRadioStation
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of client making the call.
     * @param streamUrl The stream URL for the station.
     * @param name The user-defined name for the station.
     * @param homepageUrl The home page URL for the station.
     * @return Subsonic response
     */
    @GET("createInternetRadioStation")
    Call<SubsonicResponse> createInternetRadioStation(@Query("u") String user,
                                                      @Query("p") String password,
                                                      @Query("v") String version,
                                                      @Query("c") String client,
                                                      @Query("streamUrl") String streamUrl,
                                                      @Query("name") String name,
                                                      @Query("homepageUrl") String homepageUrl);

    /**
     * http://www.subsonic.org/pages/api.jsp#updateInternetRadioStation
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of client making the call.
     * @param id The ID for the station.
     * @param streamUrl The stream URL for the station.
     * @param name The user-defined name for the station.
     * @param homepageUrl The home page URL for the station.
     * @return Subsonic response
     */
    @GET("updateInternetRadioStation")
    Call<SubsonicResponse> updateInternetRadioStation(@Query("u") String user,
                                                      @Query("p") String password,
                                                      @Query("v") String version,
                                                      @Query("c") String client,
                                                      @Query("id") String id,
                                                      @Query("streamUrl") String streamUrl,
                                                      @Query("name") String name,
                                                      @Query("homepageUrl") String homepageUrl);

    /**
     * http://www.subsonic.org/pages/api.jsp#deleteInternetRadioStation
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of client making the call.
     * @param id The ID for the station.
     * @return Subsonic response
     */
    @GET("deleteInternetRadioStation")
    Call<SubsonicResponse> deleteInternetRadioStation(@Query("u") String user,
                                                      @Query("p") String password,
                                                      @Query("v") String version,
                                                      @Query("c") String client,
                                                      @Query("id") String id);
}
