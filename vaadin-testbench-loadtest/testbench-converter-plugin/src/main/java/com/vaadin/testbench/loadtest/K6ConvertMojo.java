/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.loadtest;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Converts a HAR file to a k6 load test script.
 * <p>
 * This goal performs three steps:
 * <ol>
 * <li>Filter external domains from the HAR file (optional)</li>
 * <li>Convert HAR to k6 using har-to-k6</li>
 * <li>Refactor the generated script for Vaadin compatibility</li>
 * </ol>
 * <p>
 * Example usage:
 * 
 * <pre>
 * mvn k6:convert -Dk6.harFile=recording.har
 * </pre>
 */
@Mojo(name = "convert", requiresProject = false)
public class K6ConvertMojo extends AbstractK6Mojo {

    /**
     * The HAR file to convert.
     */
    @Parameter(property = "k6.harFile", required = true)
    private File harFile;

    /**
     * Output file name for the generated test. If not specified, derives from
     * HAR file name.
     */
    @Parameter(property = "k6.outputName")
    private String outputName;

    /**
     * Skip filtering of external domains from the HAR file.
     */
    @Parameter(property = "k6.skipFilter", defaultValue = "false")
    private boolean skipFilter;

    /**
     * Skip the Vaadin-specific refactoring step.
     */
    @Parameter(property = "k6.skipRefactor", defaultValue = "false")
    private boolean skipRefactor;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skip) {
            getLog().info("Skipping k6:convert");
            return;
        }

        // Validate input
        Path harPath = harFile.toPath().toAbsolutePath();
        if (!Files.exists(harPath)) {
            throw new MojoExecutionException("HAR file not found: " + harPath);
        }

        getLog().info("Converting HAR file: " + harPath);

        // Initialize (extract utilities, validate prerequisites)
        initialize();

        // Prepare output
        Path outputPath = outputDir.toPath().toAbsolutePath();
        ensureDirectoryExists(outputPath);

        // Determine output file name
        String baseName = outputName;
        if (baseName == null || baseName.isEmpty()) {
            String harFileName = harFile.getName();
            baseName = harFileName.replaceAll("-recording\\.har$", "")
                    .replaceAll("\\.har$", "");
        }

        Path generatedFile = outputPath.resolve(baseName + "-generated.js");
        Path refactoredFile = outputPath.resolve(baseName + ".js");

        try {
            // Step 1: Filter external domains (optional)
            if (!skipFilter) {
                nodeRunner.filterHar(harPath);
            } else {
                getLog().info("Skipping HAR filtering");
            }

            // Step 2: Convert HAR to k6
            nodeRunner.harToK6(harPath, generatedFile, buildRecorderOptions());

            // Step 3: Refactor for Vaadin (optional)
            if (!skipRefactor) {
                nodeRunner.refactorK6Test(generatedFile, refactoredFile);

                // Copy Vaadin helpers
                copyVaadinHelpers(outputPath);

                getLog().info("");
                getLog().info("Conversion complete!");
                getLog().info("  Generated test: " + refactoredFile);
                getLog().info("");
                getLog().info("Run the test with:");
                getLog().info("  k6 run " + refactoredFile);
                getLog().info("");
                getLog().info("Or with custom server:");
                getLog().info(
                        "  k6 run -e APP_IP=192.168.1.100 -e APP_PORT=8080 "
                                + refactoredFile);
            } else {
                getLog().info("Skipping Vaadin refactoring");
                getLog().info("");
                getLog().info("Conversion complete!");
                getLog().info("  Generated test: " + generatedFile);
            }

        } catch (Exception e) {
            throw new MojoExecutionException("Conversion failed", e);
        }
    }
}
