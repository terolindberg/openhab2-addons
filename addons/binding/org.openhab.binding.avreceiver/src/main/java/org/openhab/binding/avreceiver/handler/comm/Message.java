package org.openhab.binding.avreceiver.handler.comm;

import java.io.IOException;
import java.io.Writer;

public class Message {
    protected String message;
    private static int MAX_RETRIES = 5;
    private int count = MAX_RETRIES;

    /**
     * Delay required by device before sending next message.
     *
     * Eg. Power on may need a few seconds to be executed by the device and
     * we need to wait for a while before sending next command
     */
    private int postSendDelayMs = 100;

    public Message(String msg) {
        message = msg;
    }

    public String getCommandPart() {
        return message;
    }

    public String getParameterPart() {
        return message;
    }

    public void writeHeader(Writer writer) throws IOException {

    }

    public void writeMessage(Writer writer) throws IOException {
        writer.write(message);
    }

    public void writeFooter(Writer writer) throws IOException {
    }

    public int getTimeoutMs() {
        return 0;
    }

    public boolean shouldRetry() {
        return count-- > 0;// MAX_RETRIES;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Message) && ((Message) o).message.equals(message);
    }

    @Override
    public int hashCode() {
        return message.hashCode();
    }

    @Override
    public String toString() {
        return message;
    }

    public int getRemainingRetries() {
        // TODO Auto-generated method stub
        return count;
    }

    public int getPostSendDelayMs() {
        return postSendDelayMs;
    }

    public void setPostSendDelayMs(int postSendDelayMs) {
        this.postSendDelayMs = postSendDelayMs;
    }

}
