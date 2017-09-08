/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.avreceiver.handler.comm;

/**
 *
 * @author Tero Lindberg
 *
 */
public class ConversionUtils {
    public static double getVolumeIndB(int percentage, double mindB, double maxdb) {

        return 0;
    }

    public static void main(String[] args) {
        double indb = 20 * Math.log10(100);
        System.out.println("inDB:" + indb);

        double maxdb = 16.5;
        double mindb = -80.0;
        double dBVal = -22.5 - mindb;
        double linVal = Math.pow(10, (dBVal - (maxdb - mindb)) / 20);

        for (double i = 0; i <= 1; i += .1d) {
            LinearToDecibel(i, mindb, maxdb);
        }

    }

    private static double DecibelToLinear(float dB) {
        double linear = Math.pow(10.0f, dB / 20.0f);

        return linear;
    }

    private static double LinearToDecibel(double linear, double mindB, double maxdB) {
        double dB;
        double dBDiff = maxdB - mindB;
        if (linear != 0) {

            dB = (Math.log(linear) / Math.log(8)) * dBDiff + maxdB;
        } else {
            dB = mindB;
        }
        System.out.println("Linear:" + linear + " diff:" + (maxdB - mindB) + " db:" + dB + " log " + Math.log(linear));
        return dB;
    }

    public static double logOfBase(int base, int num) {
        return Math.log(num) / Math.log(base);
    }
}
