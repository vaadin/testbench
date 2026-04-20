/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.loadtest;

import java.time.Duration;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.vaadin.testbench.loadtest.util.ServerProcess;

/**
 * Stops a server previously started by {@code loadtest:start-server}. Retrieves
 * the process handle from the Maven project context.
 * <p>
 * Usage in pom.xml:
 *
 * <pre>
 * &lt;execution&gt;
 *     &lt;phase&gt;post-integration-test&lt;/phase&gt;
 *     &lt;goals&gt;&lt;goal&gt;stop-server&lt;/goal&gt;&lt;/goals&gt;
 * &lt;/execution&gt;
 * </pre>
 */
@Mojo(name = "stop-server", defaultPhase = LifecyclePhase.POST_INTEGRATION_TEST)
public class StopServerMojo extends AbstractK6Mojo {

    /**
     * Seconds to wait for graceful shutdown before force-killing the process.
     */
    @Parameter(property = "loadtest.stopGracePeriod", defaultValue = "10")
    private int gracePeriod;

    @Override
    public void execute() throws MojoExecutionException {
        if (skip) {
            getLog().info("Skipping stop-server");
            return;
        }

        Object stored = project.getContextValue(StartServerMojo.CONTEXT_KEY);
        if (!(stored instanceof ServerProcess serverProcess)) {
            getLog().info(
                    "No server process found (was start-server executed?)");
            return;
        }

        if (!serverProcess.isAlive()) {
            getLog().info("Server process is no longer running");
        } else {
            serverProcess.stop(Duration.ofSeconds(gracePeriod));
        }

        project.setContextValue(StartServerMojo.CONTEXT_KEY, null);
    }
}
