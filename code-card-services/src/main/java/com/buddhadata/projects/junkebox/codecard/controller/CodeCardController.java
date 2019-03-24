package com.buddhadata.projects.junkebox.codecard.controller;

import com.buddhadata.projects.jukebox.subsonic.client.AlbumSongServices;
import com.buddhadata.projects.jukebox.subsonic.client.JukeboxService;
import com.buddhadata.projects.junkebox.codecard.messages.CodeCardResponse;
import com.buddhadata.projects.junkebox.codecard.messages.enums.Background;
import com.buddhadata.projects.junkebox.codecard.messages.enums.BackgroundColor;
import com.buddhadata.projects.junkebox.codecard.messages.enums.Icon;
import com.buddhadata.projects.junkebox.codecard.messages.enums.Template;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.subsonic.restapi.JukeboxStatus;
import org.subsonic.restapi.NowPlayingEntry;
import org.subsonic.restapi.SubsonicResponse;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jaxb.JaxbConverterFactory;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Controller with RESTful calls for actions triggerd by pressing buttons on the Oracle Code Card.
 */

@RestController
@RequestMapping("codecard")
public class CodeCardController {

    /**
     * Retrofit service for making calls to the Subsonic AlbumSong API
     */
    private AlbumSongServices album;

    /**
     * Retrofit service for making calls to the Subsonic Jukebox API
     */
    private JukeboxService jukebox;

    /**
     * Global Retrofit instance from which individual services will be created.
     */
    private Retrofit retrofit = null;

    /**
     * The constructed/expected player name, assuming everything goes well.
     */
    private String playerName;

    /**
     * Client name to use when connecting to subsonic.
     */
    @Value("${jukebox.clientname:unknown}")
    private String subsonicClientName;

    /**
     * Host name or IP address for subsonic server
     */
    @Value("${jukebox.hostname:localhost}")
    private String subsonicHostName;

    /**
     * Password for authenticating to Subsonic server
     */
    @Value("${jukebox.credentials.password:admin}")
    private String subsonicPassword;

    /**
     * User name for authenticating to Subsonic server
     */
    @Value("${jukebox.credentials.username:admin}")
    private String subsonicUsername;

    /**
     * If nothing specified in RESTful call, how many songs are randomly retrieved.
     */
    @Value("${jukebox.randomcnt:20}")
    private int DEFAULT_RANDOM_SONG_COUNT;

    /**
     * If played over this many seconds, a 'back' causes the song to restart; otherwise go to previous song.
     */
    private final int MIN_SECOND_BEFORE_GOING_BACK_TO_SONG_START = 5;


    /**
     * Skip to the next playing song
     * @return
     */
    @GetMapping("/back")
    public CodeCardResponse back() {

        CodeCardResponse toReturn;
        try {
            //  First, get the status so we know index of current song.
            JukeboxStatus status = jukeboxStatus();
            if (status != null) {
                Response<SubsonicResponse> response;
                //  If we're just starting a song, we'll go backward otherwise restart the current song.
                if (status.getCurrentIndex() == 0  || status.getPosition() >= MIN_SECOND_BEFORE_GOING_BACK_TO_SONG_START) {
                    response = jukeboxAction("skip", status.getCurrentIndex(), null, null, null);
                } else {
                    response = jukeboxAction("skip", status.getCurrentIndex() - 1, null, null, null);
                }

                if (response.isSuccessful()) {
                    toReturn = nowPlaying();
                    toReturn.setTitle ("Back to previous song");
                } else {
                    toReturn = createResponse(Template.template1, "Unable to skip songs", null, response.errorBody().toString(), Icon.fail, Background.code, BackgroundColor.black);
                }
            } else {
                toReturn = createResponse(Template.template1, "Unable to skip songs", null, "Unable to determine current index", Icon.fail, Background.code, BackgroundColor.black);            }
        } catch (IOException ioe) {
            System.out.println ("Exception attempting to skip song: " + ioe);
            toReturn = createResponse(Template.template1, "Exception!", "Skipping song", ioe.toString(), Icon.fail,
              Background.code, BackgroundColor.black);
        }


        return toReturn;
    }

    @GetMapping("/clear")
    public CodeCardResponse clearPlaylist() {

        CodeCardResponse toReturn;
        try {
            Response<SubsonicResponse> response =
              jukebox.jukeboxControl(subsonicUsername, subsonicPassword, "1.16.0", subsonicClientName, "get", null, null, null, null).execute();
            if (!response.body().getJukeboxPlaylist().getEntry().isEmpty()) {
                for (int i = 0; i < response.body().getJukeboxPlaylist().getEntry().size(); i++) {
                    jukebox.jukeboxControl(subsonicUsername, subsonicPassword, "1.16.0", subsonicClientName, "remove", 0, null, null, null).execute();
                }
            }
            toReturn = createResponse (Template.template1, "Playlist Cleared", null, "All songs deleted from jukebox playlist", Icon.ace,
              Background.code, BackgroundColor.black);
        } catch (IOException ioe) {
            //  Something really bad happened.
            System.out.println ("Exception occurred making call to Subsonic: " + ioe);
            toReturn = createResponse(Template.template1, "Exception!", "Jukebox.get/remove", ioe.toString(), Icon.fail,
              Background.code, BackgroundColor.black);
        }


        return toReturn;
    }

    /**
     * Mute or umute the jukebox, depending on its current status.
     * @return CodeCard response
     */
    @GetMapping("/muteUnmute")
    public CodeCardResponse muteUnmute() {

        CodeCardResponse toReturn;
        try {
            //  Get the current status of the jukebox, which includes the gain.
            JukeboxStatus status = jukeboxStatus();
            if (status != null) {

                //  Determine what the new gain should be
                Double gain = (status.getGain() > 0.0f) ? 0.0 : 1.0;

                Response<SubsonicResponse> response = jukeboxAction("setGain", null, null, null, gain);
                if (response.isSuccessful()) {
                    toReturn = createResponse(Template.template11, "Volume Changed", null, "New Volume Gain is " + gain, Icon.ace, Background.code, BackgroundColor.white);
                } else {
                    toReturn = createResponse(Template.template11, "Error Muting Volume", null, response.errorBody().toString(), Icon.fail, Background.code, BackgroundColor.black);
                }
            } else {
                toReturn = createResponse(Template.template11, "Error Muting Volume", null, "Unable to get status", Icon.fail, Background.code, BackgroundColor.black);
            }
        } catch (IOException ioe) {
            System.out.println ("Exception muting volume: " + ioe);
            toReturn = createResponse (Template.template11, "Exception Muting Volume", null, ioe.getMessage(), Icon.fail, Background.code, BackgroundColor.black);
        }


        return toReturn;
    }

    /**
     * What's the current song playing on the jukebox?
     * @return CodeCard response object
     */
    @GetMapping("/playing")
    public CodeCardResponse nowPlaying() {

        CodeCardResponse toReturn;
        try {
            Response<SubsonicResponse> response =
              album.getNowPlaying(subsonicUsername, subsonicPassword, "1.16.0", subsonicClientName).execute();
            if (response.isSuccessful()) {

                //  Filter for what we expect to be the player name.
                Optional<NowPlayingEntry> player = response.body().getNowPlaying().getEntry().stream()
                  .filter(one -> one.getUsername().equals(subsonicUsername))
                  .sorted(Comparator.comparingInt(NowPlayingEntry::getMinutesAgo))
                  .filter(one -> one.getPlayerName().equals(playerName))
                  .findFirst();
                if (player.isPresent()) {
                    NowPlayingEntry entry = player.get();
                    toReturn = createResponse(Template.template1, "Now Playing", "from \"" + entry.getAlbum() + "\"", entry.getTitle() + " by " + entry.getArtist(),
                      Icon.duke, Background.code, BackgroundColor.white);
                } else {
                    toReturn = createResponse(Template.template1, "Now Playing", null, "Player not found",
                      Icon.fail, Background.code, BackgroundColor.black);
                }
            } else {
                toReturn = createResponse(Template.template1, "Jukebox Not Started!", null, "AlbumUser service returned error " + response.errorBody(),
                  Icon.fail, Background.code, BackgroundColor.black);
            }
        } catch (IOException ioe) {
            //  Something really bad happened.
            System.out.println ("Exception occurred making call to Subsonic: " + ioe);
            toReturn = createResponse(Template.template1, "Exception!", "AlbumUser.nowPlaying", ioe.toString(), Icon.fail,
              Background.code, BackgroundColor.black);
        }


        return toReturn;
    }


    /**
     * Add random songs to the jukebox playlist
     * @param genre the genre to filter
     * @param fromYear only include songs more recent than this year
     * @param toYear only  include songs older than this yera
     * @param count how many songs to add
     * @return CodeCard response object
     */
    @GetMapping("/random")
    public CodeCardResponse addRandom(@RequestParam(value = "genre", required = false) String genre,
                                      @RequestParam(value = "from", required = false) Integer fromYear,
                                      @RequestParam(value = "to", required = false) Integer toYear,
                                      @RequestParam(value = "count", required = false) Integer count) {

        CodeCardResponse toReturn;
        try {
            final AtomicInteger added = new AtomicInteger(0);
            album.getRandomSongs("jukebox", "digital99", "1.16.0", "digital-jukebox", count != null ? count : DEFAULT_RANDOM_SONG_COUNT, genre, fromYear, toYear, null)
              .execute()
              .body()
              .getRandomSongs()
              .getSong()
              .forEach (one -> {
                  try {
                      Response<SubsonicResponse> response = jukeboxAction ("add", null, null, one.getId(), null);
                      if (response.isSuccessful()) {
                          added.addAndGet(1);
                      } else {
                          System.out.println ("Failure to add song: " + response.errorBody());
                      }
                  } catch (Exception e) {
                      System.out.println ("Exception occurred making call to Subsonic: " + e);
                  }
              });

            //  Did we actually add anything?
            int addedCount = added.get();
            if (addedCount > 0) {
                toReturn = createResponse (Template.template1, "Playlist", "Random Songs Added", addedCount + " songs added to jukebox playlist.", Icon.ace,
                  Background.code, BackgroundColor.white);
            } else {
                toReturn = createResponse (Template.template1, "Playlist", "No Random Songs Added", "Nothing added though tried really hard!", Icon.fail,
                  Background.code, BackgroundColor.black);
            }
        } catch (IOException ioe) {
            //  Something really bad happened.
            System.out.println ("Exception occurred making call to Subsonic: " + ioe);
            toReturn = createResponse(Template.template1, "Exception!", "Jukebox.get/remove", ioe.toString(), Icon.fail,
              Background.code, BackgroundColor.black);
        }


        return toReturn;
    }

    /**
     * Skip to the next playing song
     * @return
     */
    @GetMapping("/next")
    public CodeCardResponse next() {

        CodeCardResponse toReturn;
        try {
            //  First, get the status so we know index of current song.
            JukeboxStatus status = jukeboxStatus();
            if (status != null) {
                Response<SubsonicResponse> response = jukeboxAction("skip", status.getCurrentIndex() + 1, null, null, null);
                if (response.isSuccessful()) {
                    toReturn = nowPlaying();
                    toReturn.setTitle ("Skipped to next song");
                } else {
                    toReturn = createResponse(Template.template1, "Unable to skip songs", null, response.errorBody().toString(), Icon.fail, Background.code, BackgroundColor.black);
                }
            } else {
                toReturn = createResponse(Template.template1, "Unable to skip songs", null, "Unable to determine current index", Icon.fail, Background.code, BackgroundColor.black);            }
        } catch (IOException ioe) {
            System.out.println ("Exception attempting to skip song: " + ioe);
            toReturn = createResponse(Template.template1, "Exception!", "Skipping song", ioe.toString(), Icon.fail,
              Background.code, BackgroundColor.black);
        }


        return toReturn;

    }

    /**
     * Flip the playing mode, either stop a playing jukebox or start a stopped jukebox.
     * @return CodeCard response object.
     */
    @GetMapping("/startStop")
    public CodeCardResponse startStopJukebox() {

        CodeCardResponse toReturn;
        try {
            //  Determine current state of jukebox.
            JukeboxStatus status = jukeboxStatus();
            if (status == null || !status.isPlaying()) {
                toReturn = startJukebox();
            } else {
                toReturn = stopJukebox();
            }
        } catch (IOException ioe) {
            //  Something really bad happened.
            System.out.println ("Exception occurred making call to Subsonic: " + ioe);
            toReturn = createResponse(Template.template1, "Exception!", "JukeboxService.start", ioe.toString(), Icon.fail,
              Background.code, BackgroundColor.black);
        }


        return toReturn;
    }

    /**
     * Start the jukebox playing
     * @return CodeCard response object
     */
    private CodeCardResponse startJukebox() throws IOException {

        CodeCardResponse toReturn;
        //  Attempt to make the call to the Subsonic server
        Response<SubsonicResponse> response = jukeboxAction("start", 0, null, null, null);

        //  Did we succeed or not?
        if (response.isSuccessful()) {
            toReturn = createResponse(Template.template1, "Jukebox Started!", null, "Subsonic playback successfully started",
              Icon.champion, Background.code, BackgroundColor.white);
        } else {
            toReturn = createResponse(Template.template1, "Jukebox Not Started!", null, "Jukebox service returned error " + response.errorBody(),
              Icon.fail, Background.code, BackgroundColor.black);
        }


        return toReturn;
    }

    /**
     * stop the jukebox from playing
     * @return COdeCard response object
     */
    private CodeCardResponse stopJukebox() throws IOException {

        CodeCardResponse toReturn;
        Response<SubsonicResponse> response = jukeboxAction("stop", null, null, null, null);

        //  Did we succeed or not?
        if (response.isSuccessful()) {
            toReturn = createResponse(Template.template1, "Jukebox Stopped!", null, "Subsonic playback successfully stopped",
              Icon.champion, Background.code, BackgroundColor.white);
        } else {
            toReturn = createResponse(Template.template1, "Jukebox Not Stopped!", null, "Jukebox service returned error " + response.errorBody(),
              Icon.fail, Background.code, BackgroundColor.black);
        }


        return toReturn;
    }

    /**
     * Post-create steps to setup the controller before use, such as creating the various services required.
     */
    @PostConstruct
    public void init() {

        //  Set up everything needed to communicate with subsonic device.
        try {
            JAXBContext context = JAXBContext.newInstance("org.subsonic.restapi");
            retrofit = new Retrofit.Builder()
              .baseUrl("http://" + subsonicHostName + "/rest/")
              .addConverterFactory(JaxbConverterFactory.create(context))
              .build();
        } catch (JAXBException e) {
            System.out.println ("Exception setting up retrofit: " + e);
        }

        //  Create the individual services required
        album = retrofit.create(AlbumSongServices.class);
        jukebox = retrofit.create(JukeboxService.class);

        //  The player name is based on the user and client names.
        playerName = subsonicClientName + "-" + subsonicUsername;
    }


    /**
     * Create a usable response object to be send to the Oracle Code Card
     * @param template template
     * @param title title
     * @param subtitle subtitle
     * @param body body text
     * @param icon icon to display
     * @param background background to discplay
     * @param bgColor background color
     * @return valid response object.
     */
    private CodeCardResponse createResponse (Template template,
                                             String title,
                                             String subtitle,
                                             String body,
                                             Icon icon,
                                             Background background,
                                             BackgroundColor bgColor) {

        //  Assign all provided values to the response object.
        CodeCardResponse toReturn = new CodeCardResponse();
        toReturn.setTemplate (template);
        toReturn.setTitle (title);
        toReturn.setSubtitle (subtitle);
        toReturn.setBodytext (body);

        if (icon != null) {
            toReturn.setIcon (icon);
        }

        if (background != null) {
            toReturn.setBackground (background);
        }

        if (bgColor != null) {
            toReturn.setBackgroundColor (bgColor);
        }


        return toReturn;
    }



    /**
     * Utility method to get rid of bloated code making all the calls
     * @param action the operation to perform
     * @param index zero-based index of the song to skip to or remove
     * @param offset when skipping, how many seconds into the track
     * @param id ID of song added or set to the jukebox playlist, can be multiples
     * @param gain controls playback volume
     * @return Response/success/failure of the call.
     * @throws IOException
     */
    Response<SubsonicResponse> jukeboxAction (String action,
                                              Integer index,
                                              Integer offset,
                                              String id,
                                              Double gain) throws IOException {
        return jukebox.jukeboxControl(subsonicUsername, subsonicPassword, "1.16.0", subsonicClientName, action, index, offset, id, gain).execute();
    }

    /**
     * A number of usages for the status, so centralize it for all usages.
     * @return current status of Jukebox, or null if unable to retrieve status from Subsonic.
     */
    private JukeboxStatus jukeboxStatus() {

        JukeboxStatus toReturn;
        try {
            toReturn =
              jukebox.jukeboxControl(subsonicUsername, subsonicPassword, "1.16.0", subsonicClientName, "status", null, null, null, null)
                .execute()
                .body()
                .getJukeboxStatus();

        } catch (IOException ioe) {
            System.out.println ("Exception attempting to get status: " + ioe);
            toReturn = null;
        }


        return toReturn;
    }
}
