/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.loadtest;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.vaadin.testbench.loadtest.util.ServerProcess;

/**
 * Starts a Spring Boot application server and waits for it to be ready. The
 * process handle is stored in the Maven project context so that
 * {@code loadtest:stop-server} can shut it down later.
 * <p>
 * Usage in pom.xml:
 *
 * <pre>
 * &lt;execution&gt;
 *     &lt;goals&gt;&lt;goal&gt;start-server&lt;/goal&gt;&lt;/goals&gt;
 *     &lt;configuration&gt;
 *         &lt;serverJar&gt;${project.build.directory}/${project.build.finalName}.jar&lt;/serverJar&gt;
 *         &lt;serverPort&gt;8081&lt;/serverPort&gt;
 *         &lt;managementPort&gt;8082&lt;/managementPort&gt;
 *     &lt;/configuration&gt;
 * &lt;/execution&gt;
 * </pre>
 */
@Mojo(name = "start-server", defaultPhase = LifecyclePhase.PRE_INTEGRATION_TEST)
public class StartServerMojo extends AbstractK6Mojo {

    static final String CONTEXT_KEY = "loadtest.serverProcess";

    /**
     * Path to the executable JAR file to start.
     */
    @Parameter(property = "loadtest.serverJar", required = true)
    private String serverJar;

    /**
     * Application server port (passed as --server.port).
     */
    @Parameter(property = "loadtest.serverPort", defaultValue = "8080")
    private int serverPort;

    /**
     * Spring Boot Actuator management port (passed as
     * --management.server.port).
     */
    @Parameter(property = "loadtest.managementPort", defaultValue = "8082")
    private int managementPort;

    /**
     * Path to the Java executable. When not set, defaults to
     * {@code $JAVA_HOME/bin/java} if {@code JAVA_HOME} is defined, otherwise
     * falls back to {@code java} (resolved via {@code PATH}).
     */
    @Parameter(property = "loadtest.javaExecutable")
    private String javaExecutable;

    /**
     * Extra JVM arguments (e.g., -Xmx512m).
     */
    @Parameter(property = "loadtest.jvmArgs")
    private List<String> jvmArgs;

    /**
     * Extra application arguments (appended after the Spring Boot arguments).
     */
    @Parameter(property = "loadtest.appArgs")
    private List<String> appArgs;

    /**
     * Maximum time in seconds to wait for the server to become ready.
     */
    @Parameter(property = "loadtest.startupTimeout", defaultValue = "120")
    private int startupTimeout;

    /**
     * Seconds between health check polls.
     */
    @Parameter(property = "loadtest.healthPollInterval", defaultValue = "2")
    private int healthPollInterval;

    @Override
    public void execute() throws MojoExecutionException {
        if (skip) {
            getLog().info("Skipping start-server");
            return;
        }

        // Build command
        List<String> command = new ArrayList<>();
        command.add(resolveJavaExecutable());
        if (jvmArgs != null) {
            command.addAll(jvmArgs);
        }
        command.add("-jar");
        command.add(serverJar);
        command.add("--server.port=" + serverPort);
        command.add("--management.server.port=" + managementPort);
        if (appArgs != null) {
            command.addAll(appArgs);
        }

        // Health URLs to poll
        List<String> healthUrls = List.of(
                "http://localhost:" + managementPort + "/actuator/health",
                "http://localhost:" + serverPort + "/");

        ServerProcess serverProcess = new ServerProcess();
        try {
            serverProcess.start(command);
            serverProcess.waitForReady(healthUrls,
                    Duration.ofSeconds(startupTimeout),
                    Duration.ofSeconds(healthPollInterval));
        } catch (Exception e) {
            serverProcess.stop(Duration.ofSeconds(5));
            throw new MojoExecutionException(
                    "Failed to start server: " + e.getMessage(), e);
        }

        // Store in project context for stop-server to retrieve
        project.setContextValue(CONTEXT_KEY, serverProcess);

        // Safety net: kill server if Maven is interrupted before stop-server
        // runs
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (serverProcess.isAlive()) {
                serverProcess.stop(Duration.ofSeconds(5));
            }
        }, "loadtest-server-shutdown-hook"));

        getLog().info("Server started on port " + serverPort + " (management: "
                + managementPort + ")");
    }

    private String resolveJavaExecutable() throws MojoExecutionException {
        if (javaExecutable != null && !javaExecutable.isBlank()) {
            if (!Files.isExecutable(Paths.get(javaExecutable))) {
                throw new MojoExecutionException(
                        String.format("Java executable '%s' does not exist.",
                                javaExecutable));
            }
            return javaExecutable;
        }
        String javaHome = System.getenv("JAVA_HOME");
        if (javaHome != null && !javaHome.isBlank()) {
            Path java = Paths.get(javaHome, "bin", "java");
            if (Files.isExecutable(java)) {
                return java.toString();
            }
            // On Windows, try java.exe
            Path javaExe = Paths.get(javaHome, "bin", "java.exe");
            if (Files.exists(javaExe)) {
                return javaExe.toString();
            }
        }
        return "java";
    }
}
