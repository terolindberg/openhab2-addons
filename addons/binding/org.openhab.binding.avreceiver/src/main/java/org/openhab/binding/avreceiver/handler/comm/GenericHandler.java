package org.openhab.binding.avreceiver.handler.comm;

import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.avreceiver.handler.AVReceiverHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * GenericHandler is a dummy implementation for a thing handler, can be used for basic functions.
 *
 * @author Tero Lindberg
 *
 */
public class GenericHandler extends AVReceiverHandler {
    private Logger logger = LoggerFactory.getLogger(getClass());

    public GenericHandler(Thing thing, GenericConnection connection) {
        super(thing, connection);
    }

    @Override
    protected String handleChannelCommand(ChannelUID channelUID, Command command) {

        return null;
    }

    @Override
    protected Message createMessage(ChannelUID channelUID, Command command, String commandStr) {

        return new Message(commandStr);
    }

    @Override
    public void handleRefresh(Command command, Channel channel) {

    }

}
