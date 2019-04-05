package com.buddhadata.projects.jukebox.subsonic.client.services;

import org.subsonic.restapi.SubsonicResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Service for Subsonic's media annotation calls, as documented <a href="http://www.subsonic.org/pages/api.jsp">here</a>.
 *
 * @author Scott C Sosna
 */
public interface MediaAnnotationService {

    /**
     * http://www.subsonic.org/pages/api.jsp#star
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of services making the call.
     * @param id The ID of the file (song) or folder (album/artist) to star. Multiple parameters allowed.
     * @param albumId The ID of an album to star. Use this rather than id if the services accesses the media collection according to ID3 tags rather than file structure. Multiple parameters allowed.
     * @param artistId The ID of an artist to star. Use this rather than id if the services accesses the media collection according to ID3 tags rather than file structure. Multiple parameters allowed.
     * @return Subsonic response
     */
    @GET("star")
    Call<SubsonicResponse> star(@Query("u") String user,
                                @Query("p") String password,
                                @Query("v") String version,
                                @Query("c") String client,
                                @Query("id") String id,
                                @Query("albumId") String albumId,
                                @Query("artistId") String artistId);

    /**
     * http://www.subsonic.org/pages/api.jsp#unstar
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of services making the call.
     * @param id The ID of the file (song) or folder (album/artist) to unstar. Multiple parameters allowed.
     * @param albumId The ID of an album to unstar. Use this rather than id if the services accesses the media collection according to ID3 tags rather than file structure. Multiple parameters allowed.
     * @param artistId The ID of an artist to unstar. Use this rather than id if the services accesses the media collection according to ID3 tags rather than file structure. Multiple parameters allowed.
     * @return Subsonic response
     */
    @GET("unstar")
    Call<SubsonicResponse> unstar(@Query("u") String user,
                                  @Query("p") String password,
                                  @Query("v") String version,
                                  @Query("c") String client,
                                  @Query("id") String id,
                                  @Query("albumId") String albumId,
                                  @Query("artistId") String artistId);

    /**
     * http://www.subsonic.org/pages/api.jsp#setRating
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of services making the call.
     * @param id A string which uniquely identifies the file (song) or folder (album/artist) to rate.
     * @param rating The rating between 1 and 5 (inclusive), or 0 to remove the rating.
     * @return Subsonic response
     */
    @GET("setRating")
    Call<SubsonicResponse> setRating(@Query("u") String user,
                                     @Query("p") String password,
                                     @Query("v") String version,
                                     @Query("c") String client,
                                     @Query("id") String id,
                                     @Query("rating") Integer rating);

    /**
     * http://www.subsonic.org/pages/api.jsp#scrobble
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of services making the call.
     * @param id A string which uniquely identifies the file to scrobble.
     * @param time The time (in milliseconds since 1 Jan 1970) at which the song was listened to.
     * @param submission Whether this is a "submission" or a "now playing" notification.
     * @return Subsonic response
     */
    @GET("scrobble")
    Call<SubsonicResponse> scrobble(@Query("u") String user,
                                    @Query("p") String password,
                                    @Query("v") String version,
                                    @Query("c") String client,
                                    @Query("id") String id,
                                    @Query("time") Long time,
                                    @Query("submission") Boolean submission);
}
