package com.buddhadata.projects.jukebox.subsonic.client.services;

import org.subsonic.restapi.SubsonicResponse;
import retrofit2.Call;
import retrofit2.http.Query;

/**
 * Service for Subsonic's bookmark calls, as documented <a href="http://www.subsonic.org/pages/api.jsp">here</a>.
 *
 * @author Scott C Sosna
 */
public interface BookmarkService {

    /**
     * http://www.subsonic.org/pages/api.jsp#getBookmarks
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of services making the call.
     * @return Subsonic response
     */
    Call<SubsonicResponse> getBookmarks(@Query("u") String user,
                                        @Query("p") String password,
                                        @Query("v") String version,
                                        @Query("c") String client);

    /**
     * http://www.subsonic.org/pages/api.jsp#createBookmarks
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of services making the call.
     * @param id ID of the media file to bookmark
     * @param position the position (in milliseconds) within the media file
     * @return Subsonic response
     */
    Call<SubsonicResponse> createBookmark(@Query("u") String user,
                                          @Query("p") String password,
                                          @Query("v") String version,
                                          @Query("c") String client,
                                          @Query("id") String id,
                                          @Query("position") long position,
                                          @Query("comment") String comment);

    /**
     * http://www.subsonic.org/pages/api.jsp#deleteBookmark
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of services making the call.
     * @param id ID of the media file for which to delete the bookmark
     * @return Subsonic response
     */
    Call<SubsonicResponse> deleteBookmark(@Query("u") String user,
                                          @Query("p") String password,
                                          @Query("v") String version,
                                          @Query("c") String client,
                                          @Query("id") String id);

    /**
     * http://www.subsonic.org/pages/api.jsp#getPlayQueue
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of services making the call.
     * @return Subsonic response
     */
    Call<SubsonicResponse> getPlayQueue(@Query("u") String user,
                                        @Query("p") String password,
                                        @Query("v") String version,
                                        @Query("c") String client);

    /**
     * http://www.subsonic.org/pages/api.jsp#savePlayQueue
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of services making the call.
     * @param id ID of a song in the play queue. Use one id parameter for each song in the play queue.
     * @param current The ID of the current playing song.
     * @param position The position in milliseconds within the currently playing song.
     * @return Subsonic response
     */
    Call<SubsonicResponse> savePlayQueue(@Query("u") String user,
                                         @Query("p") String password,
                                         @Query("v") String version,
                                         @Query("c") String client,
                                         @Query("id") String id,
                                         @Query("current") String current,
                                         @Query("position") long position);
}
