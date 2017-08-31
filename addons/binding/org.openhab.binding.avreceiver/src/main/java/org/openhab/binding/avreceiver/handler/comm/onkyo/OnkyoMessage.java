package org.openhab.binding.avreceiver.handler.comm.onkyo;

import java.io.IOException;
import java.io.Writer;

import org.openhab.binding.avreceiver.handler.comm.Message;

/**
 *
 * @author Tero Lindberg
 *
 */
public class OnkyoMessage extends Message {

    public OnkyoMessage(String msg) {
        super(msg);

    }

    @Override
    public void writeHeader(Writer writer) throws IOException {

        // writer.append((char) 0x0D);

        // writer.write('\r');
        // writer.flush();
        // writer.write('\n');

    }

    @Override
    public void writeMessage(Writer writer) throws IOException {
        String commandPart = getCommandPart();

        if (!commandPart.startsWith("!1")) {
            commandPart = "!1" + commandPart;
        }
        char pad = (char) (commandPart.length() + 1 + 16);
        String cmd = "ISCP" + (char) 0x00 + (char) 0x00 + (char) 0x00 + (char) 0x10 + (char) 0x00 + (char) 0x00
                + (char) 0x00 + pad + (char) 0x01 + (char) 0x00 + (char) 0x00 + (char) 0x00 + commandPart + (char) 0x0D;
        writer.write(cmd);
    }

    @Override
    public void writeFooter(Writer writer) throws IOException {

        // writer.append((char) 0x0D);

        writer.write('\r');
        // writer.write('\n');

    }

}
