package com.buddhadata.projects.jukebox.subsonic.client.services;

import org.subsonic.restapi.SubsonicResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Service for Subsonic's browsing calls, as documented <a href="http://www.subsonic.org/pages/api.jsp">here</a>.
 *
 * @author Scott C Sosna
 */
public interface BrowsingService {

    /**
     * http://www.subsonic.org/pages/api.jsp#getMusicFolders
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of services making the call.
     * @return Subsonic response
     */
    @GET("getMusicFolders")
    Call<SubsonicResponse> getMusicFolders(@Query("u") String user,
                                           @Query("p") String password,
                                           @Query("v") String version,
                                           @Query("c") String client);

    /**
     * http://www.subsonic.org/pages/api.jsp#getIndexes
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of services making the call.
     * @param musicFolderId If specified, only return artists in the music folder with the given ID.
     * @param ifModifiedSince If specified, only return a result if the artist collection has changed since the given time (in milliseconds since 1 Jan 1970).
     * @return Subsonic response
     */
    @GET("getIndexes")
    Call<SubsonicResponse> getIndexes(@Query("u") String user,
                                      @Query("p") String password,
                                      @Query("v") String version,
                                      @Query("c") String client,
                                      @Query("musicFolderId") String musicFolderId,
                                      @Query("ifModifiedSince") Long ifModifiedSince);

    /**
     * http://www.subsonic.org/pages/api.jsp#getMusicDirectory
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of services making the call.
     * @param id A string which uniquely identifies the music folder. Obtained by calls to getIndexes or getMusicDirectory.
     * @return Subsonic response
     */
    @GET("getMusicDirectory")
    Call<SubsonicResponse> getMusicDirectory(@Query("u") String user,
                                             @Query("p") String password,
                                             @Query("v") String version,
                                             @Query("c") String client,
                                             @Query("id") String id);

    /**
     * http://www.subsonic.org/pages/api.jsp#getGenres
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of services making the call.
     * @return Subsonic response
     */
    @GET("getGenres")
    Call<SubsonicResponse> getGenres(@Query("u") String user,
                                     @Query("p") String password,
                                     @Query("v") String version,
                                     @Query("c") String client);

    /**
     * http://www.subsonic.org/pages/api.jsp#getArtists
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of services making the call.
     * @param musicFolderId If specified, only return artists in the music folder with the given ID.
     * @return Subsonic response
     */
    @GET("getArtists")
    Call<SubsonicResponse> getArtists(@Query("u") String user,
                                      @Query("p") String password,
                                      @Query("v") String version,
                                      @Query("c") String client,
                                      @Query("musicFolderId") String musicFolderId);

    /**
     * http://www.subsonic.org/pages/api.jsp#getArtist
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of services making the call.
     * @param id The artist ID.
     * @return Subsonic response
     */
    @GET("getArtist")
    Call<SubsonicResponse> getArtist(@Query("u") String user,
                                     @Query("p") String password,
                                     @Query("v") String version,
                                     @Query("c") String client,
                                     @Query("id") String id);

    /**
     * http://www.subsonic.org/pages/api.jsp#getAlbum
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of services making the call.
     * @param id The album ID.
     * @return Subsonic response
     */
    @GET("getAlbum")
    Call<SubsonicResponse> getAlbum(@Query("u") String user,
                                    @Query("p") String password,
                                    @Query("v") String version,
                                    @Query("c") String client,
                                    @Query("id") String id);

    /**
     * http://www.subsonic.org/pages/api.jsp#getSong
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of services making the call.
     * @param id The song ID.
     * @return Subsonic response
     */
    @GET("getSong")
    Call<SubsonicResponse> getSong(@Query("u") String user,
                                   @Query("p") String password,
                                   @Query("v") String version,
                                   @Query("c") String client,
                                   @Query("id") String id);

    /**
     * http://www.subsonic.org/pages/api.jsp#getVideos
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of services making the call.
     * @return Subsonic response
     */
    @GET("getVideo")
    Call<SubsonicResponse> getVideos(@Query("u") String user,
                                     @Query("p") String password,
                                     @Query("v") String version,
                                     @Query("c") String client);

    /**
     * http://www.subsonic.org/pages/api.jsp#getVideoInfo
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of services making the call.
     * @param id The video ID.
     * @return Subsonic response
     */
    @GET("getVideoInfo")
    Call<SubsonicResponse> getVideoInfo(@Query("u") String user,
                                        @Query("p") String password,
                                        @Query("v") String version,
                                        @Query("c") String client,
                                        @Query("id") String id);

    /**
     * http://www.subsonic.org/pages/api.jsp#getArtistInfo
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of services making the call.
     * @param id The artist, album or song ID.
     * @param count Max number of similar artists to return.
     * @param includeNotPresent Whether to return artists that are not present in the media library.
     * @return Subsonic response
     */
    @GET("getArtistInfo")
    Call<SubsonicResponse> getArtistInfo(@Query("u") String user,
                                         @Query("p") String password,
                                         @Query("v") String version,
                                         @Query("c") String client,
                                         @Query("id") String id,
                                         @Query("count") Integer count,
                                         @Query("includeNotPresent") Boolean includeNotPresent);

    /**
     * http://www.subsonic.org/pages/api.jsp#getArtistInfo2
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of services making the call.
     * @param id The artist ID.
     * @param count Max number of similar artists to return.
     * @param includeNotPresent Whether to return artists that are not present in the media library.
     * @return Subsonic response
     */
    @GET("getArtistInfo2")
    Call<SubsonicResponse> getArtistInfo2(@Query("u") String user,
                                          @Query("p") String password,
                                          @Query("v") String version,
                                          @Query("c") String client,
                                          @Query("id") String id,
                                          @Query("count") Integer count,
                                          @Query("includeNotPresent") Boolean includeNotPresent);

    /**
     * http://www.subsonic.org/pages/api.jsp#getAlbumInfo
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of services making the call.
     * @param id The album or song ID.
     * @return Subsonic response
     */
    @GET("getAlbumInfo")
    Call<SubsonicResponse> getAlbumInfo(@Query("u") String user,
                                        @Query("p") String password,
                                        @Query("v") String version,
                                        @Query("c") String client,
                                        @Query("id") String id);

    /**
     * http://www.subsonic.org/pages/api.jsp#getAlbumInfo2
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of services making the call.
     * @param id The album ID.
     * @return Subsonic response
     */
    @GET("getAlbumInfo2")
    Call<SubsonicResponse> getAlbumInfo2(@Query("u") String user,
                                         @Query("p") String password,
                                         @Query("v") String version,
                                         @Query("c") String client,
                                         @Query("id") String id);

    /**
     * http://www.subsonic.org/pages/api.jsp#getSimilarSongs
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of services making the call.
     * @param id The artist, album, or song ID.
     * @param count Max number of songs to return.
     * @return Subsonic response
     */
    @GET("getSimilarSongs")
    Call<SubsonicResponse> getSimilarSongs(@Query("u") String user,
                                           @Query("p") String password,
                                           @Query("v") String version,
                                           @Query("c") String client,
                                           @Query("id") String id,
                                           @Query("count") Integer count);

    /**
     * http://www.subsonic.org/pages/api.jsp#getSimilarSongs2
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of services making the call.
     * @param id The artist ID.
     * @param count Max number of songs to return.
     * @return Subsonic response
     */
    @GET("getSimilarSongs2")
    Call<SubsonicResponse> getSimilarSongs2(@Query("u") String user,
                                            @Query("p") String password,
                                            @Query("v") String version,
                                            @Query("c") String client,
                                            @Query("id") String id,
                                            @Query("count") Integer count);

    /**
     * http://www.subsonic.org/pages/api.jsp#getTopSongs
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of services making the call.
     * @param artist The artist name
     * @param count Max number of songs to return.
     * @return Subsonic response
     */
    @GET("getTopSongs")
    Call<SubsonicResponse> getTopSongs(@Query("u") String user,
                                       @Query("p") String password,
                                       @Query("v") String version,
                                       @Query("c") String client,
                                       @Query("artist") String artist,
                                       @Query("count") Integer count);
}
