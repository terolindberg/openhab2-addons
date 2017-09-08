/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.avreceiver.handler.comm.denon;

import java.io.IOException;
import java.io.Writer;

import org.openhab.binding.avreceiver.handler.comm.Message;

/**
 *
 * @author Tero Lindberg
 *
 */
public class DenonMessage extends Message {

    public DenonMessage(String msg) {
        super(msg);

    }

    @Override
    public void writeFooter(Writer writer) throws IOException {

        writer.append((char) 0x0D);
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
