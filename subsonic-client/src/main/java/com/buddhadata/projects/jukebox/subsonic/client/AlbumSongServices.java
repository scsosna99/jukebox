package com.buddhadata.projects.jukebox.subsonic.client;

import org.subsonic.restapi.SubsonicResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Service for Subsonic's calls for listing albums and songs, as documented <a href="http://www.subsonic.org/pages/api.jsp">here</a>.
 *
 * @author Scott C Sosna
 */
public interface AlbumSongServices {

    /**
     * http://www.subsonic.org/pages/api.jsp#getAlbumList
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of client making the call.
     * @param type The list type. Must be one of the following: random, newest, frequent, recent, starred, alphabeticalByName or alphabeticalByArtist.
     * @param size The number of albums to return. Max 500.
     * @param offset The list offset. Useful if you for example want to page through the list of newest albums.
     * @param fromYear The first year in the range. If fromYear > toYear a reverse chronological list is returned.
     * @param toYear The last year in the range.
     * @param genre The name of the genre, e.g., "Rock".
     * @param musicFolderId Only return albums in the music folder with the given ID. See getMusicFolders.
     * @return Subsonic response
     */
    @GET("getAlbumList")
    Call<SubsonicResponse> getAlbumList(@Query("u") String user,
                                        @Query("p") String password,
                                        @Query("v") String version,
                                        @Query("c") String client,
                                        @Query("type") String type,
                                        @Query("size") Integer size,
                                        @Query("offset") Integer offset,
                                        @Query("fromYear") Integer fromYear,
                                        @Query("toYear") Integer toYear,
                                        @Query("genre") String genre,
                                        @Query("musicFolderId") String musicFolderId);

    /**
     * http://www.subsonic.org/pages/api.jsp#getAlbumList2
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of client making the call.
     * @param type The list type. Must be one of the following: random, newest, frequent, recent, starred, alphabeticalByName or alphabeticalByArtist.
     * @param size The number of albums to return. Max 500.
     * @param offset The list offset. Useful if you for example want to page through the list of newest albums.
     * @param fromYear The first year in the range. If fromYear > toYear a reverse chronological list is returned.
     * @param toYear The last year in the range.
     * @param genre The name of the genre, e.g., "Rock".
     * @param musicFolderId Only return albums in the music folder with the given ID. See getMusicFolders.
     * @return Subsonic response
     */
    @GET("getAlbumList2")
    Call<SubsonicResponse> getAlbumList2(@Query("u") String user,
                                         @Query("p") String password,
                                         @Query("v") String version,
                                         @Query("c") String client,
                                         @Query("type") String type,
                                         @Query("size") Integer size,
                                         @Query("offset") Integer offset,
                                         @Query("fromYear") Integer fromYear,
                                         @Query("toYear") Integer toYear,
                                         @Query("genre") String genre,
                                         @Query("musicFolderId") String musicFolderId);

    /**
     * http://www.subsonic.org/pages/api.jsp#getRandomSongs
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of client making the call.
     * @param size The maximum number of songs to return. Max 500.
     * @param genre Only returns songs belonging to this genre.
     * @param fromYear Only return songs published after or in this year.
     * @param toYear Only return songs published before or in this year.
     * @param musicFolderId Only return songs in the music folder with the given ID.
     * @return Subsonic response
     */
    @GET("getRandomSongs")
    Call<SubsonicResponse> getRandomSongs(@Query("u") String user,
                                          @Query("p") String password,
                                          @Query("v") String version,
                                          @Query("c") String client,
                                          @Query("size") Integer size,
                                          @Query("genre") String genre,
                                          @Query("fromYear") Integer fromYear,
                                          @Query("toYear") Integer toYear,
                                          @Query("musicFolderId") String musicFolderId);

    /**
     * http://www.subsonic.org/pages/api.jsp#getSongsByGenre
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of client making the call.
     * @param genre The genre, as returned by getGenres.
     * @param count The maximum number of songs to return. Max 500.
     * @param offset The offset. Useful if you want to page through the songs in a genre.
     * @param musicFolderId Only return albums in the music folder with the given ID.
     * @return Subsonic response
     */
    @GET("getSongsByGenre")
    Call<SubsonicResponse> getSongsByGenre(@Query("u") String user,
                                           @Query("p") String password,
                                           @Query("v") String version,
                                           @Query("c") String client,
                                           @Query("genre") String genre,
                                           @Query("count") Integer count,
                                           @Query("offset") Integer offset,
                                           @Query("musicFolderId") String musicFolderId);

    /**
     * http://www.subsonic.org/pages/api.jsp#getNowPlaying
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of client making the call.
     * @return Subsonic response
     */
    @GET("getNowPlaying")
    Call<SubsonicResponse> getNowPlaying(@Query("u") String user,
                                         @Query("p") String password,
                                         @Query("v") String version,
                                         @Query("c") String client);

    /**
     * http://www.subsonic.org/pages/api.jsp#getStarred
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of client making the call.
     * @param musicFolderId Only return results from the music folder with the given ID.
     * @return Subsonic response
     */
    @GET("getStarred")
    Call<SubsonicResponse> getStarred(@Query("u") String user,
                                      @Query("p") String password,
                                      @Query("v") String version,
                                      @Query("c") String client,
                                      @Query("musicFolderId") String musicFolderId);

    /**
     * http://www.subsonic.org/pages/api.jsp#getStarred2
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of client making the call.
     * @param musicFolderId Only return results from the music folder with the given ID.
     * @return Subsonic response
     */
    @GET("getStarred2")
    Call<SubsonicResponse> getStarred2(@Query("u") String user,
                                       @Query("p") String password,
                                       @Query("v") String version,
                                       @Query("c") String client,
                                       @Query("musicFolderId") String musicFolderId);
}
