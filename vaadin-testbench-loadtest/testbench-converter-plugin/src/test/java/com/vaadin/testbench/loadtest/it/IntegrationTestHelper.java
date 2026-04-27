/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.loadtest.it;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Shared assertion helpers for {@code verify.bsh} scripts under
 * {@code src/it/}. Loaded onto the BeanShell classpath via the
 * {@code addTestClassPath=true} setting on the maven-invoker-plugin.
 */
public final class IntegrationTestHelper {

    private IntegrationTestHelper() {
    }

    public static void assertFileExists(Path path) {
        if (!Files.exists(path)) {
            throw new RuntimeException("Expected file does not exist: " + path);
        }
    }

    public static void assertFileDoesNotExist(Path path) {
        if (Files.exists(path)) {
            throw new RuntimeException(
                    "Expected file to be absent but found: " + path);
        }
    }

    public static void assertDirectoryExists(Path path) {
        if (!Files.isDirectory(path)) {
            throw new RuntimeException(
                    "Expected directory does not exist: " + path);
        }
    }

    public static String readFile(Path path) {
        try {
            return Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + path, e);
        }
    }

    public static void assertFileContains(Path path, String needle) {
        assertFileExists(path);
        String content = readFile(path);
        if (!content.contains(needle)) {
            throw new RuntimeException("File '" + path
                    + "' does not contain expected text: '" + needle + "'");
        }
    }

    public static void assertFileDoesNotContain(Path path, String needle) {
        assertFileExists(path);
        String content = readFile(path);
        if (content.contains(needle)) {
            throw new RuntimeException("File '" + path
                    + "' unexpectedly contains text: '" + needle + "'");
        }
    }

    public static void assertLogContains(Path basedir, String needle) {
        Path log = basedir.resolve("build.log");
        assertFileContains(log, needle);
    }

    public static void assertLogDoesNotContain(Path basedir, String needle) {
        Path log = basedir.resolve("build.log");
        assertFileDoesNotContain(log, needle);
    }
}
