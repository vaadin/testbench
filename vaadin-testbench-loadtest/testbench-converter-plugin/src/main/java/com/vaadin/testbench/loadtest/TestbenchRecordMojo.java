/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.loadtest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Records TestBench tests through a proxy and converts them to k6 load tests.
 * <p>
 * This goal supports recording multiple test classes. For each test class it:
 * <ol>
 * <li>Starts a recording proxy on the specified port</li>
 * <li>Runs the TestBench test class with proxy configuration</li>
 * <li>Stops the proxy and saves the HAR file</li>
 * <li>Converts the HAR to a k6 test (filters, converts, refactors)</li>
 * </ol>
 * <p>
 * Example usage:
 *
 * <pre>
 * mvn k6:record -Dk6.testClasses=HelloWorldIT,CrudExampleIT
 * </pre>
 */
@Mojo(name = "record", defaultPhase = LifecyclePhase.INTEGRATION_TEST)
public class TestbenchRecordMojo extends AbstractRecordMojo {

    /**
     * Port for the recording proxy.
     */
    @Parameter(property = "k6.proxyPort", defaultValue = "6000")
    private int proxyPort;

    @Override
    protected String getGoalName() {
        return "record";
    }

    @Override
    protected String getTestFrameworkName() {
        return "TestBench";
    }

    @Override
    protected void logRecordingConfiguration() {
        getLog().info("  Proxy port: " + proxyPort);
        getLog().info("  App port: " + appPort);
    }

    @Override
    protected void recordHar(String currentTestClass, Path harPath)
            throws MojoExecutionException, InterruptedException, IOException {
        try {
            // Start proxy recorder
            nodeRunner.startProxyRecorder(proxyPort, harPath);

            // Run TestBench test
            boolean testSuccess = runTestBenchTest(currentTestClass);

            // Verify proxy captured traffic before stopping
            int recordedEntries = nodeRunner.getRecordedEntryCount();
            if (recordedEntries == 0) {
                nodeRunner.stopProxyRecorder();
                throw new MojoExecutionException(
                        "Proxy recorded 0 requests for test '"
                                + currentTestClass + "'. "
                                + "The test likely did not route traffic through the proxy on port "
                                + proxyPort + ". "
                                + "Verify that the K6RecordingExtension is on the classpath and "
                                + "junit.jupiter.extensions.autodetection.enabled=true is set.");
            }
            getLog().info("Proxy captured " + recordedEntries + " requests");

            // Stop proxy (this writes the HAR file)
            nodeRunner.stopProxyRecorder();

            // Wait a moment for HAR file to be written
            Thread.sleep(1000);

            if (!Files.exists(harPath)) {
                throw new MojoExecutionException(
                        "HAR file was not created: " + harPath);
            }

            getLog().info("HAR file created: " + harPath + " ("
                    + Files.size(harPath) + " bytes)");

            if (!testSuccess) {
                getLog().warn(
                        "TestBench test may have failed, but HAR was recorded. Continuing with conversion...");
            }
        } finally {
            // Ensure proxy is stopped
            nodeRunner.stopProxyRecorder();
        }
    }

    private boolean runTestBenchTest(String currentTestClass)
            throws MojoExecutionException {
        getLog().info("Running TestBench test: " + currentTestClass);

        List<String> command = buildBaseTestCommand(currentTestClass);
        command.add("-Dk6.proxy.host=localhost:" + proxyPort);
        // Enable JUnit 5 auto-detection for K6RecordingExtension
        command.add("-Djunit.jupiter.extensions.autodetection.enabled=true");

        return runMavenTest(command);
    }
}
