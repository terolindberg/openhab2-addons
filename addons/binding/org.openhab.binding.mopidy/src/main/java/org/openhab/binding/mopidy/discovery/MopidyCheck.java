/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.mopidy.discovery;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 *
 *
 * @author Tero Lindberg
 */
class MopidyCheck implements Runnable {
    final String ip;
    final MopidyDiscoveryService service;

    public MopidyCheck(String ip, MopidyDiscoveryService service) {
        this.ip = ip;
        this.service = service;
        if (ip == null) {
            throw new RuntimeException("We need an IP to work on this... IP null");
        }
    }

    @Override
    public void run() {
        try {

            Socket s = new Socket();//ip, 6680);

            s.setSoTimeout(200);
            s.connect(new InetSocketAddress(ip, 6680), 200);
            OutputStream os = s.getOutputStream();
            InputStream is = s.getInputStream();
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
            }
            try {
                if (os != null) {
                    os.close();
                }
            } catch (Exception e) {

            }
            try {
                if (s != null) {
                    s.close();
                }
            } catch (Exception e) {

            }

            service.newMopidyFound(ip);
        } catch (Exception e) {
            service.notFound(ip);
        }
    }
}
