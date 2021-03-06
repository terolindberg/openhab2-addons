package org.openhab.binding.avreceiver.handler.comm.yamaha;

import java.io.IOException;

import org.openhab.binding.avreceiver.handler.comm.AVSocketConnection;
import org.openhab.binding.avreceiver.handler.comm.denon.DenonMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YamahaConnection extends AVSocketConnection {

    private Logger logger = LoggerFactory.getLogger(getClass());

    public YamahaConnection(String host, int port) {
        super(host, port);
        // TODO Auto-generated constructor stub
    }

    public static void main(String[] args) {
        YamahaConnection dc = new YamahaConnection("192.168.1.108", 23);

        try {
            dc.startConnection(true);
            dc.sendMessage(new DenonMessage("PWON"));

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            dc.sendMessage(new DenonMessage("MVUP"));
            dc.sendMessage(new DenonMessage("MVDOWN"));
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // dc.sendMessage(new DenonMessage("PWSTANDBY"));
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            dc.disconnect();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
