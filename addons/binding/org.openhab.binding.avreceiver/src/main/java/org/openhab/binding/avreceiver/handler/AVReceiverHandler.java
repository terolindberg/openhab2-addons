/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.avreceiver.handler;

import static org.openhab.binding.avreceiver.AVReceiverBindingConstants.CONFIG_REFRESH_CODE;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.eclipse.smarthome.core.types.State;
import org.openhab.binding.avreceiver.handler.comm.AVSocketConnection;
import org.openhab.binding.avreceiver.handler.comm.Message;
import org.openhab.binding.avreceiver.handler.comm.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link AVReceiverHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Tero Lindberg - Initial contribution
 */
public abstract class AVReceiverHandler extends BaseThingHandler implements MessageHandler {

    private Logger logger = LoggerFactory.getLogger(AVReceiverHandler.class);

    AVSocketConnection connection;

    public AVReceiverHandler(Thing thing, AVSocketConnection connection) {
        super(thing);
        this.connection = connection;
        connection.setThingHandler(this);

    }

    public final AVSocketConnection getConnection() {
        return this.connection;
    }

    public void handleRefresh(Command command, Channel channel) {

        Map<String, String> props = channel.getProperties();
        String code = (String) getThing().getConfiguration().get(CONFIG_REFRESH_CODE);
        if (code == null) {
            code = "?";
        }
        String value = props.get("value");
        getConnection()
                .sendMessage(createMessage(channel.getUID(), command, value.substring(0, value.indexOf("{}")) + code));
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        Channel channel = getThing().getChannel(channelUID.getId());

        Map<String, String> props = channel.getProperties();

        if (!props.containsKey("value")) {
            logger.error("No 'value' set in channel properties for channel '{}' ", channelUID.getId());
            return;
        }

        logger.debug("Got command {} channel command: {}", command.toFullString(), channelUID.getId());
        if (getThing().getStatus().equals(ThingStatus.ONLINE)) {
            String value = props.get("value");

            if (command instanceof RefreshType) {
                handleRefresh(command, channel);

            } else {
                String message = handleChannelCommand(channelUID, command);
                if (message == null) {

                    if (value.contains("{}")) {
                        String commandStr = validateValue(props, command);

                        if (props.containsKey(command.toString())) {
                            commandStr = props.get(command.toString());
                        } else {

                            commandStr = prepareCommand(channelUID, commandStr);
                        }
                        value = value.replaceAll("\\{\\}", commandStr);
                    }
                    getConnection().sendMessage(createMessage(channelUID, command, value));

                } else {

                    logger.debug("sending {}", message);
                    getConnection().sendMessage(createMessage(channelUID, command, message));
                }

            }

        }

    }

    /**
     * Invoked by {@link AVSocketConnection} once it has received a message from socket
     *
     * Tries to match beginning of the message with a channel of the thing and change the
     * channel value accordingly. Can be overridden by the implementing class to handle
     * more complex protocols and cases which don't fit the xml specification easily
     */
    @Override
    public void handleMessage(String message) {
        List<Channel> channels = getThing().getChannels();

        // Trying to find possible channel
        Channel channel = null;
        String value = null;
        for (Channel ch : channels) {
            value = ch.getProperties().get("value");
            if (value.contains("{}")) {
                value = value.substring(0, value.indexOf("{}")).trim();
            }
            if (message.startsWith(value)) {
                channel = ch;
                break;
            }
        }
        if (channel != null && value != null) {
            logger.debug("Received message: '{}' could fit to channel '{}({})'", message, channel.getUID().getId(),
                    channel.getAcceptedItemType());

            updateState(channel.getUID(), getState(message.substring(value.length()), channel));

        } else {
            logger.warn(
                    "Received message: '{}', can't find suitable channel, you may need to implement override handleMessage() in your own AVReceiverHandler implementation to take care of this properly",
                    message);
        }
    }

    protected String prepareCommand(ChannelUID channelUID, String commandStr) {
        return commandStr;
    }

    /**
     * To override
     *
     * @param channelUID
     * @param command
     * @return null if no further action is required from base implementation, message body if overriding implementation
     *         has something to send
     */
    protected abstract String handleChannelCommand(ChannelUID channelUID, Command command);

    @Override
    public void initialize() {

        // Configuration conf = thing.getConfiguration();
        //
        // for (String key : conf.keySet()) {
        // logger.debug("Thing {} configuration {} : {}", thing.getLabel(), key, conf.get(key));
        //
        // }
        // Map<String, String> props = thing.getProperties();
        //
        // for (String key : props.keySet()) {
        // logger.debug("Thing {} properties {} : {}", thing.getLabel(), key, props.get(key));
        //
        // }

        // List<Channel> channels = thing.getChannels();
        // logger.debug("Found {} channels for {}", channels.size(), thing.getLabel());
        // for (Channel channel : channels) {
        // Map<String, String> properties = channel.getProperties();
        // Configuration configuration = channel.getConfiguration();
        //
        // for (String key : properties.keySet()) {
        // logger.debug("Properties for channel {} ( {} , {} )", channel.getLabel(), key, properties.get(key));
        // }
        // }
        try {
            getConnection().startConnection(true);
            updateStatus(ThingStatus.ONLINE);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, e.getMessage());
        }

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                connection.disconnect();
            }
        }));
    }

    @Override
    public void updateStatus(ThingStatus status, ThingStatusDetail statusDetail, String description) {
        super.updateStatus(status, statusDetail, description);
    }

    @Override
    public void updateStatus(ThingStatus status) {
        updateStatus(status, ThingStatusDetail.NONE, null);
    }

    @Override
    public void dispose() {
        // can be overridden by subclasses
        connection.disconnect();
    }

    protected abstract Message createMessage(ChannelUID channelUID, Command command, String commandStr);

    public String validateValue(Map<String, String> props, Command command) {
        if (props.containsKey("min") && props.containsKey("max")) {
            Double min = Double.parseDouble(props.get("min"));
            Double max = Double.parseDouble(props.get("max"));
            double volume = Double.parseDouble(command.toString());
            if (volume > max) {
                volume = max;
            }
            if (volume < min) {
                volume = min;
            }
            logger.debug("Validated:{}", Double.toString(volume));
            return Double.toString(volume);
        } else {
            return command.toString();
        }
    }

    public static final String SWITCH = "Switch";
    public static final String ROLLERSHUTTER = "Rollershutter";
    public static final String CONTACT = "Contact";
    public static final String STRING = "String";
    public static final String NUMBER = "Number";
    public static final String DIMMER = "Dimmer";
    public static final String DATETIME = "DateTime";
    public static final String COLOR = "Color";
    public static final String IMAGE = "Image";
    public static final String PLAYER = "Player";
    public static final String LOCATION = "Location";
    public static final String CALL = "Call";

    private State getState(String value, Channel channel) {
        Map<String, String> props = channel.getProperties();
        switch (channel.getAcceptedItemType()) {
            case DIMMER:
                double val = Double.parseDouble(value);
                while (val > 1) { // To fix possible Denon MV245 -> 24.5% -> 0.245
                    val = val / 100;
                }
                return new DecimalType(new BigDecimal(val));
            case SWITCH:
                return props.get("OFF").equals(value) ? OnOffType.OFF : OnOffType.ON;
            case NUMBER:
                return new DecimalType(new BigDecimal(value));
            case STRING:
                return new StringType(value);
        }
        return new StringType(value);
    }

}
