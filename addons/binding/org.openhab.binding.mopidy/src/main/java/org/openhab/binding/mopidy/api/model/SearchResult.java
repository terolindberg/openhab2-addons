package org.openhab.binding.mopidy.api.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Tero Lindberg
 *
 */
public class SearchResult {
    List<Artist> artists;
    List<Album> albums;
    List<Track> tracks;
    String uri;

    public SearchResult() {
        artists = new ArrayList<>();
        albums = new ArrayList<>();
        tracks = new ArrayList<>();

    }

    public List<Artist> getArtists() {
        return artists;
    }

    public void setArtists(List<Artist> artists) {
        this.artists = artists;
    }

    public List<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(List<Album> albums) {
        this.albums = albums;
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

}
