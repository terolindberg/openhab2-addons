package org.openhab.binding.avreceiver.handler.comm.denon;

import static org.openhab.binding.avreceiver.AVReceiverBindingConstants.*;

import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.avreceiver.handler.AVReceiverHandler;
import org.openhab.binding.avreceiver.handler.comm.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DenonHandler extends AVReceiverHandler {
    private Logger logger = LoggerFactory.getLogger(getClass());

    public DenonHandler(Thing thing, DenonConnection connection) {
        super(thing, connection);

    }

    @Override
    protected String handleChannelCommand(ChannelUID channelUID, Command command) {

        return null;
    }

    @Override
    protected Message createMessage(ChannelUID channelUID, Command command, String commandStr) {
        // TODO Auto-generated method stub
        DenonMessage message = new DenonMessage(commandStr);
        if (channelUID.getId().equals(CHANNEL_POWER)) {
            message.setPostSendDelayMs(1500);
        }
        return message;
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

    @Override
    public void handleRefresh(Command command, Channel channel) {
        // TODO Auto-generated method stub

    }

}
