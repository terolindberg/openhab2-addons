package org.openhab.binding.avreceiver.handler.comm;

import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.avreceiver.handler.AVReceiverHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericHandler extends AVReceiverHandler {
    private Logger logger = LoggerFactory.getLogger(getClass());

    public GenericHandler(Thing thing, GenericConnection connection) {
        super(thing, connection);
    }

    @Override
    protected String handleChannelCommand(ChannelUID channelUID, Command command) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Message createMessage(ChannelUID channelUID, Command command, String commandStr) {
        // TODO Auto-generated method stub
        return new Message(commandStr);
    }

    @Override
    public void handleRefresh(Command command, Channel channel) {
        // TODO Auto-generated method stub

    }

}
