/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.avreceiver.handler.comm;

/**
 * MessageHandler interface can be given to {@link SocketReader} to pass along the messages
 * Messages are sent as plain text and should be decoded to proper {@link Message} type by
 * the implementation if needed
 *
 * @author Tero Lindberg
 *
 */
public interface MessageHandler {
    /**
     * Received message from the connection
     *
     * @param message
     */
    public void handleMessage(String message);
}