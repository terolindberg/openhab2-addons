package org.openhab.binding.mopidy.api.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Tero Lindberg
 *
 */
public class Track {
    // "date":"1985",
    Album album;
    // "__model__":"Album",
    // "artists":[{
    // "__model__":"Artist",
    // "name":"Dire Straits",
    // "uri":"spotify:artist:0WwSkZ7LtFUFjGjMZBMt6T"
    // }],
    // "name":"Brothers In Arms",
    // "uri":"spotify:album:7jvcSnCnugLcisBCNBm60s"},
    // "__model__":"Track",
    String name;// "name":"The Man's Too Strong",
    Integer disc_no;// "disc_no":0,
    String uri;// "uri":"spotify:track:0OevHezRJXNhNeL7SEJbdG",
    Integer length;// "length":280000,
    Integer track_no;// "track_no":7,
    List<Artist> artists = new ArrayList<>();// "artists":[{

    List<Artist> composers = new ArrayList<>();
    List<Artist> performers = new ArrayList<>();
    String comment;
    // "__model__":"Artist",
    // "name":"Dire Straits",
    // "uri":"spotify:artist:0WwSkZ7LtFUFjGjMZBMt6T"
    // }],
    String date;// "date":"1985",
    Integer bitrate;// "bitrate":320

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDisc_no() {
        return disc_no;
    }

    public void setDisc_no(Integer disc_no) {
        this.disc_no = disc_no;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Integer getTrackNo() {
        return track_no;
    }

    public void setTrackNo(Integer track_no) {
        this.track_no = track_no;
    }

    public List<Artist> getArtists() {
        return artists;
    }

    public void setArtists(List<Artist> artists) {
        this.artists = artists;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getBitrate() {
        return bitrate;
    }

    public void setBitrate(Integer bitrate) {
        this.bitrate = bitrate;
    }

}
