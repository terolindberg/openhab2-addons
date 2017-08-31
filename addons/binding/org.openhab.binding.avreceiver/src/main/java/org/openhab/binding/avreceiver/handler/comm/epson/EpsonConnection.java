package org.openhab.binding.avreceiver.handler.comm.epson;

import java.io.IOException;

import org.openhab.binding.avreceiver.handler.comm.AVSocketConnection;

/**
 *
 * @author Tero Lindberg
 *
 */
public class EpsonConnection extends AVSocketConnection {

    public EpsonConnection(String host, int port) {
        super(host, port);
    }

    public static void main(String[] args) {

        EpsonConnection dc = new EpsonConnection("192.168.1.12", 4001);

        // LoggerFactory.getLogger("test").debug("test");

        try {
            dc.startConnection(true);
            dc.sendMessage(new EpsonMessage("MUTE ON"));

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // dc.sendMessage(new EpsonMessage("MVUP"));
            // dc.sendMessage(new EpsonMessage("MVDOWN"));
            // try {
            // Thread.sleep(3000);
            // } catch (InterruptedException e) {
            // // TODO Auto-generated catch block
            // e.printStackTrace();
            // }
            // // dc.sendMessage(new DenonMessage("PWSTANDBY"));
            // try {
            // Thread.sleep(5000);
            // } catch (InterruptedException e) {
            // // TODO Auto-generated catch block
            // e.printStackTrace();
            // }

            dc.disconnect();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // public void datagram() throws Exception {
    // DatagramSocket clientSocket = new DatagramSocket();
    // InetAddress IPAddress = InetAddress.getByName(host);
    // byte[] sendData = new byte[135];
    // byte[] receiveData = new byte[135];
    // sendData = "PWON\r".getBytes();
    // DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
    // clientSocket.send(sendPacket);
    // System.out.println("data sent " + new String(sendData));
    // DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
    // clientSocket.receive(receivePacket);
    // String modifiedSentence = new String(receivePacket.getData());
    // System.out.println("FROM SERVER:" + modifiedSentence);
    // clientSocket.close();
    // }

}
