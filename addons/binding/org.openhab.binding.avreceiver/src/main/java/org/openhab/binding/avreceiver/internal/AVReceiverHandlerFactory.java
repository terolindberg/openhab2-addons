/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.avreceiver.internal;

import static org.openhab.binding.avreceiver.AVReceiverBindingConstants.*;

import java.math.BigDecimal;
import java.util.Map;

import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.openhab.binding.avreceiver.discovery.AVReceiverDiscoveryParticipant;
import org.openhab.binding.avreceiver.handler.comm.GenericConnection;
import org.openhab.binding.avreceiver.handler.comm.GenericHandler;
import org.openhab.binding.avreceiver.handler.comm.denon.DenonConnection;
import org.openhab.binding.avreceiver.handler.comm.denon.DenonHandler;
import org.openhab.binding.avreceiver.handler.comm.epson.EpsonConnection;
import org.openhab.binding.avreceiver.handler.comm.epson.EpsonHandler;
import org.openhab.binding.avreceiver.handler.comm.onkyo.OnkyoConnection;
import org.openhab.binding.avreceiver.handler.comm.onkyo.OnkyoHandler;
import org.openhab.binding.avreceiver.handler.comm.samsung.SamsungConnection;
import org.openhab.binding.avreceiver.handler.comm.samsung.SamsungHandler;
import org.openhab.binding.avreceiver.handler.comm.yamaha.YamahaConnection;
import org.openhab.binding.avreceiver.handler.comm.yamaha.YamahaHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link AVReceiverHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Tero Lindberg - Initial contribution
 */
public class AVReceiverHandlerFactory extends BaseThingHandlerFactory {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return AVReceiverDiscoveryParticipant.supportedThingTypes.contains(thingTypeUID);
        // return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    protected ThingHandler createHandler(Thing thing) {

        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        //
        // if (thingTypeUID.equals(THING_TYPE_YAMAHA)) {
        // return new AVReceiverHandler(thing);
        // } else if(thingTypeUID.equals(THING_TYPE_DENON)){
        // return new AVReceive
        // }
        // logger.

        Map<String, String> props = thing.getProperties();

        for (String key : thing.getConfiguration().keySet()) {
            logger.debug("Conf: {}:{}", key, thing.getConfiguration().get(key));

        }
        for (String key : props.keySet()) {
            logger.debug("Props: {}:{}", key, props.get(key));
        }
        String host = (String) thing.getConfiguration().get(CONFIG_HOST_NAME);
        int port = 0;

        try {
            port = ((BigDecimal) thing.getConfiguration().get(CONFIG_HOST_SOCKET_PORT)).intValue();
        } catch (NumberFormatException e) {
            logger.error("ERROR", e);
            return null;
        }

        if (thingTypeUID.equals(THING_TYPE_DENON)) {
            return new DenonHandler(thing, new DenonConnection(host, port));
        } else if (thingTypeUID.equals(THING_TYPE_SAMSUNG)) {
            return new SamsungHandler(thing, new SamsungConnection(host, port));
        } else if (thingTypeUID.equals(THING_TYPE_YAMAHA)) {
            return new YamahaHandler(thing, new YamahaConnection(host, port));
        } else if (thingTypeUID.equals(THING_TYPE_EPSON_PROJECTOR)) {
            return new EpsonHandler(thing, new EpsonConnection(host, port));
        } else if (thingTypeUID.equals(THING_TYPE_ONKYO)) {
            return new OnkyoHandler(thing, new OnkyoConnection(host, port));
        } else {
            return new GenericHandler(thing, new GenericConnection(host, port));
        }
    }

    @Override
    protected void removeHandler(ThingHandler thingHandler) {
        super.removeHandler(thingHandler);
    }
}
