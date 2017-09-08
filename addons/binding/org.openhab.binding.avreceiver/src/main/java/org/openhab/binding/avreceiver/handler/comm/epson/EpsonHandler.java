/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.avreceiver.handler.comm.epson;

import static org.openhab.binding.avreceiver.AVReceiverBindingConstants.CHANNEL_VOLUME;

import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.avreceiver.handler.AVReceiverHandler;
import org.openhab.binding.avreceiver.handler.comm.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Tero Lindberg
 *
 */
public class EpsonHandler extends AVReceiverHandler {
    private Logger logger = LoggerFactory.getLogger(getClass());

    public EpsonHandler(Thing thing, EpsonConnection connection) {
        super(thing, connection);

    }

    @Override
    protected String handleChannelCommand(ChannelUID channelUID, Command command) {
        // TODO Auto-generated method stub

        // connection.sendMessage(new DenonMessage("PWON"));
        // TODO: handle command
        // connection.sendMessage();
        // Note: if communication with thing fails for some reason,
        // indicate that by setting the status with detail information
        // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
        // "Could not control device at IP address x.x.x.x");
        return null;
    }

    @Override
    protected Message createMessage(ChannelUID channelUID, Command command, String commandStr) {

        if (commandStr.contains(" ?")) {
            commandStr = commandStr.replaceAll(" ", "");
        }
        return new EpsonMessage(commandStr);
    }

    @Override
    protected String prepareCommand(ChannelUID channelUID, String command) {

        if (channelUID.getId().equals(CHANNEL_VOLUME)) {
            /**
             * Commands are decimals with .5 step (80, 80.5, 81)
             * Ex. MASTER VOLUME = +1.0dB : MV81<CR>
             * +0.5dB : MV805<CR>
             * 0dB : MV80<CR>
             *
             */

            String prepared = command.replace(".", "");
            while (prepared.length() < 2) {
                prepared = "0" + prepared;
            }
            return prepared;
        }
        return super.prepareCommand(channelUID, command);
    }

    /**
     * Cleaning message from projector to fit to XML specification
     * Incoming message is structured like '::LUMINANCE=01'
     * while outgoing message is 'LUMINANCE 01' and {@link AVReceiverHandler}
     * handles automatically the messages that follow outgoing message structure
     */
    @Override
    public void handleMessage(String message) {
        String orig = message;
        if (message.contains(":")) {
            message = message.replaceAll(":", "");
        }
        if (message.contains("=")) {
            message = message.replaceAll("=", " ");
        }

        logger.debug("Message converted:{}->{}", orig, message);
        super.handleMessage(message);

    }

}
