package org.openhab.binding.mopidy.api.ws;

import org.openhab.binding.mopidy.api.model.Track;

/**
 *
 * @author Tero Lindberg
 *
 */
public interface MopidyWSListener {
    void onMopidyEvent(MopidyWSEvent event);

    void onStateChange(String oldState, String newState);

    void onCurrentTrack(Track track);

    void connectionClosed();

}
