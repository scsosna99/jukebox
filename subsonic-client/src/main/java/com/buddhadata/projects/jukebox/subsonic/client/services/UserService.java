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
 * Service for Subsonic's user management calls, as documented <a href="http://www.subsonic.org/pages/api.jsp">here</a>.
 *
 * @author Scott C Sosna
 */
public interface UserService {

    /**
     * http://www.subsonic.org/pages/api.jsp#getUser
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of services making the call.
     * @param getUsername The name of the user to retrieve. You can only retrieve your own user unless you have admin privileges.
     * @return Subsonic response
     */
    @GET("getUser")
    Call<SubsonicResponse> getUser(@Query("u") String user,
                                   @Query("p") String password,
                                   @Query("v") String version,
                                   @Query("c") String client,
                                   @Query("username") String getUsername);

    /**
     * http://www.subsonic.org/pages/api.jsp#getUsers
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of services making the call.
     * @return Subsonic response
     */
    @GET("getUsers")
    Call<SubsonicResponse> getUsers(@Query("u") String user,
                                    @Query("p") String password,
                                    @Query("v") String version,
                                    @Query("c") String client);

    /**
     * http://www.subsonic.org/pages/api.jsp#createUser
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of services making the call.
     * @param createdUsername The name of the user.
     * @param createdPassword The password of the user, either in clear text or hex-encoded.
     * @param email The email address of the user.
     * @param ldapAuthenticated Wheter the user is authenticated in LDAP.
     * @param adminRole Whether the user is administrator.
     * @param settingsRole Whether the user is allowed to change personal settings and password.
     * @param streamRole Whether the user is allowed to play files.
     * @param jukeboxRole Whether the user is allowed to play files in jukebox mode.
     * @param downloadRole Whether the user is allowed to download files.
     * @param uploadRole Whether the user is allowed to upload files.
     * @param coverArtRole Whether the user is allowed to change cover art and tags.
     * @param commentRole Whether the user is allowed to create and edit comments and ratings.
     * @param podcastRole Whether the user is allowed to administrate Podcasts.
     * @param shareRole Whether the user is allowed to share files with anyone.
     * @param videoConversionRole Whether the user is allowed to start video conversions.
     * @param musicFolderId IDs of the music folders the user is allowed access to. Include the parameter once for each folder.
     * @return Subsonic response
     */
    @GET("createUser")
    Call<SubsonicResponse> createUser(@Query("u") String user,
                                      @Query("p") String password,
                                      @Query("v") String version,
                                      @Query("c") String client,
                                      @Query("username") String createdUsername,
                                      @Query("password") String createdPassword,
                                      @Query("email") String email,
                                      @Query("ldapAuthenticated") Boolean ldapAuthenticated,
                                      @Query("adminRole") Boolean adminRole,
                                      @Query("settingsRole") Boolean settingsRole,
                                      @Query("streamRole") Boolean streamRole,
                                      @Query("jukeboxRole") Boolean jukeboxRole,
                                      @Query("downloadRole") Boolean downloadRole,
                                      @Query("uploadRole") Boolean uploadRole,
                                      @Query("coverArtRole") Boolean coverArtRole,
                                      @Query("commentRole") Boolean commentRole,
                                      @Query("podcastRole") Boolean podcastRole,
                                      @Query("shareRole") Boolean shareRole,
                                      @Query("videoConversionRole") Boolean videoConversionRole,
                                      @Query("musicFolderId") String musicFolderId);

    /**
     * http://www.subsonic.org/pages/api.jsp#updateUser
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of services making the call.
     * @param updatedUsername The name of the user.
     * @param updatedPassword The password of the user, either in clear text or hex-encoded.
     * @param email The email address of the user.
     * @param ldapAuthenticated Wheter the user is authenticated in LDAP.
     * @param adminRole Whether the user is administrator.
     * @param settingsRole Whether the user is allowed to change personal settings and password.
     * @param streamRole Whether the user is allowed to play files.
     * @param jukeboxRole Whether the user is allowed to play files in jukebox mode.
     * @param downloadRole Whether the user is allowed to download files.
     * @param uploadRole Whether the user is allowed to upload files.
     * @param coverArtRole Whether the user is allowed to change cover art and tags.
     * @param commentRole Whether the user is allowed to create and edit comments and ratings.
     * @param podcastRole Whether the user is allowed to administrate Podcasts.
     * @param shareRole Whether the user is allowed to share files with anyone.
     * @param videoConversionRole Whether the user is allowed to start video conversions.
     * @param musicFolderId IDs of the music folders the user is allowed access to. Include the parameter once for each folder.
     * @param maxBitRate The maximum bit rate (in Kbps) for the user. Audio streams of higher bit rates are automatically downsampled to this bit rate. Legal values: 0 (no limit), 32, 40, 48, 56, 64, 80, 96, 112, 128, 160, 192, 224, 256, 320.
     * @return Subsonic response
     */
    @GET("updateUser")
    Call<SubsonicResponse> updateUser(@Query("u") String user,
                                      @Query("p") String password,
                                      @Query("v") String version,
                                      @Query("c") String client,
                                      @Query("username") String updatedUsername,
                                      @Query("password") String updatedPassword,
                                      @Query("email") String email,
                                      @Query("ldapAuthenticated") Boolean ldapAuthenticated,
                                      @Query("adminRole") Boolean adminRole,
                                      @Query("settingsRole") Boolean settingsRole,
                                      @Query("streamRole") Boolean streamRole,
                                      @Query("jukeboxRole") Boolean jukeboxRole,
                                      @Query("downloadRole") Boolean downloadRole,
                                      @Query("uploadRole") Boolean uploadRole,
                                      @Query("coverArtRole") Boolean coverArtRole,
                                      @Query("commentRole") Boolean commentRole,
                                      @Query("podcastRole") Boolean podcastRole,
                                      @Query("shareRole") Boolean shareRole,
                                      @Query("videoConversionRole") Boolean videoConversionRole,
                                      @Query("musicFolderId") String musicFolderId,
                                      @Query("maxBitRate") Integer maxBitRate);

    /**
     * http://www.subsonic.org/pages/api.jsp#deleteUser
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of services making the call.
     * @param deleteUsername The name of the user to delete.
     * @return Subsonic response
     */
    @GET("deleteUser")
    Call<SubsonicResponse> deleteUser(@Query("u") String user,
                                      @Query("p") String password,
                                      @Query("v") String version,
                                      @Query("c") String client,
                                      @Query("username") String deleteUsername);

    /**
     * http://www.subsonic.org/pages/api.jsp#changePassword
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of services making the call.
     * @param changedUsername The name of the user which should change its password.
     * @param changedPassword The new password of the new user, either in clear text of hex-encoded
     * @return Subsonic response
     */
    @GET("changePassword")
    Call<SubsonicResponse> changePassword(@Query("u") String user,
                                          @Query("p") String password,
                                          @Query("v") String version,
                                          @Query("c") String client,
                                          @Query("username") String changedUsername,
                                          @Query("password") String changedPassword);
}
