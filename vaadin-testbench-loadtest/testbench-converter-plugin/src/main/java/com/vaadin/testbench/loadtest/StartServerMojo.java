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
 * Starts an application server and waits for it to be ready. The process handle
 * is stored in the Maven project context so that {@code loadtest:stop-server}
 * can shut it down later.
 * <p>
 * Supports both Spring Boot and plain Jetty applications:
 * <ul>
 * <li><b>Spring Boot</b> — set {@code serverPort} (and optionally
 * {@code managementPort}). These are passed as {@code --server.port} and
 * {@code --management.server.port}, and {@code /actuator/health} is polled when
 * a management port is configured.</li>
 * <li><b>Plain Jetty</b> — set {@code httpPort}. This is passed as
 * {@code -Djetty.http.port} and only the application root is polled for
 * readiness.</li>
 * </ul>
 * <p>
 * Spring Boot example:
 *
 * <pre>
 * &lt;configuration&gt;
 *     &lt;serverJar&gt;${project.build.directory}/${project.build.finalName}.jar&lt;/serverJar&gt;
 *     &lt;serverPort&gt;8080&lt;/serverPort&gt;
 *     &lt;managementPort&gt;8082&lt;/managementPort&gt;
 * &lt;/configuration&gt;
 * </pre>
 *
 * Plain Jetty example:
 *
 * <pre>
 * &lt;configuration&gt;
 *     &lt;serverJar&gt;${project.build.directory}/${project.build.finalName}.jar&lt;/serverJar&gt;
 *     &lt;httpPort&gt;8080&lt;/httpPort&gt;
 * &lt;/configuration&gt;
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
     * Spring Boot application server port. When set, {@code --server.port} is
     * appended to the command and the application root is polled for readiness.
     */
    @Parameter(property = "loadtest.serverPort")
    private Integer serverPort;

    /**
     * Spring Boot Actuator management port. When set,
     * {@code --management.server.port} is appended and {@code /actuator/health}
     * is polled in addition to the application root.
     */
    @Parameter(property = "loadtest.managementPort")
    private Integer managementPort;

    /**
     * Jetty HTTP port. When set, {@code -Djetty.http.port} is added as a JVM
     * argument and the application root is polled for readiness.
     */
    @Parameter(property = "loadtest.httpPort")
    private Integer httpPort;

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
     * Extra application arguments appended to the command.
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

        if (serverPort == null && httpPort == null) {
            throw new MojoExecutionException(
                    "Either serverPort (Spring Boot) or httpPort (Jetty) must be set.");
        }

        int appPort = serverPort != null ? serverPort : httpPort;

        // Build command
        List<String> command = new ArrayList<>();
        command.add(resolveJavaExecutable());
        if (httpPort != null) {
            command.add("-Djetty.http.port=" + httpPort);
        }
        if (jvmArgs != null) {
            command.addAll(jvmArgs);
        }
        command.add("-jar");
        command.add(serverJar);
        if (serverPort != null) {
            command.add("--server.port=" + serverPort);
        }
        if (managementPort != null) {
            command.add("--management.server.port=" + managementPort);
        }
        if (appArgs != null) {
            command.addAll(appArgs);
        }

        // Health URLs to poll
        List<String> healthUrls = new ArrayList<>();
        if (managementPort != null) {
            healthUrls.add(
                    "http://localhost:" + managementPort + "/actuator/health");
        }
        healthUrls.add("http://localhost:" + appPort + "/");

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

        getLog().info("Server started on port " + appPort);
    }

    private String resolveJavaExecutable() throws MojoExecutionException {
        if (javaExecutable != null && !javaExecutable.isBlank()) {
            if (!Files.isExecutable(Paths.get(javaExecutable))) {
                throw new MojoExecutionException(String.format(
                        "Java executable '%s' does not exist or is not executable.",
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
