package org.openhab.binding.avreceiver.handler.comm;

public interface ConnectionStateListener {

    void connectionStarted();

    void connectionClosed(boolean fail);
}
