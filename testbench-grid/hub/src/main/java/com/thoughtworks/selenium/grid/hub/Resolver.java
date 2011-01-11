package com.thoughtworks.selenium.grid.hub;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Resolver {

    private static final Log LOGGER = LogFactory.getLog(Resolver.class);

    static Map<String, String> resolved = Collections
            .synchronizedMap(new HashMap<String, String>());
    static Set<String> resolvePending = Collections
            .synchronizedSet(new HashSet<String>());

    private static Thread resolverThread = new Thread() {
        @Override
        public void run() {
            setName("Hub IP address resolver");
            LOGGER.debug("Resolver thread started");
            while (isAlive()) {
                String toResolve = null;
                synchronized (resolvePending) {
                    if (!resolvePending.isEmpty()) {
                        toResolve = resolvePending.iterator().next();
                        resolvePending.remove(toResolve);

                        // Add ip to avoid multiple requests. This will also be
                        // used if the lookup fails.
                        resolved.put(toResolve, toResolve);
                    }
                }

                if (toResolve != null) {
                    InetAddress ipAddress;
                    try {
                        ipAddress = InetAddress.getByName(toResolve);
                        LOGGER.debug("Looking up " + toResolve);
                        if (ipAddress != null) {
                            String hostName = ipAddress.getHostName();
                            if (hostName != null) {
                                LOGGER.debug("Resolved " + toResolve + " to "
                                        + hostName);
                                resolved.put(toResolve, hostName);
                            }
                        }
                    } catch (UnknownHostException e) {
                    }

                } else {
                    try {
                        // Wait for more work
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                }
            }
        };
    };

    public static String getHostname(String ipAddress) {
        String name = resolved.get(ipAddress);
        if (name == null) {
            synchronized (resolvePending) {
                resolvePending.add(ipAddress);
                if (!resolverThread.isAlive()) {
                    resolverThread.start();
                }
            }
            return ipAddress;
        } else {
            return name;
        }
    }
}
