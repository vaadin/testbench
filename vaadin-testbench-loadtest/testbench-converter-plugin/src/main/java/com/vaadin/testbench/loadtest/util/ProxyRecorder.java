/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.loadtest.util;

import java.io.IOException;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.logging.Logger;

import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.proxy.CaptureType;

/**
 * HTTP Proxy Recorder for k6 tests using BrowserMob Proxy. Records HTTP traffic
 * and exports it as HAR format.
 */
public class ProxyRecorder {

    private static final Logger log = Logger
            .getLogger(ProxyRecorder.class.getName());
    private BrowserMobProxy proxy;
    private Path harOutputPath;

    /**
     * Creates a new proxy recorder instance.
     */
    public ProxyRecorder() {
    }

    /**
     * Starts the proxy recorder on the specified port.
     *
     * @param port
     *            the port to listen on
     * @param harOutputPath
     *            the path to write the HAR file
     */
    public void start(int port, Path harOutputPath) {
        this.harOutputPath = harOutputPath;

        proxy = new BrowserMobProxyServer();

        // Enable all capture types for complete HAR recording
        proxy.setHarCaptureTypes(EnumSet.allOf(CaptureType.class));

        // Disable request/response size limits
        proxy.setTrustAllServers(true);

        // Strip Accept-Encoding from requests so the server returns
        // uncompressed
        // responses. This ensures HAR bodies are readable for Vaadin session
        // value extraction (syncId, csrfToken, etc.)
        proxy.addRequestFilter((request, contents, messageInfo) -> {
            request.headers().remove("Accept-Encoding");
            return null;
        });

        // Start proxy on specified port
        proxy.start(port);

        // Create a new HAR
        proxy.newHar("k6-recording");

        log.info("Proxy Recorder running on port " + port);
        log.info("HAR will be stored in: " + harOutputPath);
        log.info("Waiting for requests...");
    }

    /**
     * Stops the proxy recorder and saves the HAR file.
     *
     * @throws IOException
     *             if writing the HAR file fails
     */
    public void stop() throws IOException {
        if (proxy != null && proxy.isStarted()) {
            log.info("Stopping proxy recorder...");

            // Get the recorded HAR
            Har har = proxy.getHar();

            // Save HAR to file
            if (har != null && harOutputPath != null) {
                log.info("Writing HAR file: " + harOutputPath);
                log.info("Recorded requests: "
                        + har.getLog().getEntries().size());
                har.writeTo(harOutputPath.toFile());
            }

            // Stop the proxy
            proxy.stop();
            proxy = null;

            log.info("Proxy recorder stopped");
        }
    }

    /**
     * Checks if the proxy is currently running.
     *
     * @return true if the proxy is running
     */
    public boolean isRunning() {
        return proxy != null && proxy.isStarted();
    }

    /**
     * Gets the current number of recorded entries.
     *
     * @return the number of recorded HAR entries
     */
    public int getRecordedEntryCount() {
        if (proxy != null && proxy.getHar() != null) {
            return proxy.getHar().getLog().getEntries().size();
        }
        return 0;
    }

    /**
     * Gets the port the proxy is listening on.
     *
     * @return the port number, or -1 if not running
     */
    public int getPort() {
        if (proxy != null && proxy.isStarted()) {
            return proxy.getPort();
        }
        return -1;
    }
}
