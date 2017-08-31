package org.openhab.binding.avreceiver.handler.comm;

/**
 *
 * @author Tero Lindberg
 *
 */
public interface ConnectionStateListener {

    void connectionStarted();

    void connectionClosed(boolean fail);
}
