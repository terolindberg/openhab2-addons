/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.mopidy.handler;

import static org.openhab.binding.mopidy.MopidyBindingConstants.*;
import static org.openhab.binding.mopidy.api.ws.MopidyWSConstants.*;

import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.NextPreviousType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.PlayPauseType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.openhab.binding.mopidy.api.Mopidy;
import org.openhab.binding.mopidy.api.model.Image;
import org.openhab.binding.mopidy.api.model.Track;
import org.openhab.binding.mopidy.api.ws.MopidyWSEvent;
import org.openhab.binding.mopidy.api.ws.MopidyWSListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link MopidyHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Tero Lindberg - Initial contribution
 */
public class MopidyHandler extends BaseThingHandler implements MopidyWSListener {
    Mopidy mopidy;
    private Logger logger = LoggerFactory.getLogger(MopidyHandler.class);

    public MopidyHandler(Thing thing) {
        super(thing);

    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {

        logger.debug("HandleCommand ChannelUID:{} command:{} commandClass:{}", channelUID, command,
                command.getClass().getName());

        try {
            if (command instanceof RefreshType) {
                handleRefresh(channelUID);
            } else {
                switch (channelUID.getId()) {

                    case CHANNEL_CONTROL:
                        if (command instanceof PlayPauseType) {
                            if (command.equals(PlayPauseType.PLAY) && !mopidy.isPLAYING()) {
                                mopidy.play();
                            } else if (command.equals(PlayPauseType.PAUSE) && mopidy.isPLAYING()) {
                                mopidy.pause();
                            }
                        } else if (command instanceof NextPreviousType) {
                            if (command.equals(NextPreviousType.NEXT)) {
                                mopidy.next();
                            } else if (command.equals(NextPreviousType.PREVIOUS)) {
                                mopidy.prev();
                            }
                        }
                        break;
                    case CHANNEL_MUTE:
                        mopidy.setMute(true);
                        break;
                    default:
                        logger.warn("HandleCommand no channel action defined: ChannelUID:{} command:{} commandClass:{}",
                                channelUID, command, command.getClass().getName());
                }
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, "Connection failed to mopidy");
        }
    }

    private void handleRefresh(ChannelUID channelUID) throws ExecutionException, InterruptedException {
        switch (channelUID.getId()) {
            case CHANNEL_VOLUME:
                updateState(channelUID, new DecimalType(mopidy.getVolume()));
                break;
            case CHANNEL_MUTE:
                updateState(channelUID, mopidy.getMute() ? OnOffType.ON : OnOffType.OFF);
                break;
        }

    }

    @Override
    public void initialize() {

        super.initialize();
        updateStatus(ThingStatus.UNKNOWN);
        logger.debug("Initialize Mopidy handler.");

        Configuration conf = this.getConfig();
        String hostName = String.valueOf(conf.get(PARAMETER_HOSTNAME));
        int port = 6680;
        Object value = conf.get(PARAMETER_PORT);
        if (value != null) {
            if (value instanceof BigDecimal) {
                port = ((BigDecimal) value).intValue();
            } else {
                port = (Integer) value;
            }
        }

        try {
            this.mopidy = new Mopidy("new", hostName, port);
        } catch (URISyntaxException e1) {
            logger.error("Invalid URI: {}", hostName);
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR);
        }

        this.mopidy.setWSEventListener(this);
        startWSConnection();
    }

    @Override
    public void onMopidyEvent(MopidyWSEvent event) {
        logger.debug("Got new event {}  newState: {} track: {}", event.getEvent(), event.getNewState(),
                event.getTLTrack());

    }

    @Override
    public void onStateChange(String oldState, String newState) {

        switch (newState) {
            case EVENT_PLAYBACK_PAUSED:
                mopidy.setPLAYING(false);
                updateState(CHANNEL_CONTROL, PlayPauseType.PAUSE);
                break;
            case EVENT_PLAYBACK_STARTED:
            case EVENT_PLAYBACK_PLAYING:
                mopidy.setPLAYING(true);
                updateState(CHANNEL_CONTROL, PlayPauseType.PLAY);
                break;
            case EVENT_PLAYBACK_RESUMED:
                break;
        }
    }

    @Override
    public void onCurrentTrack(Track track) {
        logger.debug("Current track: {}", track.getName());
        updateState(CHANNEL_CURRENTLY_PLAYING_TRACK, new StringType(track.getName()));
        if (track.getAlbum() != null) {
            updateState(CHANNEL_CURRENTLY_PLAYING_ALBUM, new StringType(track.getAlbum().getName()));
        }
        if (track.getArtists().size() > 0) {
            updateState(CHANNEL_CURRENTLY_PLAYING_ARTIST, new StringType(track.getArtists().get(0).getName()));

        } else if (track != null && track.getAlbum().getArtists().size() > 0) {
            updateState(CHANNEL_CURRENTLY_PLAYING_ARTIST,
                    new StringType(track.getAlbum().getArtists().get(0).getName()));

        }
        try {
            Collection<Image> imgs = mopidy.getImage(track.getUri());

            for (Image img : imgs) {
                switch (img.getHeight()) {
                    case 64:
                        updateState(CHANNEL_CURRENTLY_PLAYING_ALBUMART_SMALL, new StringType(img.getUri()));
                        break;
                    case 300:
                        updateState(CHANNEL_CURRENTLY_PLAYING_ALBUMART, new StringType(img.getUri()));
                        break;
                    case 600:
                        updateState(CHANNEL_CURRENTLY_PLAYING_ALBUMART_LARGE, new StringType(img.getUri()));
                        break;

                }
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    long lastReconnectAttempt;
    long reconnectInterval = 30000;

    @Override
    public void connectionClosed() {
        updateStatus(ThingStatus.OFFLINE);
        startWSConnection();

    }

    private void startWSConnection() {
        // TODO Properly queue/manage reconnect attempts
        if (System.currentTimeMillis() - lastReconnectAttempt > reconnectInterval) {
            lastReconnectAttempt = System.currentTimeMillis();
            new Thread(new Runnable() {

                @Override
                public void run() {
                    logger.debug("Re-Connecting to websocket ");
                    try {
                        mopidy.connectWebSocket();

                        updateStatus(ThingStatus.ONLINE);
                    } catch (Exception e) {
                        e.printStackTrace();
                        updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR);
                    }

                }
            }).start();
        }

    }
}
