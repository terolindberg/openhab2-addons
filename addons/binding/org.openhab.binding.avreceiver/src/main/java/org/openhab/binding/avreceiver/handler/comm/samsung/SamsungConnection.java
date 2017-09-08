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
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Base64;

import org.openhab.binding.avreceiver.handler.comm.AVSocketConnection;

/**
 *
 * @author Tero Lindberg
 *
 */
public class SamsungConnection extends AVSocketConnection {

    public SamsungConnection(String host, int port) {
        super(host, port);
    }

    String mac = "8C-70-5A-C8-11-28"; // # mac of remote
    String remote = "OpenHAB AV";// # remote name
    static String app = "iphone.iapp.samsung"; // # iphone..iapp.samsung

    @Override
    public void initConnection(OutputStreamWriter bw, InputStreamReader br) throws IOException {
        String ip = getSocket().getLocalAddress().getHostAddress();

        String base64ip = Base64.getEncoder().encodeToString(ip.getBytes());
        String base64mac = Base64.getEncoder().encodeToString(mac.getBytes());
        String base64remote = Base64.getEncoder().encodeToString(remote.getBytes());

        String msg = (char) 0x64 + "" + (char) 0x00 + "" + (char) base64ip.length() + "" + (char) 0x00 + base64ip
                + (char) base64mac.length() + "" + (char) 0x00 + base64mac + (char) base64remote.length() + ""
                + (char) 0x00 + base64remote;
        String pkt = (char) 0x00 + "" + (char) app.length() + "" + (char) 0x00 + app + (char) msg.length() + ""
                + (char) 0x00 + msg;
        System.out.println("Sending " + pkt);
        bw.write(pkt);
        bw.flush();

        msg = (char) 0xc8 + "" + (char) 0x00;
        pkt = (char) 0x00 + "" + (char) app.length() + "" + (char) 0x00 + app + (char) msg.length() + "" + (char) 0x00
                + msg;
        System.out.println("Sending " + pkt);
        bw.write(pkt);
        bw.flush();

    }

}
