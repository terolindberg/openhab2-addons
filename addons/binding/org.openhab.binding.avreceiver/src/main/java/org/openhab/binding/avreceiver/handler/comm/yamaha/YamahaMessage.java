package org.openhab.binding.avreceiver.handler.comm.yamaha;

import java.io.IOException;
import java.io.Writer;

import org.openhab.binding.avreceiver.handler.comm.Message;

public class YamahaMessage extends Message {

    public YamahaMessage(String msg) {
        super(msg);
    }

    @Override
    public void writeFooter(Writer writer) throws IOException {

        writer.append((char) 0x0D);
        writer.append((char) 0x0A);
    }
}
