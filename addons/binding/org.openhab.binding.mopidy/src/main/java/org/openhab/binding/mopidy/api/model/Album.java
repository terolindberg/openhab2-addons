package org.openhab.binding.mopidy.api.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Album {
    // "__model__":"Album",
    String date;
    List<Artist> artists;
    // "artists":[{
    // "__model__":"Artist",
    // "name":"Dire Straits",
    // "uri":"spotify:artist:0WwSkZ7LtFUFjGjMZBMt6T"
    // }],
    String name;// "name":"Brothers In Arms",
    String uri;// "uri":"spotify:album:7jvcSnCnugLcisBCNBm60s"},
    @SerializedName("num_tracks")
    int numTracks;
    @SerializedName("num_discs")
    int numDisks;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Artist> getArtists() {
        return artists;
    }

    public void setArtists(List<Artist> artists) {
        this.artists = artists;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

}
