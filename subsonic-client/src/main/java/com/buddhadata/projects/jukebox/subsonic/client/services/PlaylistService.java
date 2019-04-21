/*
 * Copyright (c) 2019  Scott C. Sosna  ALL RIGHTS RESERVED
 *
 */

package com.buddhadata.projects.jukebox.subsonic.client.services;

import org.subsonic.restapi.SubsonicResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Service for Subsonic's playlist calls, as documented <a href="http://www.subsonic.org/pages/api.jsp">here</a>.
 *
 * @author Scott C Sosna
 */
public interface PlaylistService {

    /**
     * http://www.subsonic.org/pages/api.jsp#getPlaylists
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of services making the call.
     * @param username If specified, return playlists for this user rather than for the authenticated user. The authenticated user must have admin role if this parameter is used.
     * @return Subsonic response
     */
    @GET("getPlaylists")
    Call<SubsonicResponse> getPlaylists(@Query("u") String user,
                                        @Query("p") String password,
                                        @Query("v") String version,
                                        @Query("c") String client,
                                        @Query("username") String username);

    /**
     * http://www.subsonic.org/pages/api.jsp#getPlaylist
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of services making the call.
     * @param id ID of the playlist to return, as obtained by getPlaylists.
     * @return Subsonic response
     */
    @GET("getPlaylist")
    Call<SubsonicResponse> getPlaylist(@Query("u") String user,
                                       @Query("p") String password,
                                       @Query("v") String version,
                                       @Query("c") String client,
                                       @Query("id") String id);

    /**
     * http://www.subsonic.org/pages/api.jsp#createPlaylist
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of services making the call.
     * @param playlistId The playlist ID.
     * @param name The human-readable name of the playlist.
     * @param songId ID of a song in the playlist. Use one songId parameter for each song in the playlist.
     * @return Subsonic response
     */
    @GET("createPlaylist")
    Call<SubsonicResponse> createPlaylist(@Query("u") String user,
                                          @Query("p") String password,
                                          @Query("v") String version,
                                          @Query("c") String client,
                                          @Query("playlistId") String playlistId,
                                          @Query("name") String name,
                                          @Query("songId") String songId);

    /**
     * http://www.subsonic.org/pages/api.jsp#updatePlaylist
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of services making the call.
     * @param playlistId The playlist ID.
     * @param name The human-readable name of the playlist.
     * @param comment The playlist comment.
     * @param publicFlag true if the playlist should be visible to all users, false otherwise.
     * @param songToAdd Add this song with this ID to the playlist. Multiple parameters allowed.
     * @param songIndexToRemove Remove the song at this position in the playlist. Multiple parameters allowed.=
     * @return Subsonic response
     */
    @GET("updatePlaylist")
    Call<SubsonicResponse> updatePlaylist(@Query("u") String user,
                                          @Query("p") String password,
                                          @Query("v") String version,
                                          @Query("c") String client,
                                          @Query("playlistId") String playlistId,
                                          @Query("name") String name,
                                          @Query("comment") String comment,
                                          @Query("public") Boolean publicFlag,
                                          @Query("songToAdd") String songToAdd,
                                          @Query("songIndexToRemove") Integer songIndexToRemove);

    /**
     * http://www.subsonic.org/pages/api.jsp#deletePlaylist
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of services making the call.
     * @param id ID of the playlist to delete, as obtained by getPlaylists.
     * @return Subsonic response
     */
    @GET("deletePlaylist")
    Call<SubsonicResponse> deletePlaylist(@Query("u") String user,
                                          @Query("p") String password,
                                          @Query("v") String version,
                                          @Query("c") String client,
                                          @Query("id") String id);


}
