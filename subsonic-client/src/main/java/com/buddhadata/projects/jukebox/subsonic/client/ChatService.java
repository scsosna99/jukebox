package com.buddhadata.projects.jukebox.subsonic.client;

import org.subsonic.restapi.SubsonicResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Service for Subsonic's chat calls, as documented <a href="http://www.subsonic.org/pages/api.jsp">here</a>.
 *
 * @author Scott C Sosna
 */
public interface ChatService {

    /**
     * http://www.subsonic.org/pages/api.jsp#getChatMessages
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of client making the call.
     * @param since Only return messages newer than this time (in millis since Jan 1 1970).
     * @return Subsonic response
     */
    @GET("getChatMessages")
    Call<SubsonicResponse> getChatMessages(@Query("u") String user,
                                           @Query("p") String password,
                                           @Query("v") String version,
                                           @Query("c") String client,
                                           @Query("since") long since);

    /**
     * http://www.subsonic.org/pages/api.jsp#addChatMessage
     * @param user authentication user name
     * @param password authentication password
     * @param version Subsonic API version
     * @param client name of client making the call.
     * @param message The chat message.
     * @return Subsonic response
     */
    @GET("addChatMessage")
    Call<SubsonicResponse> addChatMessage(@Query("u") String user,
                                          @Query("p") String password,
                                          @Query("v") String version,
                                          @Query("c") String client,
                                          @Query("message") String message);
}
