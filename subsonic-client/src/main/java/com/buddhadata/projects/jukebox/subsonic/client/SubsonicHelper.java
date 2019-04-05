package com.buddhadata.projects.jukebox.subsonic.client;

import org.subsonic.restapi.NowPlaying;
import org.subsonic.restapi.NowPlayingEntry;
import retrofit2.Retrofit;
import retrofit2.converter.jaxb.JaxbConverterFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.util.Comparator;
import java.util.Optional;

/**
 * Provides reusable functionality for accessing the Subsonic API.
 */
public enum SubsonicHelper {

  instance;

  /**
   * Same JAXB context can be used for all service interfaces created.
   */
  private JAXBContext context;

  /**
   * Constructor
   */
  private SubsonicHelper() {
    try {
      context = JAXBContext.newInstance("org.subsonic.restapi");
    } catch (JAXBException je) {
      System.out.println ("Error creating context:" + je);
    }
  }

  /**
   * Create Retrofit servces
   * @param subsonicHostName the host name to which we are connecting
   * @param type the class of the service interface
   * @return (hopefully) an instantiated service on which we can make calls.
   */
  public <T extends Object> T createService (String subsonicHostName,
                                             Class<T> type) {

    //  Create the retrofit builder specific to the host.  Could cache, but doubt it
    //  really will help much
    Retrofit retrofit = new Retrofit.Builder()
      .baseUrl("http://" + subsonicHostName + "/rest/")
      .addConverterFactory(JaxbConverterFactory.create(context))
      .build();

    //  Return the creted service
    return retrofit.create(type);
  }

  /**
   * Method that attempts to filter out the various players which are returned by the API call for
   * the jukebox specific player
   * @param allNowPlaying collection of now playing entries from the response of a Subsonic API call
   * @param clientName client name used for the Subsonic API calls
   * @param userName the Subsonic user name
   * @return option with or without the now playing for the player we care about
   */
  public Optional<NowPlayingEntry> getNowPlayingForPlayer (NowPlaying allNowPlaying,
                                                           String clientName,
                                                           String userName) {
    return allNowPlaying.getEntry().stream()
      .filter(one -> one.getUsername().equals(userName))
      .sorted(Comparator.comparingInt(NowPlayingEntry::getMinutesAgo))
      .filter(one -> one.getPlayerName().equals(clientName + "-" + userName))
      .findFirst();
  }
}
