/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.avreceiver.handler.comm.onkyo;

import static org.openhab.binding.avreceiver.AVReceiverBindingConstants.CHANNEL_VOLUME;

import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.avreceiver.handler.AVReceiverHandler;
import org.openhab.binding.avreceiver.handler.comm.AVSocketConnection;
import org.openhab.binding.avreceiver.handler.comm.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Tero Lindberg
 *
 */
public class OnkyoHandler extends AVReceiverHandler {
    private Logger logger = LoggerFactory.getLogger(getClass());

    public OnkyoHandler(Thing thing, AVSocketConnection connection) {
        super(thing, connection);

    }

    @Override
    protected String handleChannelCommand(ChannelUID channelUID, Command command) {

        return null;
    }

    @Override
    protected Message createMessage(ChannelUID channelUID, Command command, String commandStr) {
        // TODO Auto-generated method stub
        return new OnkyoMessage(commandStr);
    }

    @Override
    protected String prepareCommand(ChannelUID channelUID, String command) {

        if (channelUID.getId().equals(CHANNEL_VOLUME)) {
            String type = getThing().getChannel(channelUID.getId()).getProperties().get("type");
            logger.debug("Volume channel type: {}", type);

            Double commandDouble = Double.parseDouble(command);
            String prepared = Integer.toHexString(commandDouble.intValue()).toUpperCase();

            while (prepared.length() < 2) {
                prepared = "0" + prepared;
            }
            return prepared;
        }
        return super.prepareCommand(channelUID, command);
    }

    @Override
    public void handleMessage(String message) {
        message = message.trim();

        if (message.contains("ISCP")) {
            message = message.replaceFirst("ISCP", "");
        }
        message = message.trim();
        if (message.startsWith("!1")) {
            message = message.substring(2);
        }
        logger.debug("onkyo Message:{}", message);

        if (message.startsWith("MVL")) {
            int intValue = Integer.parseInt(message.substring(3), 16);
            message = "MVL" + intValue;
        }

        super.handleMessage(message);

    }

}
