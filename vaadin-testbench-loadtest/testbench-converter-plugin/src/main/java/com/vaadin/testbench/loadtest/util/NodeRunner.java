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
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * Manages process execution for the plugin. Now uses Java implementations for
 * HAR filtering, k6 conversion, and proxy recording. Only k6 execution still
 * requires an external process.
 */
public class NodeRunner {

    private static final Logger log = Logger
            .getLogger(NodeRunner.class.getName());
    private final Path workingDirectory;
    private ProxyRecorder proxyRecorder;

    /**
     * Creates a new node runner for the given working directory.
     *
     * @param workingDirectory
     *            the directory to run node/k6 commands in
     */
    public NodeRunner(Path workingDirectory) {
        ExperimentalWarning.log();
        this.workingDirectory = workingDirectory;
    }

    /**
     * Checks if k6 is available on the system.
     *
     * @return true if k6 is installed and accessible
     */
    public boolean isK6Available() {
        try {
            ProcessBuilder pb = new ProcessBuilder("k6", "version");
            Process process = pb.start();
            boolean finished = process.waitFor(10, TimeUnit.SECONDS);
            if (finished && process.exitValue() == 0) {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()))) {
                    String version = reader.readLine();
                    log.fine("k6 version: " + version);
                }
                return true;
            }
        } catch (IOException | InterruptedException e) {
            log.fine("k6 check failed: " + e.getMessage());
        }
        return false;
    }

    /**
     * Starts the proxy recorder in the background.
     *
     * @param proxyPort
     *            the port for the proxy to listen on
     * @param harFile
     *            the output HAR file path
     * @throws MojoExecutionException
     *             if starting the proxy fails
     */
    public void startProxyRecorder(int proxyPort, Path harFile)
            throws MojoExecutionException {
        log.info("Starting proxy recorder on port " + proxyPort + "...");
        try {
            proxyRecorder = new ProxyRecorder();
            proxyRecorder.start(proxyPort, harFile);
            log.info("Proxy recorder started");
        } catch (Exception e) {
            throw new MojoExecutionException("Failed to start proxy recorder",
                    e);
        }
    }

    /**
     * Stops the proxy recorder gracefully.
     */
    public void stopProxyRecorder() {
        if (proxyRecorder != null && proxyRecorder.isRunning()) {
            log.info("Stopping proxy recorder...");
            try {
                proxyRecorder.stop();
                log.info("Proxy recorder stopped");
            } catch (IOException e) {
                log.warning("Error stopping proxy recorder: " + e.getMessage());
            }
            proxyRecorder = null;
        }
    }

    /**
     * Gets the number of requests recorded by the proxy.
     *
     * @return the number of recorded HAR entries, or 0 if not recording
     */
    public int getRecordedEntryCount() {
        if (proxyRecorder != null) {
            return proxyRecorder.getRecordedEntryCount();
        }
        return 0;
    }

    /**
     * Runs the HAR filter to remove external domain requests.
     *
     * @param harFile
     *            the HAR file to filter
     * @throws MojoExecutionException
     *             if filtering fails
     */
    public void filterHar(Path harFile) throws MojoExecutionException {
        log.info("Filtering external domains from HAR file...");
        try {
            HarFilter filter = new HarFilter();
            filter.filter(harFile);
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to filter HAR file", e);
        }
    }

    /**
     * Converts a HAR file to a k6 test with default thresholds.
     *
     * @param harFile
     *            the input HAR file
     * @param outputFile
     *            the output k6 test file
     * @throws MojoExecutionException
     *             if conversion fails
     */
    public void harToK6(Path harFile, Path outputFile)
            throws MojoExecutionException {
        harToK6(harFile, outputFile, ThresholdConfig.DEFAULT);
    }

    /**
     * Converts a HAR file to a k6 test with configurable thresholds.
     *
     * @param harFile
     *            the input HAR file
     * @param outputFile
     *            the output k6 test file
     * @param thresholdConfig
     *            threshold configuration for the generated script
     * @throws MojoExecutionException
     *             if conversion fails
     */
    public void harToK6(Path harFile, Path outputFile,
            ThresholdConfig thresholdConfig) throws MojoExecutionException {
        log.info("Converting HAR to k6 test...");
        try {
            HarToK6Converter converter = new HarToK6Converter();
            converter.convert(harFile, outputFile, thresholdConfig);
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to convert HAR to k6", e);
        }
    }

    /**
     * Refactors a k6 test for Vaadin compatibility with default think time
     * settings.
     *
     * @param inputFile
     *            the input k6 test file
     * @param outputFile
     *            the output refactored test file
     * @throws MojoExecutionException
     *             if refactoring fails
     */
    public void refactorK6Test(Path inputFile, Path outputFile)
            throws MojoExecutionException {
        refactorK6Test(inputFile, outputFile,
                K6TestRefactorer.ThinkTimeConfig.DEFAULT);
    }

    /**
     * Refactors a k6 test for Vaadin compatibility with custom think time
     * settings.
     *
     * @param inputFile
     *            the input k6 test file
     * @param outputFile
     *            the output refactored test file
     * @param thinkTimeConfig
     *            think time configuration for realistic user simulation
     * @throws MojoExecutionException
     *             if refactoring fails
     */
    public void refactorK6Test(Path inputFile, Path outputFile,
            K6TestRefactorer.ThinkTimeConfig thinkTimeConfig)
            throws MojoExecutionException {
        log.info("Refactoring k6 test for Vaadin...");
        try {
            K6TestRefactorer refactorer = new K6TestRefactorer(thinkTimeConfig);
            refactorer.refactor(inputFile, outputFile);
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to refactor k6 test", e);
        }
    }

    /**
     * Runs a k6 load test.
     *
     * @param testFile
     *            the k6 test file to run
     * @param virtualUsers
     *            number of virtual users
     * @param duration
     *            test duration (e.g., "30s", "1m")
     * @param appIp
     *            application IP address
     * @param appPort
     *            application port
     * @throws MojoExecutionException
     *             if the test fails
     */
    public void runK6Test(Path testFile, int virtualUsers, String duration,
            String appIp, int appPort) throws MojoExecutionException {
        runK6Test(testFile, virtualUsers, duration, appIp, appPort, false);
    }

    /**
     * Runs a k6 load test.
     *
     * @param testFile
     *            the k6 test file to run
     * @param virtualUsers
     *            number of virtual users
     * @param duration
     *            test duration (e.g., "30s", "1m")
     * @param appIp
     *            application IP address
     * @param appPort
     *            application port
     * @param useEmbeddedConfig
     *            if true, don't pass --vus and --duration (use script's
     *            embedded config)
     * @throws MojoExecutionException
     *             if the test fails
     */
    public void runK6Test(Path testFile, int virtualUsers, String duration,
            String appIp, int appPort, boolean useEmbeddedConfig)
            throws MojoExecutionException {
        log.info("Running k6 load test: " + testFile.getFileName());
        if (!useEmbeddedConfig) {
            log.info("  Virtual Users: " + virtualUsers);
            log.info("  Duration: " + duration);
        } else {
            log.info("  Using embedded scenario configuration");
        }
        log.info("  Target: " + appIp + ":" + appPort);

        try {
            List<String> command = new ArrayList<>();
            command.add("k6");
            command.add("run");

            // Only add VUs and duration if not using embedded config
            if (!useEmbeddedConfig) {
                command.add("--vus");
                command.add(String.valueOf(virtualUsers));
                command.add("--duration");
                command.add(duration);
            }

            // Always pass environment variables for target server
            command.add("-e");
            command.add("APP_IP=" + appIp);
            command.add("-e");
            command.add("APP_PORT=" + String.valueOf(appPort));
            command.add(testFile.toAbsolutePath().toString());

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.inheritIO(); // Pass through stdin/stdout/stderr for live output

            Process process = pb.start();

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new MojoExecutionException(
                        "k6 test failed with exit code: " + exitCode);
            }
            log.info("k6 test completed successfully");
        } catch (IOException | InterruptedException e) {
            throw new MojoExecutionException("Failed to run k6 test", e);
        }
    }

    // Deprecated methods - kept for backwards compatibility but no longer
    // needed

    /**
     * @deprecated Node.js is no longer required. This method always returns
     *             true.
     */
    @Deprecated
    public boolean isNodeAvailable() {
        return true;
    }

    /**
     * @deprecated npm is no longer required. This method always returns true.
     */
    @Deprecated
    public boolean isNpmAvailable() {
        return true;
    }

    /**
     * @deprecated npm dependencies are no longer required. This method is a
     *             no-op.
     */
    @Deprecated
    public void npmInstall() throws MojoExecutionException {
        // No-op - npm dependencies are no longer needed
    }

    /**
     * @deprecated npm dependencies are no longer required. This method always
     *             returns true.
     */
    @Deprecated
    public boolean areDependenciesInstalled() {
        return true;
    }
}
