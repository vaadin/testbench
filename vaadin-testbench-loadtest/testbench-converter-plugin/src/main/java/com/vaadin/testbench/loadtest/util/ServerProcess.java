/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.loadtest.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.logging.Logger;

/**
 * Manages a server process lifecycle: start, health-check polling, and stop.
 * Uses {@link java.lang.Process} for cross-platform process management (no
 * shell commands like {@code pkill} required).
 */
public class ServerProcess {

    private static final Logger log = Logger
            .getLogger(ServerProcess.class.getName());
    private Process process;
    private Thread outputThread;

    /**
     * Starts the server process asynchronously.
     *
     * @param command
     *            the command to execute (e.g., ["java", "-jar", "app.jar",
     *            ...])
     * @throws Exception
     *             if the process cannot be started
     */
    public void start(List<String> command) throws Exception {
        log.info("Starting server: " + String.join(" ", command));

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        process = pb.start();

        // Consume stdout/stderr on a daemon thread to prevent process blocking
        outputThread = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (isStackTraceLine(line)) {
                        // Print continuation lines (stack traces) without
                        // logger formatting so they remain readable
                        System.out.println("[server] " + line);
                    } else {
                        log.info("[server] " + line);
                    }
                }
            } catch (Exception e) {
                // Process ended or stream closed
            }
        }, "k6-server-output");
        outputThread.setDaemon(true);
        outputThread.start();
    }

    /**
     * Polls health URLs until all return HTTP status &lt; 500, or timeout is
     * reached.
     *
     * @param healthUrls
     *            URLs to poll (each must become healthy in sequence)
     * @param timeout
     *            maximum time to wait
     * @param pollInterval
     *            time between poll attempts
     * @throws Exception
     *             if timeout is reached or the process dies during startup
     */
    public void waitForReady(List<String> healthUrls, Duration timeout,
            Duration pollInterval) throws Exception {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(3)).build();

        for (String url : healthUrls) {
            log.info("Waiting for " + url + " ...");
            long deadline = System.currentTimeMillis() + timeout.toMillis();

            while (System.currentTimeMillis() < deadline) {
                if (!isAlive()) {
                    throw new Exception(
                            "Server process died during startup (exit code: "
                                    + process.exitValue() + ")");
                }
                try {
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(url)).timeout(Duration.ofSeconds(3))
                            .GET().build();
                    HttpResponse<String> response = client.send(request,
                            HttpResponse.BodyHandlers.ofString());
                    if (response.statusCode() < 500) {
                        log.info(url + " is ready (status "
                                + response.statusCode() + ")");
                        break;
                    }
                } catch (Exception e) {
                    // Connection refused or timeout — not ready yet
                    log.fine("Not ready yet: " + e.getMessage());
                }

                if (System.currentTimeMillis() >= deadline) {
                    throw new Exception("Timeout waiting for " + url + " after "
                            + timeout.getSeconds() + "s");
                }
                Thread.sleep(pollInterval.toMillis());
            }
        }
    }

    /**
     * Stops the server process. Sends a graceful shutdown signal first, then
     * force-kills if the process does not exit within the grace period.
     *
     * @param gracePeriod
     *            time to wait for graceful shutdown before force-killing
     */
    public void stop(Duration gracePeriod) {
        if (process == null || !process.isAlive()) {
            log.info("No server process to stop");
            return;
        }

        log.info("Stopping server process...");
        process.destroy();

        try {
            boolean exited = process.waitFor(gracePeriod.toMillis(),
                    java.util.concurrent.TimeUnit.MILLISECONDS);
            if (!exited) {
                log.warning("Server did not stop within "
                        + gracePeriod.getSeconds() + "s, force-killing");
                process.destroyForcibly();
                process.waitFor(5, java.util.concurrent.TimeUnit.MILLISECONDS);
            }
        } catch (InterruptedException e) {
            process.destroyForcibly();
            Thread.currentThread().interrupt();
        }

        if (!process.isAlive()) {
            log.info("Server stopped (exit code: " + process.exitValue() + ")");
        }
    }

    /**
     * Returns whether the server process is still running.
     *
     * @return {@code true} if the process is running
     */
    public boolean isAlive() {
        return process != null && process.isAlive();
    }

    /**
     * Returns {@code true} for stack-trace continuation lines that should be
     * printed without logger formatting (timestamp, class name, etc.) so that
     * multi-line stack traces remain readable.
     */
    private static boolean isStackTraceLine(String line) {
        String trimmed = line.stripLeading();
        return trimmed.startsWith("at ") || trimmed.startsWith("Caused by: ")
                || trimmed.startsWith("Suppressed: ")
                || trimmed.startsWith("... ");
    }
}
