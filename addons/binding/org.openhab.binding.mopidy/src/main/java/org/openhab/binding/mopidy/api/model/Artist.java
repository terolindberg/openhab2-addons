package org.openhab.binding.mopidy.api.model;

/**
 *
 * @author Tero Lindberg
 *
 */
public class Artist {
    // "__model__":"Artist",
    String name;// "name":"Dire Straits",
    String uri;// "uri":"spotify:artist:0WwSkZ7LtFUFjGjMZBMt6T

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
