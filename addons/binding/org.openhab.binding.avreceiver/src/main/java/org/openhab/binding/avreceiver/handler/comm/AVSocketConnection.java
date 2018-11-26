package org.openhab.binding.avreceiver.handler.comm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.openhab.binding.avreceiver.handler.AVReceiverHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AVSocketConnection implements ConnectionStateListener, MessageHandler {

    private Logger logger = LoggerFactory.getLogger(getClass());

    protected String host;
    protected int port;
    Socket socket;
    boolean running;
    InputStreamReader br;
    OutputStreamWriter bw;
    MessageSender sender;
    SocketReader reader;
    Thread readerThread;
    Thread senderThread;
    AVReceiverHandler handler;
    TimerTask timeout;
    Timer timeoutTimer;
    boolean resend = true;

    public AVSocketConnection(String host, int port) {
        this.port = port;
        this.host = host;

        timeoutTimer = new Timer();
    }

    public void setThingHandler(AVReceiverHandler handler) {
        this.handler = handler;
    }

    public void startConnection(boolean keepalive) throws UnknownHostException, IOException {

        boolean success = false;
        if (keepalive) {

            try {
                connect();

            } catch (Exception e) {
                connectionClosed(true);
                throw e;
            }

        } else {
            connect();
        }

    }

    protected int getBufferSize() {
        return 1024;
    }

    private void connect() throws UnknownHostException, IOException {
        lastConnectionTrial = System.currentTimeMillis();
        running = true;
        logger.debug("Connecting to {}:{}", host, port);
        socket = new Socket(host, port);
        socket.setReceiveBufferSize(getBufferSize());
        socket.setSendBufferSize(getBufferSize());
        bw = new OutputStreamWriter(socket.getOutputStream());// , getBufferSize();
        br = new InputStreamReader(socket.getInputStream());// , getBufferSize());
        socket.setKeepAlive(true);
        // socket.setSoTimeout(250);

        logger.debug("Connected to {}:{}", host, port);

        reader = new SocketReader(br, this, this);
        readerThread = new Thread(reader);
        readerThread.start();

        sender = new MessageSender(bw, this);
        senderThread = new Thread(sender);
        senderThread.start();
        if (handler != null) {
            handler.updateStatus(ThingStatus.ONLINE);
        }

    }

    public void disconnect() {
        if (lastConnectionTrial + connectionDurationOKInterval < System.currentTimeMillis()) {
            retryTime = MIN_RETRY_WAIT;
        } else {
            logger.trace("Connection didn't stay open, let's wait a bit before continuing");
        }
        logger.trace("Disconnecting");
        running = false;
        if (sender != null) {
            sender.stop();
        }
        if (reader != null) {
            reader.stop();
        }
        try {
            if (bw != null) {
                bw.close();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            if (br != null) {
                br.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (socket != null) {
                socket.close();
            }
            logger.debug("Socket Closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (handler != null) {
            handler.updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, "Connection Closed");
        }

    }

    /**
     * Overwrite if connection needs initialization before communication is started
     *
     * @param bw
     * @param br
     * @throws IOException
     */
    protected void initConnection(BufferedWriter bw, BufferedReader br) throws IOException {

    }

    public void sendMessage(final Message msg) {
        sender.send(msg);

        if (msg.getTimeoutMs() > 0) {
            synchronized (timeoutTimer) {
                if (timeout != null) {
                    timeout.cancel();
                }
                timeout = new TimerTask() {

                    @Override
                    public void run() {
                        logger.debug("Message TimeOut! setTo:{}ms", msg.getTimeoutMs());
                        connectionClosed(true, msg.shouldRetry() ? msg : null);
                    }
                };
                timeoutTimer.schedule(timeout, msg.getTimeoutMs());

            }
        }
    }

    @Override
    public void connectionStarted() {
        retryTime = MIN_RETRY_WAIT;
    }

    static int MAX_RETRY_WAIT = 60000;
    static int MIN_RETRY_WAIT = 0;
    static double RETRY_WAIT_STEP_FACTOR = 1.5;
    static int RETRY_WAIT_STEP = 1000;
    int retryTime = MIN_RETRY_WAIT;
    long lastConnectionTrial;
    int connectionDurationOKInterval = 2000;

    @Override
    public void connectionClosed(boolean fail) {
        connectionClosed(fail, null);
    }

    /**
     *
     * @param fail
     * @param message, reconnect and bruteforce the message (Denon tcp is shaky)
     */
    public void connectionClosed(boolean fail, final Message message) {

        if (fail && running) {

            new Thread(new Runnable() {

                @Override
                public void run() {
                    if (message != null) {
                        retryTime = MIN_RETRY_WAIT;
                    } else {
                        retryTime = (int) ((retryTime + RETRY_WAIT_STEP) * RETRY_WAIT_STEP_FACTOR);
                    }
                    if (retryTime > MAX_RETRY_WAIT) {
                        retryTime = MAX_RETRY_WAIT;
                    }

                    disconnect();
                    try {
                        logger.debug("Waiting for {}ms to reconnect", retryTime);
                        Thread.sleep(retryTime);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    try {
                        logger.debug("Reconnecting.... Failed message:{} {}", message,
                                message != null ? message.getRemainingRetries() : "N/A");
                        connect();
                        if (resend && message != null) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                            }
                            sendMessage(message);
                        }
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        connectionClosed(true, message);
                    }

                }
            }).start();

        }

    }

    @Override
    public void handleMessage(String message) {

        synchronized (timeoutTimer) {
            if (timeout != null) {
                timeout.cancel();
                timeout = null;
            }

        }
        if (handler != null) {
            handler.handleMessage(message);
        } else {
            System.out.println(message);
        }

    }
}
