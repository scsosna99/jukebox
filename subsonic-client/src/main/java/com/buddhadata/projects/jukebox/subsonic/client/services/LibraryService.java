package com.buddhadata.projects.jukebox.subsonic.client.services;

import org.subsonic.restapi.SubsonicResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Service for Subsonic's media library calls, as documented <a href="http://www.subsonic.org/pages/api.jsp">here</a>.
 *
 * @author Scott C Sosna
 */
public interface LibraryService {

    /**
     * http://www.subsonic.org/pages/api.jsp#getScanStatus
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of services making the call.
     * @return Subsonic response
     */
    @GET("getScanStatus")
    Call<SubsonicResponse> getScanStatus(@Query("u") String user,
                                         @Query("p") String password,
                                         @Query("v") String version,
                                         @Query("c") String client);

    /**
     * http://www.subsonic.org/pages/api.jsp#startScan
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of services making the call.
     * @return Subsonic response
     * @return
     */
    @GET("startScan")
    Call<SubsonicResponse> startScan(@Query("u") String user,
                                     @Query("p") String password,
                                     @Query("v") String version,
                                     @Query("c") String client);
}
