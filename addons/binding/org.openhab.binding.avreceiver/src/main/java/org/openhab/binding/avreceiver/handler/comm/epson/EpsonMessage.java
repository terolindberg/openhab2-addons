/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.avreceiver.handler.comm.epson;

import java.io.IOException;
import java.io.Writer;

import org.openhab.binding.avreceiver.handler.comm.Message;

/**
 *
 * @author Tero Lindberg
 *
 */
public class EpsonMessage extends Message {

    public EpsonMessage(String msg) {
        super(msg);

    }

    @Override
    public void writeHeader(Writer writer) throws IOException {

    }

    @Override
    public void writeFooter(Writer writer) throws IOException {

        writer.write('\r');

    }

}
