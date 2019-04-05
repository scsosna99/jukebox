package com.buddhadata.projects.jukebox.subsonic.client.services;

import org.subsonic.restapi.SubsonicResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Service for Subsonic's jukebox calls, as documented <a href="http://www.subsonic.org/pages/api.jsp">here</a>.
 *
 * @author Scott C Sosna
 */
public interface JukeboxService {

    /**
     * http://www.subsonic.org/pages/api.jsp#jukeboxControl
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of services making the call.
     * @param action The operation to perform. Must be one of: get, status, set, start, stop, skip, add, clear, remove, shuffle, setGain
     * @param index Used by skip and remove. Zero-based index of the song to skip to or remove.
     * @param offset Used by skip. Start playing this many seconds into the track.
     * @param id Used by add and set. ID of song to add to the jukebox playlist. Use multiple id parameters to add many songs in the same request.
     * @param gain Used by setGain to control the playback volume. A float value between 0.0 and 1.0.
     * @return Subsonic response
     */
    @GET("jukeboxControl")
    Call<SubsonicResponse> jukeboxControl (@Query("u") String user,
                                           @Query("p") String password,
                                           @Query("v") String version,
                                           @Query("c") String client,
                                           @Query("action") String action,
                                           @Query("index") Integer index,
                                           @Query("offset") Integer offset,
                                           @Query("id") String id,
                                           @Query("gain") Double gain);
}
