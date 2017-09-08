/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
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

    public SamsungMessage(String message) {
        super(message);
    }

    @Override
    public void writeMessage(Writer writer) throws IOException {
        String base64key = Base64.getEncoder().encodeToString(message.getBytes());

        String msg = (char) 0x00 + "" + (char) 0x00 + "" + (char) 0x00 + "" + (char) base64key.length() + ""
                + (char) 0x00 + base64key;
        String pkt = (char) 0x00 + "" + (char) SamsungConnection.app.length() + "" + (char) 0x00 + SamsungConnection.app
                + (char) msg.length() + "" + (char) 0x00 + msg;
        System.out.println("Sending (" + message + ") -> '" + pkt + "'");
        writer.write(pkt);
    }

}
