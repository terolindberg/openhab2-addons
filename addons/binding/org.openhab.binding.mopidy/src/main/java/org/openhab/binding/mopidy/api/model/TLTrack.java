package org.openhab.binding.mopidy.api.model;

/**
 *
 * @author Tero Lindberg
 *
 */
public class TLTrack {
    Integer tlid;
    Track track;

    public Integer getTlid() {
        return tlid;
    }

    public void setTlid(Integer tlid) {
        this.tlid = tlid;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

}
