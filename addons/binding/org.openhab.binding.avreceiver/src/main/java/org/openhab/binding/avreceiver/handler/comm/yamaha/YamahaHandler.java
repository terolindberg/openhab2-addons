package org.openhab.binding.avreceiver.handler.comm.yamaha;

import java.util.Map;

import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.avreceiver.handler.AVReceiverHandler;
import org.openhab.binding.avreceiver.handler.comm.AVSocketConnection;
import org.openhab.binding.avreceiver.handler.comm.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YamahaHandler extends AVReceiverHandler {

    private Logger logger = LoggerFactory.getLogger(getClass());

    public YamahaHandler(Thing thing, AVSocketConnection connection) {
        super(thing, connection);

    }

    @Override
    public void handleRefresh(Command command, Channel channel) {
        // TODO Auto-generated method stub
        Map<String, String> props = channel.getProperties();

        if (props.containsKey("value")) {
            String message = props.get("value");

            message = message.replaceAll("\\{\\}", "?");
            getConnection().sendMessage(createMessage(channel.getUID(), command, message));
        }
    }

    @Override
    protected String handleChannelCommand(ChannelUID channelUID, Command command) {
        // TODO Auto-generated method stub
        return null;
        // Channel channel = getThing().getChannel(channelUID.getId());
        // for (String key : channel.getConfiguration().keySet()) {
        // logger.debug("Channel config {} {}:{}", channel.getChannelTypeUID().getId(), key,
        // channel.getConfiguration().get(key));
        //
        // }
        //
        // Map<String, String> props = getThing().getChannel(channelUID.getId()).getProperties();
        //
        // if (props.containsKey("value")) {
        // String message = props.get("value");
        // if (message.contains("{}")) {
        // message = message.replaceAll("\\{\\}", command.toString());
        //
        // }
        // logger.debug("Would send {}", message);
        //
        // // getConnection().sendMessage(new YamahaMessage(message));
        // }
        // if (channelUID.getId().equals(CHANNEL_POWER)) {
        // logger.debug("Command {} full {}", command.toString(), command.toFullString());
        //
        // getConnection().sendMessage(new YamahaMessage(props.get(command.toString())));
        //
        // logger.debug("Channel: {} {}", getThing().getChannel(channelUID.getId()), getThing().getClass());
        //
        // // connection.sendMessage(new DenonMessage("PWON"));
        // // TODO: handle command
        // // connection.sendMessage();
        // // Note: if communication with thing fails for some reason,
        // // indicate that by setting the status with detail information
        // // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
        // // "Could not control device at IP address x.x.x.x");
        //
        // }
    }

    public static void main(String[] args) {
        String message = "@MAIN:VOL={}";
        if (message.contains("{}")) {
            message = "r" + message.replaceAll("\\{\\}", "23");

        }
        System.out.println(message);
    }

    @Override
    protected Message createMessage(ChannelUID channelUID, Command command, String commandStr) {
        return new YamahaMessage(commandStr);
    }

}
