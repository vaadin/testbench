/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.loadtest.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * Extracts bundled JavaScript utilities from plugin resources to a temporary
 * directory. Only vaadin-k6-helpers.js is needed now since HAR processing is
 * done in Java.
 */
public class ResourceExtractor {

    private static final String RESOURCE_PREFIX = "k6-utils/";
    private static final List<String> RESOURCE_FILES = List
            .of("vaadin-k6-helpers.js");

    private final Path extractionDir;

    public ResourceExtractor(Path extractionDir) {
        this.extractionDir = extractionDir;
    }

    /**
     * Extracts all bundled k6 utilities to the extraction directory.
     *
     * @return the directory containing extracted utilities
     * @throws IOException
     *             if extraction fails
     */
    public Path extractUtilities() throws IOException {
        Files.createDirectories(extractionDir);

        for (String resourceFile : RESOURCE_FILES) {
            extractResource(resourceFile);
        }

        return extractionDir;
    }

    /**
     * Extracts a single resource file to the extraction directory.
     *
     * @param fileName
     *            the name of the resource file to extract
     * @throws IOException
     *             if the resource cannot be found or extraction fails
     */
    private void extractResource(String fileName) throws IOException {
        String resourcePath = RESOURCE_PREFIX + fileName;
        try (InputStream is = getClass().getClassLoader()
                .getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }
            Path targetPath = extractionDir.resolve(fileName);
            Files.copy(is, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * @return the path to the vaadin-k6-helpers.js file
     */
    public Path getVaadinHelpersScript() {
        return extractionDir.resolve("vaadin-k6-helpers.js");
    }

    /**
     * @return the extraction directory path
     */
    public Path getExtractionDir() {
        return extractionDir;
    }

    /**
     * Cleans up the extraction directory.
     *
     * @throws IOException
     *             if cleanup fails
     */
    public void cleanup() throws IOException {
        if (Files.exists(extractionDir)) {
            Files.walk(extractionDir).sorted((a, b) -> -a.compareTo(b))
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                            // Ignore cleanup errors
                        }
                    });
        }
    }
}
