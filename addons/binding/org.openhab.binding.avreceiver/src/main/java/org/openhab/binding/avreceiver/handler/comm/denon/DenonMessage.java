package org.openhab.binding.avreceiver.handler.comm.denon;

import java.io.IOException;
import java.io.Writer;

import org.openhab.binding.avreceiver.handler.comm.Message;

public class DenonMessage extends Message {

    public DenonMessage(String msg) {
        super(msg);

    }

    @Override
    public void writeFooter(Writer writer) throws IOException {

        writer.append((char) 0x0D);
        // writer.write('\r');

    }

    @Override
    public String getCommandPart() {
        return message.substring(0, 2);
    }

    @Override
    public String getParameterPart() {
        return message.substring(2);
    }

    @Override
    public int getTimeoutMs() {
        return message.equals("PWON") ? 1500 : 250;
    }

}
