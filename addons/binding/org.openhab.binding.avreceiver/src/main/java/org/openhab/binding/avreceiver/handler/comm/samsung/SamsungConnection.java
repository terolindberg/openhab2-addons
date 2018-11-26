package org.openhab.binding.avreceiver.handler.comm.samsung;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Base64;

import org.openhab.binding.avreceiver.handler.comm.AVSocketConnection;
import org.openhab.binding.avreceiver.handler.comm.ConnectionStateListener;
import org.openhab.binding.avreceiver.handler.comm.MessageHandler;
import org.openhab.binding.avreceiver.handler.comm.SocketReader;

public class SamsungConnection extends AVSocketConnection {

    public SamsungConnection(String host, int port) {
        super(host, port);
    }

    String src = "192.168.1.128"; // # ip of remote
    String mac = "8C-70-5A-C8-11-28"; // # mac of remote
    String remote = "Munkapula";// # remote name
    String app = "iphone..iapp.samsung"; // # iphone..iapp.samsung
    String tv = "iphone.LE32C650.iapp.samsung"; // # iphone.LE32C650.iapp.samsung
    BufferedWriter bw;
    InputStream is;
    OutputStream os;
    Socket socket;
    SocketReader reader;

    /**
     * @param args
     */
    public static void main(String[] args) {
        final SamsungConnection ss = new SamsungConnection("192.168.1.133", 55000);

        try {
            ss.startConnection(true);
            ss.send("KEY_DOWN");
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            SocketReader sr = new SocketReader(new InputStreamReader(System.in), new MessageHandler() {

                @Override
                public void handleMessage(String message) {
                    System.out.println("MESSAGE:" + message);
                    try {
                        ss.send("KEY_" + message);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            }, new ConnectionStateListener() {

                @Override
                public void connectionStarted() {
                    // TODO Auto-generated method stub

                }

                @Override
                public void connectionClosed(boolean fail) {
                    // TODO Auto-generated method stub

                }
            });
            new Thread(sr).start();

            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // ss.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }

    }

    void close() {

        try {
            reader.stop();
            bw.close();
            is.close();
            os.close();
            socket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public void initConnection(BufferedWriter bw, BufferedReader br) throws IOException {
        String base64src = Base64.getEncoder().encodeToString(src.getBytes());
        String base64mac = Base64.getEncoder().encodeToString(mac.getBytes());
        String base64remote = Base64.getEncoder().encodeToString(remote.getBytes());

        String msg = (char) 0x64 + "" + (char) 0x00 + "" + (char) base64src.length() + "" + (char) 0x00 + base64src
                + (char) base64mac.length() + "" + (char) 0x00 + base64mac + (char) base64remote.length() + ""
                + (char) 0x00 + base64remote;
        String pkt = (char) 0x00 + "" + (char) app.length() + "" + (char) 0x00 + app + (char) msg.length() + ""
                + (char) 0x00 + msg;
        System.out.println("Sending " + pkt);
        bw.write(pkt);
        bw.flush();

        msg = (char) 0xc8 + "" + (char) 0x00;
        pkt = (char) 0x00 + "" + (char) app.length() + "" + (char) 0x00 + app + (char) msg.length() + "" + (char) 0x00
                + msg;
        System.out.println("Sending " + pkt);
        bw.write(pkt);
        bw.flush();

    }

    public void send(String key) throws IOException {
        String base64key = Base64.getEncoder().encodeToString(key.getBytes());

        String msg = (char) 0x00 + "" + (char) 0x00 + "" + (char) 0x00 + "" + (char) base64key.length() + ""
                + (char) 0x00 + base64key;
        String pkt = (char) 0x00 + "" + (char) tv.length() + "" + (char) 0x00 + tv + (char) msg.length() + ""
                + (char) 0x00 + msg;
        System.out.println("Sending " + pkt);
        bw.write(pkt);
        bw.flush();
        System.out.println(key + " sent");

    }

}
