package org.openhab.binding.mopidy.api.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Tero Lindberg
 *
 */
public class Playlist {
    private String uri;
    private String name;
    private List<Track> tracks = new ArrayList<>();

    public Playlist() {

    }

    public Playlist(String uri, String name, List<Track> tracks) {
        this.uri = uri;
        this.name = name;
        this.tracks = tracks;
    }

    public String getUri() {
        return uri;
    }

    public String getName() {
        return name;
    }

    public List<Track> getTracks() {
        return tracks;
    }

    @Override
    public String toString() {
        return name;
    }

}
