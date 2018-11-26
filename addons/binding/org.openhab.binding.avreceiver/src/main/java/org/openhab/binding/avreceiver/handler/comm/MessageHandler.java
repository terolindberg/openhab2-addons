package org.openhab.binding.avreceiver.handler.comm;

public interface MessageHandler {
    /**
     * Received message from the connection
     * 
     * @param message
     */
    public void handleMessage(String message);
}