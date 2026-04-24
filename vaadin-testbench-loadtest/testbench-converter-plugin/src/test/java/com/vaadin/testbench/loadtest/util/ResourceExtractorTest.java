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
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResourceExtractorTest {

    @TempDir
    Path tempDir;

    @Test
    void extractUtilities_writesVaadinAndSummaryScripts() throws IOException {
        Path extractDir = tempDir.resolve("k6-utils");
        ResourceExtractor extractor = new ResourceExtractor(extractDir);

        Path result = extractor.extractUtilities();

        assertEquals(extractDir, result);
        assertTrue(Files.exists(extractor.getVaadinHelpersScript()));
        assertTrue(Files.exists(extractor.getK6SummaryScript()));
        assertTrue(Files.size(extractor.getVaadinHelpersScript()) > 0);
        assertTrue(Files.size(extractor.getK6SummaryScript()) > 0);
    }

    @Test
    void cleanup_removesExtractedFilesAndDirectory() throws IOException {
        Path extractDir = tempDir.resolve("k6-utils");
        ResourceExtractor extractor = new ResourceExtractor(extractDir);
        extractor.extractUtilities();
        assertTrue(Files.exists(extractDir));

        extractor.cleanup();

        assertFalse(Files.exists(extractDir));
    }

    @Test
    void cleanup_onMissingDir_doesNotThrow() throws IOException {
        Path missingDir = tempDir.resolve("does-not-exist");
        ResourceExtractor extractor = new ResourceExtractor(missingDir);
        extractor.cleanup();
        assertFalse(Files.exists(missingDir));
    }
}
