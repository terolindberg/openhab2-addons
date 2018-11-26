package org.openhab.binding.avreceiver.handler.comm;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageSender implements Runnable {

    private Logger logger = LoggerFactory.getLogger(MessageSender.class);
    List<Message> queue;
    private OutputStreamWriter bw;
    boolean running;
    ConnectionStateListener listener;

    public MessageSender(OutputStreamWriter bw, ConnectionStateListener listener) {
        queue = new ArrayList<>();
        this.bw = bw;
        running = true;
        this.listener = listener;

    }

    @Override
    public void run() {
        logger.trace("Starting message sender");
        while (running) {
            synchronized (queue) {

                if (queue.isEmpty()) {
                    try {
                        logger.trace("Waiting for queue to be added");
                        queue.wait();
                        // logger.debug("Queue handler released");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            Message msg = null;
            synchronized (queue) {

                if (!queue.isEmpty()) {
                    msg = queue.get(0);
                    logger.debug("Sending message {}'{}'", msg.getClass().getSimpleName(), msg.toString());

                    try {

                        msg.writeHeader(bw);
                        msg.writeMessage(bw);
                        msg.writeFooter(bw);
                        bw.flush();
                        queue.remove(0);
                        logger.trace("Message '{}' sent", msg.toString());

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        queue.remove(0);
                    }

                }
            }
            if (msg != null && msg.getPostSendDelayMs() > 0) {
                try {
                    Thread.sleep(msg.getPostSendDelayMs());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        listener.connectionClosed(running);
        logger.trace("Sender stopped");

    }

    public void send(Message msg) {
        synchronized (queue) {
            logger.trace("Adding to queue size: " + queue.size());
            queue.add(msg);
            queue.notify();
        }
    }

    /**
     * Stream closing is left to the owner of the stream
     */
    public void stop() {
        logger.trace("Stopping MessageSender");
        running = false;

        synchronized (queue) {
            queue.notify();
        }

    }

}
