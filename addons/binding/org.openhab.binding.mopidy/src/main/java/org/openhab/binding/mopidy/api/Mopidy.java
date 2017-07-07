package org.openhab.binding.mopidy.api;

import static org.openhab.binding.mopidy.api.MopidyAPIConstants.*;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.openhab.binding.mopidy.api.model.Image;
import org.openhab.binding.mopidy.api.model.JSONRPC;
import org.openhab.binding.mopidy.api.model.Ref;
import org.openhab.binding.mopidy.api.model.SearchQuery;
import org.openhab.binding.mopidy.api.model.SearchResult;
import org.openhab.binding.mopidy.api.model.Track;
import org.openhab.binding.mopidy.api.ws.MopidyWS;
import org.openhab.binding.mopidy.api.ws.MopidyWSListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

public class Mopidy {
    Gson gson = new Gson();
    private final Logger logger = LoggerFactory.getLogger(Mopidy.class);

    public String name;
    public String host;
    public int port;
    private String url;
    private URI webSocketUrl;

    WebSocketClient client;
    MopidyWS socket;

    private boolean PLAYING = false;

    public Mopidy() {

    }

    public Mopidy(String name, String host, int port) throws URISyntaxException {
        this.name = name;
        this.host = host;
        this.port = port;
        this.url = "http://" + this.host + ":" + this.port;

        webSocketUrl = new URI("ws://" + this.host + ":" + this.port + "/mopidy/ws");
        client = new WebSocketClient();
        socket = new MopidyWS();
    }

    public boolean isPLAYING() {
        return PLAYING;
    }

    public void setPLAYING(boolean pLAYING) {
        PLAYING = pLAYING;
    }

    public void setWSEventListener(MopidyWSListener listener) {
        socket.setWSEventListener(listener);
    }

    @Override
    public boolean equals(Object object) {
        boolean sameSame = false;

        if (object != null && object instanceof Mopidy) {
            sameSame = this.url == ((Mopidy) object).url;
        }

        return sameSame;
    }

    public String getURL() {
        return url;
    }

    public String getVersion() {

        try {
            return this.callApi("core.get_version", null).get("result").getAsString();
        } catch (Exception e) {
            return "Unknown";
        }

    }

    public HashSet<String> getSchemes() {

        HashSet<String> schemes = new HashSet<String>();

        JsonArray list = null;
        try {
            list = this.callApi(METHOD_CORE_URI_SCHEMES, null).get("result").getAsJsonArray();
            for (JsonElement scheme : list) {
                schemes.add(scheme.getAsString());
            }
        } catch (Exception e) {
            logger.error("{}", e.getMessage());
        }

        return schemes;

    }

    public Boolean tracklistClear() {

        logger.info("tracklistClear called");
        Boolean result = null;
        try {
            result = this.callApi(METHOD_TRACKLIST_CLEAR, null).get("result").getAsBoolean();
        } catch (Exception e) {
            logger.error("{}", e.getMessage());
        }
        return result;

    }

    public Boolean tracklistAdd(String url) {
        logger.info("Adding track %s", url);
        HashMap<String, Object> params = new HashMap<>();
        try {
            params.put("uri", url);
            logger.info("Params %s", params);
            return this.callApi(METHOD_TRACKLIST_ADD, params).get("result").getAsBoolean();
        } catch (Exception e) {
            logger.error("{}", e.getMessage());
        }
        return false;

    }

    public void play() throws ExecutionException, InterruptedException {
        this.callApi(METHOD_PLAYBACK_PLAY, null).get("result");
    }

    public void pause() throws ExecutionException, InterruptedException {
        this.callApi(METHOD_PLAYBACK_PAUSE, null).get("result");
    }

    public void next() throws ExecutionException, InterruptedException {
        this.callApi(METHOD_PLAYBACK_NEXT, null).get("result");
    }

    public void prev() throws ExecutionException, InterruptedException {
        this.callApi(METHOD_PLAYBACK_PREVIOUS, null).get("result");
    }

    public boolean getMute() throws ExecutionException, InterruptedException {
        return this.callApi(METHOD_MIXER_GET_MUTE, null).get("result").getAsBoolean();
    }

    public boolean setMute(boolean mute) throws ExecutionException, InterruptedException {
        HashMap<String, Object> params = new HashMap<>();
        params.put("mute", mute);
        return this.callApi(METHOD_MIXER_SET_MUTE, params).get("result").getAsBoolean();
    }

    public int getVolume() throws ExecutionException, InterruptedException {
        return this.callApi(METHOD_MIXER_GET_VOLUME, null).get("result").getAsInt();
    }

    public int setVolume(int volume) throws ExecutionException, InterruptedException {
        HashMap<String, Object> params = new HashMap<>();
        params.put("volume", volume);
        return this.callApi(METHOD_MIXER_SET_VOLUME, params).get("result").getAsInt();
    }

    public Collection<Image> getImage(String uri) throws ExecutionException, InterruptedException {
        HashMap<String, Object> params = new HashMap<>();
        List<String> uris = new ArrayList<String>();
        uris.add(uri);

        params.put("uris", uris);
        JsonArray images = this.callApi(METHOD_LIBRARY_IMAGES, params).getAsJsonObject("result").getAsJsonArray(uri);
        Type collectionType = new TypeToken<Collection<Image>>() {
        }.getType();
        Collection<Image> enums = gson.fromJson(images, collectionType);
        return enums;
    }

    public Collection<Ref> getPlaylists() throws ExecutionException, InterruptedException {
        return getRefList(METHOD_PLAYLIST_GET);
    }

    public Collection<Ref> getPlaylistItems() throws ExecutionException, InterruptedException {
        return getRefList(METHOD_PLAYLIST_GET_ITEMS);
    }

    private Collection<Ref> getRefList(String method) throws ExecutionException, InterruptedException {
        JsonObject object = this.callApi(method, null);// .get("result").getAsBoolean();
        JsonArray result = object.getAsJsonArray("result");
        Type collectionType = new TypeToken<Collection<Ref>>() {
        }.getType();
        Collection<Ref> enums = gson.fromJson(result, collectionType);
        return enums;
    }

    private Collection<Ref> getHistory() throws ExecutionException, InterruptedException {

        JsonObject object = this.callApi(METHOD_HISTORY_TRACK, null);
        JsonArray array = object.get("result").getAsJsonArray();
        Type collectionType = new TypeToken<Collection<JsonArray>>() {
        }.getType();
        Collection<JsonArray> coll = gson.fromJson(array, collectionType);
        List<Ref> list = new ArrayList<>();
        for (Iterator<JsonArray> i = coll.iterator(); i.hasNext();) {
            JsonArray arr = i.next();
            list.add(gson.fromJson(arr.get(1), Ref.class));
        }
        return list;
    }

    public Collection<SearchResult> search(SearchQuery search) throws ExecutionException, InterruptedException {
        logger.info("Searching {}", search);
        HashMap<String, Object> params = new HashMap<>();
        params.put("query", search);
        // params.put("uris", null);
        // params.put("exact", null);
        logger.info("Params {}", params);
        JsonObject object = this.callApi(METHOD_LIBRARY_SEARCH, params);// .getAsJsonObject("result");//
                                                                        // .getAsBoolean();
        JsonArray result = object.getAsJsonArray("result");
        Type collectionType = new TypeToken<Collection<SearchResult>>() {
        }.getType();
        Collection<SearchResult> enums = gson.fromJson(result, collectionType);
        return enums;
    }

    public Track getCurrentlyPlayingTrack() throws ExecutionException, InterruptedException {
        JsonObject object = this.callApi(METHOD_PLAYBACK_CURRENT_TRACK, null);
        JsonObject result = object.getAsJsonObject("result");
        Track track = gson.fromJson(result.get("track"), Track.class);

        return track != null ? track : null;
    }

    private JsonObject callApi(String cmd, HashMap<String, Object> params)
            throws ExecutionException, InterruptedException {
        JSONRPC json = new JSONRPC(cmd, params);
        return HttpConnectionHelper.executePostJson(this.getRPCUrl(), json);
    }

    public String getRPCUrl() {
        return this.getURL() + "/mopidy/rpc";
    }

    public String getName() {
        return name;
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getJSON() {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(this);
    }

    public void connectWebSocket() throws Exception {
        client.start();
        ClientUpgradeRequest request = new ClientUpgradeRequest();
        client.connect(socket, webSocketUrl, request);
        logger.debug("Connecting to : %s%n", webSocketUrl);
    }

    public void disconnectWebSocket() throws Exception {
        try {
            client.stop();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static void main(String args[]) {
        Mopidy mopidy = null;
        try {
            mopidy = new Mopidy("mopidy", "192.168.1.10", 6680);
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // System.out.println("YEE: " + mopidy.play());
        // Set<String> schemes = mopidy.getSchemes();
        //
        // for (String st : schemes) {
        // System.out.println("Scheme:" + st);
        // }
        //
        // Track track = mopidy.getCurrentlyPlayingTrack();
        // System.out.println("YEE: " + track.getName() + "/" + track.getArtists().get(0).getName());
        //
        // Collection<SearchResult> object = mopidy.search(new SearchQuery("Klamydia"));
        //
        // System.out.println("PRINT found results:" + object.size());
        //
        // for (SearchResult res : object) {
        // System.out.println(res.getUri() + " Artists : " + res.getArtists().size() + " Albums : "
        // + res.getAlbums().size() + " Tracks : " + res.getTracks().size());
        // }
        try {
            System.out.println("RESULT ");
            Collection<Image> imgs = mopidy.getImage("spotify:track:3otNtKS7zvDxGVtPiOEaF0");

            Iterator<Image> images = imgs.iterator();
            while (images.hasNext()) {
                Image img = images.next();
                System.out.println("Image uri " + img.getHeight() + " " + img.getUri());
            }
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
