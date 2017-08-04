package org.openhab.binding.mopidy.api;

/**
 *
 * @author Tero Lindberg
 *
 */
public class MopidyAPIConstants {
    public final static String METHOD_CORE_URI_SCHEMES = "core.get_uri_schemes";

    public final static String METHOD_TRACKLIST_CLEAR = "core.tracklist.clear";
    public final static String METHOD_TRACKLIST_ADD = "core.tracklist.add";
    public final static String METHOD_TRACKLIST_GET_TRACKS = "core.tracklist.get_tracks";
    public final static String METHOD_PLAYBACK_PLAY = "core.playback.play";
    public final static String METHOD_PLAYBACK_PAUSE = "core.playback.pause";
    public final static String METHOD_PLAYBACK_NEXT = "core.playback.next";
    public final static String METHOD_PLAYBACK_PREVIOUS = "core.playback.previous";
    public final static String METHOD_PLAYBACK_CURRENT_TRACK = "core.playback.get_current_tl_track";
    public final static String METHOD_PLAYLIST_GET = "core.playlists.as_list";
    public final static String METHOD_PLAYLIST_GET_ITEMS = "core.playlists.get_items";
    public final static String METHOD_LIBRARY_SEARCH = "core.library.search";
    public final static String METHOD_LIBRARY_LOOKUP = "core.library.lookup";
    public final static String METHOD_LIBRARY_IMAGES = "core.library.get_images";
    public final static String METHOD_LIBRARY_FIND_EXACT = "core.library.find_exact";
    public final static String METHOD_LIBRARY_BROWSE = "core.library.browse";
    public final static String METHOD_MIXER_GET_MUTE = "core.mixer.get_mute";
    public final static String METHOD_MIXER_SET_MUTE = "core.mixer.set_mute";
    public final static String METHOD_MIXER_GET_VOLUME = "core.mixer.get_volume";
    public final static String METHOD_MIXER_SET_VOLUME = "core.mixer.set_volume";
    public final static String METHOD_HISTORY_TRACK = "core.history.get_history";
}
