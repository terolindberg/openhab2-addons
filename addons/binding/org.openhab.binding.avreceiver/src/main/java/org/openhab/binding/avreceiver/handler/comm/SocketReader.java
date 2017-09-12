/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.avreceiver.handler.comm;

import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Tero Lindberg
 *
 */
public class SocketReader implements Runnable {

    private Logger logger = LoggerFactory.getLogger(getClass());

    InputStreamReader br;
    boolean m_run = true;
    MessageHandler m_handler;
    ConnectionStateListener listener;

    public SocketReader(InputStreamReader br, MessageHandler mh, ConnectionStateListener listener) {
        this.br = br;
        m_handler = mh;
        this.listener = listener;
    }

    public void setMessageHandler(MessageHandler handler) {
        m_handler = handler;
    }

    @Override
    public void run() {
        String line = null;
        try {
            logger.trace("waiting for data");
            StringBuffer sb = new StringBuffer();
            int i;
            char c;
            while (m_run && (i = br.read()) != -1) {
                c = (char) i;
                logger.trace("CHAR:(0x{})'{}'", Integer.toHexString(i), c);
                if (c == '\n' || c == '\r') {
                    if (m_handler != null) {
                        m_handler.handleMessage(sb.toString().trim());
                        sb = new StringBuffer();
                    }
                } else {
                    sb.append(c);
                }

            }
            // while (m_run && (line = br.readLine()) != null) {
            // if (m_handler != null) {
            // m_handler.handleMessage(line);
            // } else {
            // // ERRROR
            // }
            // }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            if (m_run) {
            } else {
            }
            logger.info("Reader stopped");

        }

        logger.trace("{} Reader stopped {} last received:{}", m_handler, m_run, line);
        listener.connectionClosed(m_run);

    }

    /**
     * The stream closing is left to the owner of the stream
     */
    public void stop() {
        m_run = false;

    }
}
