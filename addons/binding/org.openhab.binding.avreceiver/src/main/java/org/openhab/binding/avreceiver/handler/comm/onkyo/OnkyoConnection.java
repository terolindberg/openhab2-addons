package org.openhab.binding.avreceiver.handler.comm.onkyo;

import org.openhab.binding.avreceiver.handler.comm.AVSocketConnection;

/**
 *
 * @author Tero Lindberg
 *
 */
public class OnkyoConnection extends AVSocketConnection {

    public OnkyoConnection(String host, int port) {
        super(host, port);
    }

}
