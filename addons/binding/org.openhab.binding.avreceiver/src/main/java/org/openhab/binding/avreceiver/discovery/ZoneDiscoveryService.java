/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.avreceiver.discovery;

import java.util.Hashtable;

import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class ZoneDiscoveryService extends AbstractDiscoveryService {

    private ServiceRegistration<?> reg = null;

    public ZoneDiscoveryService() {
        super(AVReceiverDiscoveryParticipant.supportedThingTypes, 2, true);
    }

    public void stop() {
        if (reg != null) {
            reg.unregister();
        }
        reg = null;
    }

    @Override
    protected void startScan() {
    }

    // public void detectZones(YamahaReceiverState state, String base_udn) {
    // Map<String, Object> properties = new HashMap<>(3);
    // properties.put((String) CONFIG_HOST_NAME, state.getHost());
    //
    // for (Zone zone : state.additional_zones) {
    // String zoneName = zone.name();
    // ThingUID uid = new ThingUID(THING_TYPE_YAMAHA, base_udn + zoneName);
    //
    // properties.put(CONFIG_ZONE, zoneName);
    // DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(uid).withProperties(properties)
    // .withLabel(state.name + " " + zoneName).build();
    // thingDiscovered(discoveryResult);
    // }
    // }

    public void start(BundleContext bundleContext) {
        if (reg != null) {
            return;
        }
        reg = bundleContext.registerService(DiscoveryService.class.getName(), this, new Hashtable<String, Object>());
    }
}