package com.buddhadata.projects.jukebox.subsonic.client.services;

import org.subsonic.restapi.SubsonicResponse;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jaxb.JaxbConverterFactory;

import javax.xml.bind.JAXBContext;

/**
 * Created by scsosna on 11/10/18.
 */
public class Testing {

    public static void main (String[] args) {

        try {
            JAXBContext context = JAXBContext.newInstance("org.subsonic.restapi");
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://10.10.9.27:4040/rest/")
                    .addConverterFactory(JaxbConverterFactory.create(context))
                    .build();
//            SystemService service = services.create(SystemService.class);
//            Response<SubsonicResponse> response = service.getLicense("admin", "Anim8d99!", "1.16.0", "digital-jukebox").execute();
//            AlbumSongServices service = services.create(AlbumSongServices.class);
//            Response<SubsonicResponse> response = service.getNowPlaying("admin", "admin", "1.16.0", "digital-jukebox").execute();
//            System.out.println(response.body());

            Response<SubsonicResponse> response;

            AlbumSongServices album = retrofit.create(AlbumSongServices.class);
            JukeboxService jukebox = retrofit.create(JukeboxService.class);
            BrowsingService browse = retrofit.create(BrowsingService.class);

            //  Check current status
            response = jukebox.jukeboxControl("jukebox", "digital99", "1.16.0", "digital-jukebox", "status", null, null, null, null).execute();
            System.out.println (response);

            response = browse.getGenres("jukebox", "digital99", "1.16.0", "digital-jukebox").execute();
            response.body().getGenres().getGenre().forEach(one -> System.out.println (one.getContent()));

            Response<SubsonicResponse> aresp = album.getRandomSongs("jukebox", "digital99", "1.16.0", "digital-jukebox", 100, "Latin", null, null, null).execute();
            System.out.println (aresp.code());

/*
            PlaylistService playlist = services.create(PlaylistService.class);
            response = playlist.createPlaylist("admin", "admin", "1.16.0", "digital-jukebox", null, "myplaylist", null).execute();
            String plId = response.body().getPlaylist().getId();
            aresp.body().getRandomSongs().getSong().forEach(one -> {
                try {
                    Response<SubsonicResponse> response2 = playlist.updatePlaylist("admin", "admin", "1.16.0", "digital-jukebox", plId, null, null, null, one.getId(), null).execute();
                    System.out.println (response2.code());
                } catch (IOException ioe) {
                    System.out.println ("Exception: " + ioe);
                }
            });
*/


            //  Check current status
//            response = jukebox.jukeboxControl("jukebox", "digital99", "1.16.0", "digital-jukebox", "status", null, null, null, null).execute();
//            System.out.println (response);

            //  Clear out anything in the current jukebox playlist
            response = jukebox.jukeboxControl("jukebox", "digital99", "1.16.0", "digital-jukebox", "get", null, null, null, null).execute();
            for (int i = 0; i < response.body().getJukeboxPlaylist().getEntry().size(); i++) {
                jukebox.jukeboxControl("jukebox", "digital99", "1.16.0", "digital-jukebox", "remove", 0, null, null, null).execute();
            }

            //  Add a song and start the jukebox
            aresp.body().getRandomSongs().getSong().forEach (one -> {
               try {
                   jukebox.jukeboxControl("jukebox", "digital99", "1.16.0", "digital-jukebox", "add", null, null, one.getId(), null).execute();
               } catch (Exception e) {
                   System.out.println (e);
               }
            });
//            response = jukebox.jukeboxControl("admin", "admin", "1.16.0", "testing", "add", null, null, "10", null).execute();
            response = jukebox.jukeboxControl("jukebox", "digital99", "1.16.0", "digital-jukebox", "start", null, null, "10", null).execute();

            //  Check current status
            response = jukebox.jukeboxControl("jukebox", "digital99", "1.16.0", "digital-jukebox", "status", null, null, null, null).execute();
            System.out.println (response);


            jukebox.jukeboxControl("jukebox", "digital99", "1.16.0", "digital-jukebox", "add", null, null, null, null).execute();
            System.out.println (response);
        } catch (Exception e) {
            System.out.println (e);
        }
    }
}
