package com.buddhadata.projects.jukebox.subsonic.client.services;

import org.subsonic.restapi.SubsonicResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Service for Subsonic's media retrieval calls, as documented <a href="http://www.subsonic.org/pages/api.jsp">here</a>.
 *
 * @author Scott C Sosna
 */
public interface MediaRetrievalService {

    /**
     * http://www.subsonic.org/pages/api.jsp#stream
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of services making the call.
     * @param id A string which uniquely identifies the file to stream. Obtained by calls to getMusicDirectory.
     * @param maxBitRate If specified, the server will attempt to limit the bitrate to this value, in kilobits per second. If set to zero, no limit is imposed.
     * @param format Specifies the preferred target format (e.g., "mp3" or "flv") in case there are multiple applicable transcodings. Starting with 1.9.0 you can use the special value "raw" to disable transcoding.
     * @param seconds Only applicable to video streaming. If specified, start streaming at the given offset (in seconds) into the video. Typically used to implement video skipping.
     * @param size Only applicable to video streaming. Requested video size specified as WxH, for instance "640x480".
     * @param estimatedContentLength If set to "true", the Content-Length HTTP header will be set to an estimated value for transcoded or downsampled media.
     * @param converted Only applicable to video streaming. Subsonic can optimize videos for streaming by converting them to MP4. If a conversion exists for the video in question, then setting this parameter to "true" will cause the converted video to be returned instead of the original.
     * @return Subsonic response
     */
    @GET("stream")
    Call<SubsonicResponse> stream(@Query("u") String user,
                                  @Query("p") String password,
                                  @Query("v") String version,
                                  @Query("c") String client,
                                  @Query("id") String id,
                                  @Query("maxBitRate") Integer maxBitRate,
                                  @Query("format") String format,
                                  @Query("timeOffset") Integer seconds,
                                  @Query("size") String size,
                                  @Query("estimateContentLength") Boolean estimatedContentLength,
                                  @Query("converted") Boolean converted);

    /**
     * http://www.subsonic.org/pages/api.jsp#download
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of services making the call.
     * @param id A string which uniquely identifies the file to download. Obtained by calls to getMusicDirectory.
     * @return Subsonic response
     */
    @GET("download")
    Call<SubsonicResponse> download(@Query("u") String user,
                                    @Query("p") String password,
                                    @Query("v") String version,
                                    @Query("c") String client,
                                    @Query("id") String id);

    /**
     * http://www.subsonic.org/pages/api.jsp#hls
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of services making the call.
     * @param id A string which uniquely identifies the media file to stream.
     * @param bitRate If specified, the server will attempt to limit the bitrate to this value, in kilobits per second.
     * @param audioTrack The ID of the audio track to use. See getVideoInfo for how to get the list of available audio tracks for a video.
     * @return Subsonic response
     */
    @GET("hls")
    Call<SubsonicResponse> hls(@Query("u") String user,
                               @Query("p") String password,
                               @Query("v") String version,
                               @Query("c") String client,
                               @Query("id") String id,
                               @Query("bitRate") String bitRate,
                               @Query("audioTrack") String audioTrack);

    /**
     * http://www.subsonic.org/pages/api.jsp#getCaptions
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of services making the call.
     * @param id The ID of the video.
     * @param format Preferred captions format ("srt" or "vtt").
     * @return Subsonic response
     */
    @GET("getCaptions")
    Call<SubsonicResponse> getCaptions(@Query("u") String user,
                                       @Query("p") String password,
                                       @Query("v") String version,
                                       @Query("c") String client,
                                       @Query("id") String id,
                                       @Query("format") String format);

    /**
     * http://www.subsonic.org/pages/api.jsp#getCoverArt
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of services making the call.
     * @param id The ID of a song, album or artist.
     * @param size If specified, scale image to this size.
     * @return Subsonic response
     */
    @GET("getCoverArt")
    Call<SubsonicResponse> getCoverArt(@Query("u") String user,
                                       @Query("p") String password,
                                       @Query("v") String version,
                                       @Query("c") String client,
                                       @Query("id") String id,
                                       @Query("size") String size);

    /**
     * http://www.subsonic.org/pages/api.jsp#getLyrics
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of services making the call.
     * @param artist The artist name.
     * @param title The song title.
     * @return Subsonic response
     */
    @GET("getLyrics")
    Call<SubsonicResponse> getLyrics(@Query("u") String user,
                                     @Query("p") String password,
                                     @Query("v") String version,
                                     @Query("c") String client,
                                     @Query("artist") String artist,
                                     @Query("title") String title);

    /**
     * http://www.subsonic.org/pages/api.jsp#getAvatar
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of services making the call.
     * @param username The user in question.
     * @return Subsonic response
     */
    @GET("getAvatar")
    Call<SubsonicResponse> getAvatar(@Query("u") String user,
                                     @Query("p") String password,
                                     @Query("v") String version,
                                     @Query("c") String client,
                                     @Query("username") String username);
}
