package org.openhab.binding.mopidy.api.ws;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 *
 * @author Tero Lindberg
 *
 */
@WebSocket(maxTextMessageSize = 64 * 1024)
public class MopidyWS {
    MopidyWSListener listener;
    Gson gson = new Gson();
    private final Logger logger = LoggerFactory.getLogger(MopidyWS.class);
    private final CountDownLatch closeLatch;
    @SuppressWarnings("unused")
    private Session session;

    public MopidyWS() {
        this.closeLatch = new CountDownLatch(1);
    }

    public boolean awaitClose(int duration, TimeUnit unit) throws InterruptedException {
        return this.closeLatch.await(duration, unit);
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        logger.debug("Connection closed: {} - {}", statusCode, reason);
        this.session = null;
        this.closeLatch.countDown(); // trigger latch
        if (listener != null) {
            listener.connectionClosed();
        }
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        logger.debug("Got connect: {}", session.getRemoteAddress());
        this.session = session;

    }

    @OnWebSocketMessage
    public void onMessage(String msg) {
        logger.debug("Got msg: {}", msg);
        MopidyWSEvent object = gson.fromJson(msg, MopidyWSEvent.class);

        if (listener != null) {

            listener.onMopidyEvent(object);
            if (object.getNewState() != null) {
                listener.onStateChange(object.getOldState(), object.getNewState());
            } else if (object.getTLTrack() != null) {
                listener.onCurrentTrack(object.getTLTrack().getTrack());
            }
        }
    }

    public void setWSEventListener(MopidyWSListener listener) {
        this.listener = listener;
    }
}