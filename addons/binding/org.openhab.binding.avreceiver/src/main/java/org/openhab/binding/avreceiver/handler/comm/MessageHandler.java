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