/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.avreceiver.handler.comm.denon;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.openhab.binding.avreceiver.handler.comm.AVSocketConnection;

/**
 * @author Tero Lindberg
 *
 */
public class DenonConnection extends AVSocketConnection {

    public DenonConnection(String host, int port) {
        super(host, port);
    }

    @Override
    protected int getBufferSize() {
        return 135;
    }

    public static void main(String[] args) {

        DenonConnection dc = new DenonConnection("192.168.1.108", 23);
        try {
            dc.datagram();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // LoggerFactory.getLogger("test").debug("test");

        // try {
        // dc.startConnection(true);
        // dc.sendMessage(new DenonMessage("PWON"));
        //
        // try {
        // Thread.sleep(1000);
        // } catch (InterruptedException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        // dc.sendMessage(new DenonMessage("MVUP"));
        // dc.sendMessage(new DenonMessage("MVDOWN"));
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
        //
        // dc.disconnect();
        // } catch (IOException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
    }

    public void datagram() throws Exception {
        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress IPAddress = InetAddress.getByName(host);
        byte[] sendData = new byte[135];
        byte[] receiveData = new byte[135];
        sendData = "PWON\r".getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
        clientSocket.send(sendPacket);
        System.out.println("data sent " + new String(sendData));
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        String modifiedSentence = new String(receivePacket.getData());
        System.out.println("FROM SERVER:" + modifiedSentence);
        clientSocket.close();
    }

}
