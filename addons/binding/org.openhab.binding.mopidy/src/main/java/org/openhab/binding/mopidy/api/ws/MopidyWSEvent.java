package org.openhab.binding.mopidy.api.ws;

import org.openhab.binding.mopidy.api.model.TLTrack;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author Tero Lindberg
 *
 */
public class MopidyWSEvent {
    @SerializedName("tl_track")
    TLTrack tlTrack;
    String event;
    @SerializedName("old_state")
    String oldState;
    @SerializedName("new_state")
    String newState;
    @SerializedName("time_position")
    Integer timePosition;

    public TLTrack getTLTrack() {
        return tlTrack;
    }

    public void setTLTrack(TLTrack track) {
        this.tlTrack = track;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getOldState() {
        return oldState;
    }

    public void setOldState(String old_state) {
        this.oldState = old_state;
    }

    public String getNewState() {
        return newState;
    }

    public void setNewState(String new_state) {
        this.newState = new_state;
    }

}
