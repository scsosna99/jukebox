package com.buddhadata.projects.jukebox.particle.event;

import com.buddhadata.projects.jukebox.subsonic.client.SubsonicHelper;
import com.buddhadata.projects.jukebox.subsonic.client.services.JukeboxService;
import nl.infcomtec.jparticle.DeviceEvent;
import nl.infcomtec.jparticle.Event;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.UUID;

/**
 * Event when the Photon publishes a volume increase/decrease event
 */
@Component
public class VolumeChangeEvent implements DeviceEvent {

  /**
   * Retrofit service for making calls to the Subsonic Jukebox API
   */
  private JukeboxService jukebox;

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
   * Particle event name of the published event for volume change.
   */
  private static final String EVENT_VOLUME_CHANGE = "jukebox.volume.change";

  /**
   * UUID for the event, only unique within this execution
   */
  private static final UUID eventUUID = UUID.randomUUID();

  /**
   * Called whenever a subscribed event arrives.
   *
   * @param e The event from the cloud.
   */
  public void event(Event e) {

    try {
      //  Data received, attempt to change volume on the jukebox.
      jukebox.jukeboxControl(subsonicUsername, subsonicPassword, SubsonicHelper.SUBSONIC_API_VERSION, subsonicClientName, "setGain", null, null, null, Double.valueOf(e.data)).execute();
    } catch (Throwable t) {
      //  Don't want any exceptions to leak out of here, just swallow it.
      System.out.println ("Exception changing volume: " + t);
    }
  }

  /**
   * Used to cancel a subscription.
   *
   * @return Should always return the same UUID for a given task.
   */
  public UUID uuid() {
    return eventUUID;
  }

  /**
   * If not null, we only want events for this device Id.
   *
   * @return null or deviceId to match.
   */
  public String forDeviceId() {
    return null;
  }

  /**
   * If not null, we only want events for this device name.
   *
   * @return null or device name to match.
   */
  public String forDeviceName() {
    return null;
  }

  /**
   * If not null, we only want events that match this event name.
   *
   * Note that Particle Cloud publish does not restrict event names in
   * any way other then a max length. More then one device may send the
   * some event name and the event data is not matched to the name.
   *
   * @return the case sensitive name to match.
   */
  public String forEventName() {
    return EVENT_VOLUME_CHANGE;
  }

  /**
   * Post-create steps to setup the controller before use, such as creating the various services required.
   */
  @PostConstruct
  public void init() {

    //  Create the individual services required
    jukebox = SubsonicHelper.instance.createService(subsonicHostName, JukeboxService.class);
  }
}
