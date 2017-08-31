package org.openhab.binding.avreceiver.handler.comm.samsung;

import java.io.IOException;
import java.io.Writer;
import java.util.Base64;

import org.openhab.binding.avreceiver.handler.comm.Message;

/**
 *
 * @author Tero Lindberg
 *
 */
public class SamsungMessage extends Message {
    static final String tv = "iphone.LE32C650.iapp.samsung";

    public SamsungMessage(String message) {
        super(message);
    }

    @Override
    public void writeMessage(Writer writer) throws IOException {
        String base64key = Base64.getEncoder().encodeToString(message.getBytes());

        String msg = (char) 0x00 + "" + (char) 0x00 + "" + (char) 0x00 + "" + (char) base64key.length() + ""
                + (char) 0x00 + base64key;
        String pkt = (char) 0x00 + "" + (char) tv.length() + "" + (char) 0x00 + tv + (char) msg.length() + ""
                + (char) 0x00 + msg;
        System.out.println("Sending " + pkt);
        writer.write(pkt);
    }

}
