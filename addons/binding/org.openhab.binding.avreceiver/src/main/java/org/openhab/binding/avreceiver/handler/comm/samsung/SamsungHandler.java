/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.avreceiver.handler.comm.samsung;

import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.avreceiver.handler.AVReceiverHandler;
import org.openhab.binding.avreceiver.handler.comm.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Tero Lindberg
 *
 */
public class SamsungHandler extends AVReceiverHandler {
    private Logger logger = LoggerFactory.getLogger(getClass());

    public SamsungHandler(Thing thing, SamsungConnection connection) {
        super(thing, connection);

    }

    @Override
    protected String handleChannelCommand(ChannelUID channelUID, Command command) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Message createMessage(ChannelUID channelUID, Command command, String commandStr) {
        // TODO Auto-generated method stub
        return new SamsungMessage(commandStr);
    }

}
