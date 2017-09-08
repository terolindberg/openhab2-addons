/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.avreceiver.handler.comm.denon;

import org.openhab.binding.avreceiver.handler.comm.AVSocketConnection;

/**
 * @author Tero Lindberg
 *
 */
public class DenonConnection extends AVSocketConnection {

    public DenonConnection(String host, int port) {
        super(host, port);
    }

    @Override
    protected int getBufferSize() {
        return 135;
    }

}
