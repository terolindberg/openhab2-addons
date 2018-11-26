package org.openhab.binding.avreceiver.handler.comm.onkyo;

import java.io.IOException;
import java.io.Writer;

import org.openhab.binding.avreceiver.handler.comm.Message;

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
    public void writeFooter(Writer writer) throws IOException {

        // writer.append((char) 0x0D);

        writer.write('\r');
        // writer.write('\n');

    }

}
