package org.openhab.binding.mopidy.discovery;

import static org.openhab.binding.mopidy.MopidyBindingConstants.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.openhab.binding.mopidy.MopidyBindingConstants;
import org.openhab.binding.mopidy.service.NetworkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MopidyDiscoveryService extends AbstractDiscoveryService {

    private final Logger logger = LoggerFactory.getLogger(MopidyDiscoveryService.class);
    private ExecutorService executorService = null;
    final static int PING_TIMEOUT_IN_MS = 50;
    private int scanningNetworkSize = 0;

    public MopidyDiscoveryService() {
        super(MopidyBindingConstants.SUPPORTED_THING_TYPES_UIDS, 900, true);
    }

    @Override
    protected void startBackgroundDiscovery() {
        startScan();
    }

    @Override
    protected void stopBackgroundDiscovery() {
        stopScan();
    }

    @Override
    protected void startScan() {
        if (executorService != null) {
            stopScan();
        }

        logger.info("Starting Discovery for Mopidy");
        LinkedHashSet<String> networkIPs = NetworkUtils.getNetworkIPs(NetworkUtils.getInterfaceIPs());
        scanningNetworkSize = networkIPs.size();
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 10);
        for (Iterator<String> it = networkIPs.iterator(); it.hasNext();) {
            final String ip = it.next();
            // logger.debug("Executing check to ip:{}", ip);
            executorService.execute(new MopidyCheck(ip, this));
        }
        stopScan();

    }

    @Override
    protected synchronized void stopScan() {
        super.stopScan();
        if (executorService == null) {
            return;
        }

        try {
            executorService.awaitTermination(PING_TIMEOUT_IN_MS * scanningNetworkSize, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
        }
        executorService.shutdown();
        executorService = null;

    }

    @Override
    protected void thingDiscovered(DiscoveryResult discoveryResult) {
        super.thingDiscovered(discoveryResult);
        logger.debug("Thing Discovered {}", discoveryResult);
    }

    protected void newMopidyFound(String ip) {
        logger.info("Found {}", ip);

        ThingUID uid = new ThingUID(MopidyBindingConstants.THING_TYPE_MOPIDY, ip.replace('.', '_'));

        if (uid != null) {
            Map<String, Object> map = new HashMap<>();
            map.put(PARAMETER_HOSTNAME, ip);
            map.put(PARAMETER_PORT, 6680);
            DiscoveryResult result = DiscoveryResultBuilder.create(uid).withLabel("Mopidy server (" + ip + ")")
                    .withProperties(map).build();

            thingDiscovered(result);
        }
    }

    protected void notFound(String ip) {
        // logger.debug("Not found {}", ip);
    }

}
