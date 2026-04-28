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

/**
 * Records Playwright Java tests and converts their HAR output to k6 load tests.
 * <p>
 * Unlike the TestBench {@code record} goal, this does NOT use a recording
 * proxy. Instead, it relies on Playwright's native HAR recording capability.
 * The test class must create its {@code BrowserContext} via
 * com.vaadin.testbench.loadtest.PlaywrightHelper#createBrowserContext (or
 * otherwise configure {@code BrowserContext} with {@code setRecordHarPath}) to
 * produce HAR output when the {@code k6.harOutputPath} system property is set.
 * <p>
 * For each test class, this goal:
 * <ol>
 * <li>Runs the Playwright test with {@code -Dk6.harOutputPath=...}</li>
 * <li>Collects the HAR file produced by Playwright</li>
 * <li>Filters external domains from the HAR</li>
 * <li>Converts the HAR to a k6 script</li>
 * <li>Refactors the script for Vaadin compatibility</li>
 * </ol>
 * <p>
 * Example usage:
 * 
 * <pre>
 * mvn k6:record-playwright -Dk6.testClasses=HelloWorldPlaywrightIT,CrudExamplePlaywrightIT
 * </pre>
 */
@Mojo(name = "record-playwright", defaultPhase = LifecyclePhase.INTEGRATION_TEST)
public class PlaywrightRecordMojo extends AbstractRecordMojo {

    @Override
    protected String getGoalName() {
        return "record-playwright";
    }

    @Override
    protected String getTestFrameworkName() {
        return "Playwright";
    }

    @Override
    protected void logRecordingConfiguration() {
        getLog().info("  App port: " + appPort);
        getLog().info(
                "  Using Playwright native HAR recording (no proxy needed)");
    }

    @Override
    protected void recordHar(String currentTestClass, Path harPath)
            throws MojoExecutionException, InterruptedException, IOException {
        boolean testSuccess = runPlaywrightTest(currentTestClass, harPath);

        if (!Files.exists(harPath)) {
            throw new MojoExecutionException("HAR file was not created: "
                    + harPath
                    + ". Ensure the test class creates its BrowserContext via "
                    + "PlaywrightHelper.createBrowserContext(...) or otherwise "
                    + "configures BrowserContext with setRecordHarPath when "
                    + "the k6.harOutputPath system property is set.");
        }

        long harSize = Files.size(harPath);
        if (harSize < 100) {
            throw new MojoExecutionException(
                    "HAR file appears empty (size: " + harSize + " bytes)");
        }

        getLog().info(
                "HAR file created: " + harPath + " (" + harSize + " bytes)");

        if (!testSuccess) {
            getLog().warn(
                    "Playwright test may have failed, but HAR was recorded. Continuing with conversion...");
        }
    }

    private boolean runPlaywrightTest(String currentTestClass,
            Path harOutputPath) throws MojoExecutionException {
        getLog().info("Running Playwright test: " + currentTestClass);

        List<String> command = buildBaseTestCommand(currentTestClass);
        command.add("-Dk6.harOutputPath=" + harOutputPath.toAbsolutePath());

        return runMavenTest(command);
    }
}
