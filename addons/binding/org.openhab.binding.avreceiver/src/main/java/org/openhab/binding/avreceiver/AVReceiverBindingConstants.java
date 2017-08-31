/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.avreceiver;

import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link AVReceiverBinding} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Tero Lindberg - Initial contribution
 */
public class AVReceiverBindingConstants {
    public static enum Zone {
        Main_Zone,
        Zone_2,
        Zone_3,
        Zone_4;
    }

    public static final String BINDING_ID = "avreceiver";

    // List of all Thing Type UIDs
    public final static ThingTypeUID THING_TYPE_SAMPLE = new ThingTypeUID(BINDING_ID, "asample");

    // List of all Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_GENERAL_RENDERER = new ThingTypeUID(BINDING_ID, "generic");
    public static final ThingTypeUID THING_TYPE_YAMAHA = new ThingTypeUID(BINDING_ID, "yamaha");
    public static final ThingTypeUID THING_TYPE_DENON = new ThingTypeUID(BINDING_ID, "denon");
    public static final ThingTypeUID THING_TYPE_SAMSUNG = new ThingTypeUID(BINDING_ID, "samsung");
    public static final ThingTypeUID THING_TYPE_ONKYO = new ThingTypeUID(BINDING_ID, "onkyo");
    public static final ThingTypeUID THING_TYPE_EPSON_PROJECTOR = new ThingTypeUID(BINDING_ID, "epson_projector");
    public static final ThingTypeUID THING_TYPE_PHILIPS = new ThingTypeUID(BINDING_ID, "philips");

    // List of all Channel ids
    public static final String CHANNEL_POWER = "power";
    public static final String CHANNEL_INPUT = "input";
    public static final String CHANNEL_SURROUND = "surroundProgram";
    public static final String CHANNEL_VOLUME = "master_volume";
    public static final String CHANNEL_VOLUME_DB = "master_volume_db";
    public static final String CHANNEL_MUTE = "mute";
    public static final String CHANNEL_NETRADIO_TUNE = "netradiotune";

    public static final String UPNP_TYPE = "MediaRenderer";

    public static final CharSequence UPNP_MANUFACTURER_YAMAHA = "YAMAHA";
    public static final CharSequence UPNP_MANUFACTURER_SAMSUNG = "SAMSUNG";
    public static final CharSequence UPNP_MANUFACTURER_DENON = "DENON";
    public static final CharSequence UPNP_MANUFACTURER_ONKYO = "ONKYO";
    public static final CharSequence UPNP_MANUFACTURER_EPSON = "EPSON";
    public static final CharSequence UPNP_MANUFACTURER_PHILIPS = "PHILIPS";

    // public static final String CONFIG_REFRESH = "REFRESH_IN_SEC";
    public static final String CONFIG_HOST_NAME = "hostname";
    public static final String CONFIG_HOST_SOCKET_PORT = "socket_port";
    public static final String CONFIG_HOST_HTTP_PORT = "http_port";
    public static final String CONFIG_REFRESH_CODE = "refresh_code";
    public static final String CONFIG_ZONE = "ZONE";
    public static final String CONFIG_RELVOLUMECHANGE = "RELVOLUMECHANGE";

}
