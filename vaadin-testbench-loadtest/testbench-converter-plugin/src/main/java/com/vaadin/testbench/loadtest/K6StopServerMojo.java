package com.vaadin.testbench.loadtest;

import com.vaadin.testbench.loadtest.util.ServerProcess;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.time.Duration;

/**
 * Stops a server previously started by {@code k6:start-server}.
 * Retrieves the process handle from the Maven project context.
 * <p>
 * Usage in pom.xml:
 * <pre>
 * &lt;execution&gt;
 *     &lt;phase&gt;post-integration-test&lt;/phase&gt;
 *     &lt;goals&gt;&lt;goal&gt;stop-server&lt;/goal&gt;&lt;/goals&gt;
 * &lt;/execution&gt;
 * </pre>
 */
@Mojo(name = "stop-server", defaultPhase = LifecyclePhase.POST_INTEGRATION_TEST)
public class K6StopServerMojo extends AbstractK6Mojo {

    /**
     * Seconds to wait for graceful shutdown before force-killing the process.
     */
    @Parameter(property = "k6.stopGracePeriod", defaultValue = "10")
    private int gracePeriod;

    @Override
    public void execute() throws MojoExecutionException {
        if (skip) {
            getLog().info("Skipping stop-server");
            return;
        }

        Object stored = project.getContextValue(K6StartServerMojo.CONTEXT_KEY);
        if (!(stored instanceof ServerProcess serverProcess)) {
            getLog().info("No server process found (was start-server executed?)");
            return;
        }

        if (!serverProcess.isAlive()) {
            getLog().info("Server process is no longer running");
        } else {
            serverProcess.stop(Duration.ofSeconds(gracePeriod));
        }

        project.setContextValue(K6StartServerMojo.CONTEXT_KEY, null);
    }
}
