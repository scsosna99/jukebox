package com.buddhadata.projects.jukebox.subsonic.client;

import org.subsonic.restapi.SubsonicResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Service for Subsonic's sharing calls, as documented <a href="http://www.subsonic.org/pages/api.jsp">here</a>.
 *
 * @author Scott C Sosna
 */
public interface SharingService {

    /**
     * http://www.subsonic.org/pages/api.jsp#getShares
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of client making the call.
     * @return Subsonic response
     */
    @GET("getShares")
    Call<SubsonicResponse> getShares(@Query("u") String user,
                                     @Query("p") String password,
                                     @Query("v") String version,
                                     @Query("c") String client);

    /**
     * http://www.subsonic.org/pages/api.jsp#createShare
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of client making the call.
     * @param id ID of a song, album or video to share. Use one id parameter for each entry to share.
     * @param description A user-defined description that will be displayed to people visiting the shared media.
     * @param expires The time at which the share expires. Given as milliseconds since 1970.
     * @return Subsonic response
     */
    @GET("createShare")
    Call<SubsonicResponse> createShare(@Query("u") String user,
                                       @Query("p") String password,
                                       @Query("v") String version,
                                       @Query("c") String client,
                                       @Query("id") String id,
                                       @Query("description") String description,
                                       @Query("expires") Long expires);

    /**
     * http://www.subsonic.org/pages/api.jsp#updateShare
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of client making the call.
     * @param id ID of the share to update.
     * @param description A user-defined description that will be displayed to people visiting the shared media.
     * @param expires The time at which the share expires. Given as milliseconds since 1970, or zero to remove the expiration.
     * @return Subsonic response
     */
    @GET("updateShare")
    Call<SubsonicResponse> updateShare(@Query("u") String user,
                                       @Query("p") String password,
                                       @Query("v") String version,
                                       @Query("c") String client,
                                       @Query("id") String id,
                                       @Query("description") String description,
                                       @Query("expires") Long expires);

    /**
     * http://www.subsonic.org/pages/api.jsp#deleteShare
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of client making the call.
     * @param id ID of the share to delete
     * @return Subsonic response
     */
    @GET("deleteShare")
    Call<SubsonicResponse> deleteShare(@Query("u") String user,
                                       @Query("p") String password,
                                       @Query("v") String version,
                                       @Query("c") String client,
                                       @Query("id") String id);
}
