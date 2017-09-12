/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.avreceiver.discovery;

import static org.openhab.binding.avreceiver.AVReceiverBindingConstants.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.config.discovery.UpnpDiscoveryParticipant;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.jupnp.model.meta.RemoteDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link AVReceiverDiscoveryParticipant} is responsible for processing the
 * results of searched UPnP devices
 *
 * @author David Gräff - Initial contribution
 * @author Tero Lindberg
 */
public class AVReceiverDiscoveryParticipant implements UpnpDiscoveryParticipant {

    private Logger logger = LoggerFactory.getLogger(AVReceiverDiscoveryParticipant.class);
    public static final Set<ThingTypeUID> supportedThingTypes;
    static {
        supportedThingTypes = new HashSet<>();
        supportedThingTypes.add(THING_TYPE_GENERAL_RENDERER);
        supportedThingTypes.add(THING_TYPE_DENON);
        supportedThingTypes.add(THING_TYPE_YAMAHA);
        supportedThingTypes.add(THING_TYPE_SAMSUNG);
        supportedThingTypes.add(THING_TYPE_ONKYO);
        supportedThingTypes.add(THING_TYPE_EPSON_PROJECTOR);
        supportedThingTypes.add(THING_TYPE_PHILIPS);

    }

    @Override
    public Set<ThingTypeUID> getSupportedThingTypeUIDs() {
        return supportedThingTypes;
    }

    @Override
    public DiscoveryResult createResult(RemoteDevice device) {

        ThingUID uid = getThingUID(device);
        if (uid == null) {
            return null;
        }
        logger.debug("Found Supported Device : {} - {}", device.getDetails().getManufacturerDetails().getManufacturer(),
                device.getDetails().getModelDetails().getModelName());
        Map<String, Object> properties = new HashMap<>(3);
        String label = device.getDisplayString();
        try {
            label += " " + device.getDetails().getModelDetails().getModelName();
        } catch (Exception e) {
            // ignore and use the default label
        }
        properties.put(CONFIG_HOST_NAME, device.getIdentity().getDescriptorURL().getHost());

        DiscoveryResult result = DiscoveryResultBuilder.create(uid).withProperties(properties).withLabel(label).build();

        logger.debug("Created a DiscoveryResult for device '{}' with UDN '{}' {}",
                device.getDetails().getModelDetails().getModelName(),
                device.getIdentity().getUdn().getIdentifierString(), device.getIdentity().getDescriptorURL().getHost()
                        + ":" + device.getIdentity().getDescriptorURL().getPort());
        for (String key : result.getProperties().keySet()) {
            logger.debug("With props: {}:{}", key, result.getProperties().get(key));
        }
        return result;
    }

    @Override
    public ThingUID getThingUID(RemoteDevice device) {
        if (device == null) {
            return null;
        }

        String manufacturer = device.getDetails().getManufacturerDetails().getManufacturer();
        String modelName = device.getDetails().getModelDetails().getModelName();
        String friedlyName = device.getDetails().getFriendlyName();

        if (manufacturer == null || modelName == null) {
            return null;
        }

        // UDN shouldn't contain '-' characters.
        String udn = device.getIdentity().getUdn().getIdentifierString().replace("-", "_");

        if (device.getType().getType().equals(UPNP_TYPE)) {
            logger.debug("Discovered '{}' model '{}' thing with UDN '{}'", friedlyName, modelName, udn);
            if (manufacturer.toUpperCase().contains(UPNP_MANUFACTURER_YAMAHA)) {
                return new ThingUID(THING_TYPE_YAMAHA, udn);
            } else if (manufacturer.toUpperCase().contains(UPNP_MANUFACTURER_SAMSUNG)) {
                return new ThingUID(THING_TYPE_SAMSUNG, udn);
            } else if (manufacturer.toUpperCase().contains(UPNP_MANUFACTURER_MARANTZ)) {
                return new ThingUID(THING_TYPE_MARANTZ, udn);
            } else if (manufacturer.toUpperCase().contains(UPNP_MANUFACTURER_DENON)) {
                return new ThingUID(THING_TYPE_DENON, udn);
            } else if (manufacturer.toUpperCase().contains(UPNP_MANUFACTURER_ONKYO)) {
                return new ThingUID(THING_TYPE_ONKYO, udn);
            } else if (manufacturer.toUpperCase().contains(UPNP_MANUFACTURER_EPSON)) {
                return new ThingUID(THING_TYPE_EPSON_PROJECTOR, udn);
            } else if (manufacturer.toUpperCase().contains(UPNP_MANUFACTURER_PHILIPS)) {
                return new ThingUID(THING_TYPE_PHILIPS, udn);

            } else {

                return new ThingUID(THING_TYPE_GENERAL_RENDERER, udn);
            }

        } else {
            return null;
        }
    }
}
