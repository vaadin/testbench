/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.loadtest;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;

import com.vaadin.testbench.loadtest.util.K6TestRefactorer.ThinkTimeConfig;
import com.vaadin.testbench.loadtest.util.SourceHasher;

/**
 * Base class for recording test scenarios and converting them to k6 load tests.
 * Provides the shared orchestration for cache checking, HAR post-processing,
 * and k6 script generation. Subclasses implement the recording mechanism
 * (proxy-based for TestBench, native HAR for Playwright).
 */
public abstract class AbstractRecordMojo extends AbstractK6Mojo {

    /**
     * The test class to record. Can be a simple class name or fully qualified
     * name. For multiple classes, use testClasses instead.
     */
    @Parameter(property = "k6.testClass")
    protected String testClass;

    /**
     * List of test classes to record. Each class will be recorded separately
     * and generate its own k6 test file.
     */
    @Parameter(property = "k6.testClasses")
    protected List<String> testClasses;

    /**
     * Port where the application is running.
     */
    @Parameter(property = "k6.appPort", defaultValue = "8080")
    protected int appPort;

    /**
     * Working directory for running the test. Defaults to the project base
     * directory.
     */
    @Parameter(property = "k6.testWorkDir", defaultValue = "${project.basedir}")
    protected File testWorkDir;

    /**
     * Directory to store HAR recordings.
     */
    @Parameter(property = "k6.harDir", defaultValue = "${project.build.directory}")
    protected File harDir;

    /**
     * Output directory for generated k6 tests.
     */
    @Parameter(property = "k6.outputDir", defaultValue = "${project.build.directory}/k6/tests")
    protected File outputDir;

    /**
     * Timeout for test execution in seconds.
     */
    @Parameter(property = "k6.testTimeout", defaultValue = "300")
    protected int testTimeout;

    /**
     * Additional Maven arguments for running the test.
     */
    @Parameter(property = "k6.mavenArgs")
    protected String mavenArgs;

    /**
     * Force re-recording even if sources haven't changed. By default, recording
     * is skipped if the test source files and pom.xml haven't changed since the
     * last recording.
     */
    @Parameter(property = "k6.forceRecord", defaultValue = "false")
    protected boolean forceRecord;

    /**
     * Enable realistic think time delays between user actions. When enabled,
     * the generated k6 scripts will include sleep() calls to simulate real user
     * behavior (reading pages, thinking before actions). Set to false for
     * maximum throughput testing.
     */
    @Parameter(property = "k6.thinkTime.enabled", defaultValue = "true")
    protected boolean thinkTimeEnabled;

    /**
     * Base delay in seconds after page load (v-r=init response). Simulates time
     * for a user to read and understand the page. Actual delay will be:
     * baseDelay + random(0, baseDelay * 1.5) Set to 0 to disable page read
     * delays while keeping interaction delays.
     */
    @Parameter(property = "k6.thinkTime.pageReadDelay", defaultValue = "2.0")
    protected double pageReadDelay;

    /**
     * Base delay in seconds after user interaction (v-r=uidl response).
     * Simulates thinking time between user actions. Actual delay will be:
     * baseDelay + random(0, baseDelay * 3) Set to 0 to disable interaction
     * delays while keeping page read delays.
     */
    @Parameter(property = "k6.thinkTime.interactionDelay", defaultValue = "0.5")
    protected double interactionDelay;

    protected SourceHasher sourceHasher;

    /**
     * Returns the goal name for logging (e.g., "k6:record").
     */
    protected abstract String getGoalName();

    /**
     * Returns the test framework name for logging (e.g., "TestBench").
     */
    protected abstract String getTestFrameworkName();

    /**
     * Logs recording-specific configuration after the common header.
     */
    protected abstract void logRecordingConfiguration();

    /**
     * Records a HAR file by running the test. Must ensure a valid HAR file
     * exists at {@code harPath} when returning, or throw an exception.
     *
     * @param currentTestClass
     *            the test class to record
     * @param harPath
     *            where the HAR file should be written
     * @throws MojoExecutionException
     *             if recording fails
     * @throws InterruptedException
     *             if interrupted during recording
     * @throws IOException
     *             if an I/O error occurs
     */
    protected abstract void recordHar(String currentTestClass, Path harPath)
            throws MojoExecutionException, InterruptedException, IOException;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skip) {
            getLog().info("Skipping " + getGoalName());
            return;
        }

        List<String> classesToRecord = getTestClassesToRecord();
        if (classesToRecord.isEmpty()) {
            throw new MojoExecutionException(
                    "No test classes specified. Use testClass or testClasses parameter.");
        }

        getLog().info("Recording " + classesToRecord.size() + " "
                + getTestFrameworkName() + " test(s)");
        logRecordingConfiguration();

        initialize();
        sourceHasher = new SourceHasher();

        Path outputPath = outputDir.toPath().toAbsolutePath();
        ensureDirectoryExists(outputPath);

        List<Path> generatedTests = new ArrayList<>();
        List<Path> cachedTests = new ArrayList<>();

        for (String currentTestClass : classesToRecord) {
            try {
                RecordResult result = recordSingleTest(currentTestClass,
                        outputPath);
                if (result.wasCached()) {
                    cachedTests.add(result.testFile());
                } else {
                    generatedTests.add(result.testFile());
                }
            } catch (Exception e) {
                getLog().error(
                        "Failed to record test class: " + currentTestClass, e);
                throw new MojoExecutionException(
                        "Failed to record test class: " + currentTestClass, e);
            }
        }

        copyVaadinHelpers(outputPath);

        getLog().info("");
        if (!generatedTests.isEmpty()) {
            getLog().info("Recorded " + generatedTests.size() + " test(s):");
            for (Path test : generatedTests) {
                getLog().info("  - " + test.getFileName());
            }
        }
        if (!cachedTests.isEmpty()) {
            getLog().info(
                    "Skipped " + cachedTests.size() + " unchanged test(s):");
            for (Path test : cachedTests) {
                getLog().info("  - " + test.getFileName() + " (cached)");
            }
        }
        getLog().info("");
        getLog().info("Run the tests with:");
        for (Path test : generatedTests) {
            getLog().info("  k6 run -e APP_PORT=" + appPort + " " + test);
        }
        for (Path test : cachedTests) {
            getLog().info("  k6 run -e APP_PORT=" + appPort + " " + test);
        }
    }

    /**
     * Result of recording a single test.
     */
    private record RecordResult(Path testFile, boolean wasCached) {
    }

    /**
     * Builds the list of test classes to record from configuration.
     */
    private List<String> getTestClassesToRecord() {
        List<String> result = new ArrayList<>();

        if (testClasses != null && !testClasses.isEmpty()) {
            for (String tc : testClasses) {
                // Support comma-separated values in list items
                if (tc.contains(",")) {
                    result.addAll(Arrays.asList(tc.split("\\s*,\\s*")));
                } else {
                    result.add(tc.trim());
                }
            }
        }

        // Add single testClass if specified and not already in list
        if (testClass != null && !testClass.isEmpty()) {
            for (String tc : testClass.split("\\s*,\\s*")) {
                if (!result.contains(tc.trim())) {
                    result.add(tc.trim());
                }
            }
        }

        return result;
    }

    /**
     * Records a single test class and returns the path to the generated k6
     * test. Uses hash-based caching to skip recording if sources haven't
     * changed.
     */
    private RecordResult recordSingleTest(String currentTestClass,
            Path outputPath)
            throws MojoExecutionException, InterruptedException, IOException {

        String outputName = scenarioToFileName(currentTestClass);
        Path harPath = harDir.toPath().resolve(outputName + "-recording.har")
                .toAbsolutePath();
        Path generatedFile = outputPath.resolve(outputName + "-generated.js");
        Path refactoredFile = outputPath.resolve(outputName + ".js");
        Path hashFile = outputPath.resolve(outputName + ".hash");

        // Check if we can use cached version
        if (!forceRecord && Files.exists(refactoredFile)) {
            String currentHash = sourceHasher.calculateSourceHash(
                    testWorkDir.toPath(), currentTestClass);
            String storedHash = sourceHasher.readStoredHash(hashFile);

            if (currentHash != null && currentHash.equals(storedHash)) {
                getLog().info("");
                getLog().info("========================================");
                getLog().info("Skipping: " + currentTestClass + " (unchanged)");
                getLog().info("========================================");
                return new RecordResult(refactoredFile, true);
            }
        }

        getLog().info("");
        getLog().info("========================================");
        getLog().info("Recording: " + currentTestClass);
        getLog().info("========================================");

        ensureDirectoryExists(harPath.getParent());

        // Clean up old files
        try {
            Files.deleteIfExists(harPath);
            Files.deleteIfExists(generatedFile);
        } catch (Exception e) {
            getLog().warn("Could not clean up old files: " + e.getMessage());
        }

        // Record HAR (subclass-specific)
        recordHar(currentTestClass, harPath);

        // Post-processing (shared)
        nodeRunner.filterHar(harPath);
        nodeRunner.harToK6(harPath, generatedFile, buildRecorderOptions());

        ThinkTimeConfig thinkTimeConfig = new ThinkTimeConfig(thinkTimeEnabled,
                pageReadDelay, interactionDelay);
        nodeRunner.refactorK6Test(generatedFile, refactoredFile,
                thinkTimeConfig);

        // Store hash for future cache checks
        String currentHash = sourceHasher
                .calculateSourceHash(testWorkDir.toPath(), currentTestClass);
        if (currentHash != null) {
            sourceHasher.storeHash(hashFile, currentHash);
        }

        return new RecordResult(refactoredFile, false);
    }

    /**
     * Builds the base Maven failsafe command with common arguments. The
     * returned list is mutable so subclasses can add framework-specific
     * arguments.
     *
     * @param currentTestClass
     *            the test class to run
     * @return the command list
     */
    protected List<String> buildBaseTestCommand(String currentTestClass) {
        List<String> command = new ArrayList<>();
        boolean isWindows = System.getProperty("os.name", "").toLowerCase()
                .contains("win");
        if (isWindows) {
            command.add("cmd.exe");
            command.add("/c");
        }
        command.add("mvn");
        command.add("failsafe:integration-test");
        command.add("-Dit.test=" + currentTestClass);
        command.add("-Dserver.port=" + appPort);
        command.add("-DfailIfNoTests=false");

        if (mavenArgs != null && !mavenArgs.isEmpty()) {
            for (String arg : mavenArgs.split("\\s+")) {
                command.add(arg);
            }
        }

        return command;
    }

    /**
     * Runs a Maven failsafe test with the given command.
     *
     * @param command
     *            the full command to execute
     * @return true if the test completed successfully
     * @throws MojoExecutionException
     *             if test execution fails critically
     */
    protected boolean runMavenTest(List<String> command)
            throws MojoExecutionException {
        getLog().debug("Test command: " + String.join(" ", command));

        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(testWorkDir);
            pb.redirectErrorStream(true);

            Process process = pb.start();

            boolean hasTestFailures = false;
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    getLog().info("[test] " + line);
                    if (line.contains("[ERROR] Failures:")
                            || line.contains("[ERROR] Errors:")) {
                        hasTestFailures = true;
                    }
                }
            }

            boolean finished = process.waitFor(testTimeout, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                getLog().warn(
                        "Test timed out after " + testTimeout + " seconds");
                return false;
            }

            int exitCode = process.exitValue();
            if (exitCode != 0) {
                getLog().warn("Test exited with code: " + exitCode);
                return false;
            }

            if (hasTestFailures) {
                return false;
            }

            return true;

        } catch (Exception e) {
            throw new MojoExecutionException("Failed to run test", e);
        }
    }
}
