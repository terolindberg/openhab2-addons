/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.mopidy;

import java.util.Collections;
import java.util.Set;

import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link MopidyBindingBinding} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Tero Lindberg - Initial contribution
 */
public class MopidyBindingConstants {

    public static final String BINDING_ID = "mopidy";

    // List of all Thing Type UIDs
    public final static ThingTypeUID THING_TYPE_MOPIDY = new ThingTypeUID(BINDING_ID, "player");

    // List of all Channel ids
    public final static String CHANNEL_CURRENTLY_PLAYING = "currentlyPlaying";
    public final static String CHANNEL_CURRENTLY_PLAYING_TRACK = "currentlyPlayingTrack";
    public final static String CHANNEL_CURRENTLY_PLAYING_ALBUM = "currentlyPlayingAlbum";
    public final static String CHANNEL_CURRENTLY_PLAYING_ARTIST = "currentlyPlayingArtist";
    public final static String CHANNEL_CURRENTLY_PLAYING_ALBUMART_SMALL = "currentlyPlayingAlbumArtSmall";
    public final static String CHANNEL_CURRENTLY_PLAYING_ALBUMART = "currentlyPlayingAlbumArt";
    public final static String CHANNEL_CURRENTLY_PLAYING_ALBUMART_LARGE = "currentlyPlayingAlbumArtLarge";
    public final static String CHANNEL_CONTROL = "control";
    public final static String CHANNEL_VOLUME = "mixervolume";
    public final static String CHANNEL_MUTE = "mixermute";

    // public final static String CHANNEL_PREVIOUS = "previous";
    public final static Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Collections.singleton(THING_TYPE_MOPIDY);

    public final static String PARAMETER_HOSTNAME = "hostname";
    public final static String PARAMETER_PORT = "port";

}
