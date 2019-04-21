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
 * Service for Subsonic's searching calls, as documented <a href="http://www.subsonic.org/pages/api.jsp">here</a>.
 *
 * @author Scott C Sosna
 */
public interface SearchingService {

    /**
     * http://www.subsonic.org/pages/api.jsp#search
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of services making the call.
     * @param artist Artist to search for.
     * @param album Album to search for.
     * @param title Title to search for.
     * @param any Searches all fields
     * @param count Maximum number of results to return.
     * @param offset Search result offset. Used for paging.
     * @param newerThan Only return matches that are newer than this. Given as milliseconds since 1970.
     * @return Subsonic result
     */
    @GET("search")
    Call<SubsonicResponse> search(@Query("u") String user,
                                  @Query("p") String password,
                                  @Query("v") String version,
                                  @Query("c") String client,
                                  @Query("artist") String artist,
                                  @Query("album") String album,
                                  @Query("title") String title,
                                  @Query("any") String any,
                                  @Query("count") Integer count,
                                  @Query("offset") Integer offset,
                                  @Query("newerThan") Long newerThan);

    /**
     * http://www.subsonic.org/pages/api.jsp#search2
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of services making the call.
     * @param query Search query.
     * @param artistCount Maximum number of artists to return.
     * @param artistOffset Search result offset for artists. Used for paging.
     * @param albumCount Maximum number of albums to return.
     * @param albumOffset Search result offset for albums. Used for paging.
     * @param songCount Maximum number of songs to return.
     * @param songOffset Search result offset for songs. Used for paging.
     * @param musicFolderId
     * @return Subsonic result
     */
    @GET("search2")
    Call<SubsonicResponse> search2(@Query("u") String user,
                                   @Query("p") String password,
                                   @Query("v") String version,
                                   @Query("c") String client,
                                   @Query("query") String query,
                                   @Query("artistCount") Integer artistCount,
                                   @Query("artistOffset")Integer artistOffset,
                                   @Query("albumCount") Integer albumCount,
                                   @Query("albumOffset")Integer albumOffset,
                                   @Query("songCount") Integer songCount,
                                   @Query("songOffset") Integer songOffset,
                                   @Query("musicFolderId") String musicFolderId);

    /**
     * http://www.subsonic.org/pages/api.jsp#search3
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of services making the call.
     * @param query Search query.
     * @param artistCount Maximum number of artists to return.
     * @param artistOffset Search result offset for artists. Used for paging.
     * @param albumCount Maximum number of albums to return.
     * @param albumOffset Search result offset for albums. Used for paging.
     * @param songCount Maximum number of songs to return.
     * @param songOffset Search result offset for songs. Used for paging.
     * @param musicFolderId
     * @return Subsonic result
     */
    @GET("search3")
    Call<SubsonicResponse> search3(@Query("u") String user,
                                   @Query("p") String password,
                                   @Query("v") String version,
                                   @Query("c") String client,
                                   @Query("query") String query,
                                   @Query("artistCount") Integer artistCount,
                                   @Query("artistOffset")Integer artistOffset,
                                   @Query("albumCount") Integer albumCount,
                                   @Query("albumOffset")Integer albumOffset,
                                   @Query("songCount") Integer songCount,
                                   @Query("songOffset") Integer songOffset,
                                   @Query("musicFolderId") String musicFolderId);

}
