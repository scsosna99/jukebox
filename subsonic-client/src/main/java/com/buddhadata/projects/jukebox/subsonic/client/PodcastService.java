package com.buddhadata.projects.jukebox.subsonic.client;

import org.subsonic.restapi.SubsonicResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Service for Subsonic's podcast calls, as documented <a href="http://www.subsonic.org/pages/api.jsp">here</a>.
 *
 * @author Scott C Sosna
 */
public interface PodcastService {

    /**
     * http://www.subsonic.org/pages/api.jsp#getPodcasts
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of client making the call.
     * @param includeEpisodes Whether to include Podcast episodes in the returned result.
     * @param id If specified, only return the Podcast channel with this ID.
     * @return Subsonic response
     */
    @GET("getPodcasts")
    Call<SubsonicResponse> getPodcasts(@Query("u") String user,
                                       @Query("p") String password,
                                       @Query("v") String version,
                                       @Query("c") String client,
                                       @Query("includeEpisodes") Boolean includeEpisodes,
                                       @Query("id") String id);

    /**
     * http://www.subsonic.org/pages/api.jsp#getNewestPodcasts
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of client making the call.
     * @param count The maximum number of episodes to return.
     * @return Subsonic response
     */
    @GET("getNewestPodcasts")
    Call<SubsonicResponse> getNewestPodcasts(@Query("u") String user,
                                             @Query("p") String password,
                                             @Query("v") String version,
                                             @Query("c") String client,
                                             @Query("count") Integer count);

    /**
     * http://www.subsonic.org/pages/api.jsp#refreshPodcasts
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of client making the call.
     * @return Subsonic response
     */
    @GET("refreshPodcasts")
    Call<SubsonicResponse> refreshPodcasts(@Query("u") String user,
                                           @Query("p") String password,
                                           @Query("v") String version,
                                           @Query("c") String client);

    /**
     * http://www.subsonic.org/pages/api.jsp#createPodcastChannel
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of client making the call.
     * @param url The URL of the Podcast to add
     * @return Subsonic response
     */
    @GET("createPodcastChannel")
    Call<SubsonicResponse> createPodcastChannel(@Query("u") String user,
                                                @Query("p") String password,
                                                @Query("v") String version,
                                                @Query("c") String client,
                                                @Query("url") String url);

    /**
     * http://www.subsonic.org/pages/api.jsp#deletePodcastChannel
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of client making the call.
     * @param id The ID of the Podcast channel to delete.
     * @return Subsonic response
     */
    @GET("deletePodcastChannel")
    Call<SubsonicResponse> deletePodcastChannel(@Query("u") String user,
                                                @Query("p") String password,
                                                @Query("v") String version,
                                                @Query("c") String client,
                                                @Query("id") String id);

    /**
     * http://www.subsonic.org/pages/api.jsp#deletePodcastEpisode
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of client making the call.
     * @param id The ID of the Podcast episode to delete.
     * @return Subsonic response
     */
    @GET("deletePodcastEpisode")
    Call<SubsonicResponse> deletePodcastEpisode(@Query("u") String user,
                                                @Query("p") String password,
                                                @Query("v") String version,
                                                @Query("c") String client,
                                                @Query("id") String id);

    /**
     * http://www.subsonic.org/pages/api.jsp#downloadPodcastEpisode
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of client making the call.
     * @param id The ID of the Podcast episode to download.
     * @return Subsonic response
     */
    @GET("downloadPodcastEpisode")
    Call<SubsonicResponse> downloadPodcastEpisode(@Query("u") String user,
                                                  @Query("p") String password,
                                                  @Query("v") String version,
                                                  @Query("c") String client,
                                                  @Query("id") String id);
}
