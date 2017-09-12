/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.avreceiver.handler;

import static org.openhab.binding.avreceiver.AVReceiverBindingConstants.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.library.CoreItemFactory;
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

    /**
     * Defines how long non mapped error situation is visible
     */
    public static int ERROR_TIMEOUT_MS = 5000;
    Timer errorTimer;

    public AVReceiverHandler(Thing thing, AVSocketConnection connection) {
        super(thing);
        this.connection = connection;
        connection.setThingHandler(this);

    }

    public final AVSocketConnection getConnection() {
        return this.connection;
    }

    public final void handleRefresh(Command command, Channel channel) {
        logger.debug("HANDLING REFRESH FOR {} - {}", getThing().getLabel(), channel.getLabel());

        Map<String, String> props = channel.getProperties();
        String code = (String) getThing().getConfiguration().get(CONFIG_REFRESH_CODE);
        if (code == null) {
            code = "?";
        }
        String value = props.get("value");
        if (props.containsKey("value")) {
            String message = props.get("value");

            message = message.replaceAll("\\{\\}", code);
            logger.debug("Refresh value:{}, code:{}, message:{}", props.get("value"), code, message);
            getConnection().sendMessage(createMessage(channel.getUID(), command, message));
        }
        // getConnection()
        // .sendMessage(createMessage(channel.getUID(), command, value.substring(0, value.indexOf("{}")) + code));
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        Channel channel = getThing().getChannel(channelUID.getId());

        if (channel == null) {
            logger.error("No 'channel' '{}' found!", channelUID.getId());
            return;
        }
        Map<String, String> props = channel.getProperties();

        if (!props.containsKey("value")) {
            logger.error("No 'value' set in channel properties for channel '{}' ", channelUID.getId());
            return;
        }

        logger.debug("Got command '{}' channel command: '{}' thingstatus={} commandType={}", command.toFullString(),
                channelUID.getId(), getThing().getStatus().toString(), command.getClass().getName());
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
     *
     * For example: Some protocols have different structure in messages going to the device
     * compared to ones sent by the device
     */
    @Override
    public void handleMessage(String message) {
        logger.debug("Received message '{}'", message);
        if (message.length() == 0) {
            return;
        }

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
            if (channel.getChannelTypeUID().getId().equalsIgnoreCase("error")) {
                handleError(channel, message.substring(value.length()));
            } else {
                State state = getState(message.substring(value.length()), channel);
                logger.debug("Updating channel {} state to {}", channel.getUID(), state);
                updateState(channel.getUID(), state);
                updateStatus(ThingStatus.ONLINE, ThingStatusDetail.NONE, "");

            }

        } else {
            logger.info(
                    "Received message: '{}', can't find suitable channel, you may need to implement override handleMessage() in your own AVReceiverHandler implementation to take care of this properly",
                    message);
            updateStatus(ThingStatus.ONLINE, ThingStatusDetail.NONE, "no handling for '" + message + "'");
        }
    }

    protected String prepareCommand(ChannelUID channelUID, String commandStr) {
        return commandStr;
    }

    private void handleError(Channel channel, String message) {
        Set<String> keys = channel.getProperties().keySet();

        logger.debug("ERROR on channel {} - '{}'", channel.getLabel(), message);
        for (String key : keys) {
            if (key.startsWith("ERROR_CODE_")) {
                if (key.substring("ERROR_CODE_".length()).trim().equals(message.trim())) {
                    updateStatus(ThingStatus.ONLINE, ThingStatusDetail.NONE, channel.getProperties().get(key));
                    return;
                }
            }
        }
        if (message == null || message.length() == 0) {
            message = "Unknown Error";
        }

        updateStatus(ThingStatus.ONLINE, ThingStatusDetail.NONE, "'" + message + "'");
        if (errorTimer != null) {
            errorTimer.cancel();
        }
        errorTimer = new Timer();
        errorTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateStatus(ThingStatus.ONLINE, ThingStatusDetail.NONE, null);

            }
        }, ERROR_TIMEOUT_MS);
    }

    /**
     * To override if custom behavior is required
     *
     * @param channelUID
     * @param command
     * @return null if something non default is to be sent. If returns non-null, the command is sent directly to the
     *         thing
     */
    protected abstract String handleChannelCommand(ChannelUID channelUID, Command command);

    @Override
    public void handleConfigurationUpdate(Map<String, Object> configurationParameters) {
        connection.disconnect();
        if (configurationParameters.containsKey(CONFIG_HOST_SOCKET_PORT)) {
            connection.setPort((int) configurationParameters.get(CONFIG_HOST_SOCKET_PORT));
        }
        if (configurationParameters.containsKey(CONFIG_HOST_NAME)) {
            connection.setHost((String) configurationParameters.get(CONFIG_HOST_NAME));
        }
        logger.debug("New configuration : {}:{}", connection.getHost(), connection.getPort());

        super.handleConfigurationUpdate(configurationParameters);
        initialize();
    }

    @Override
    public void initialize() {

        Configuration conf = thing.getConfiguration();
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
            if (this.connection.isConfigured()) {
                this.connection.startConnection(true);
                updateStatus(ThingStatus.ONLINE);
            } else {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, "Connection details not set");
            }

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

    private State getState(String value, Channel channel) {
        Map<String, String> props = channel.getProperties();
        switch (channel.getAcceptedItemType()) {
            case CoreItemFactory.DIMMER:

                double val;
                try {
                    val = Double.parseDouble(value);
                } catch (NumberFormatException e) {
                    try {
                        logger.debug("trying hex:{} -> {}", value, Long.parseLong(value, 16));
                        val = Long.parseLong(value, 16);
                    } catch (Exception e2) {
                        logger.warn("Value not dec nor hex for dimmer {}", value);
                        return new DecimalType();
                    }
                }
                while (val > 1) { // To fix possible Denon MV245 -> 24.5% -> 0.245
                    val = val / 100;
                }
                return new DecimalType(new BigDecimal(val));
            case CoreItemFactory.SWITCH:
                return props.get("OFF").equals(value) ? OnOffType.OFF : OnOffType.ON;
            case CoreItemFactory.NUMBER:
                return new DecimalType(new BigDecimal(value));
            case CoreItemFactory.STRING:
                return new StringType(value);
        }
        return new StringType(value);
    }

}
