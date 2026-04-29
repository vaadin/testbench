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
import java.util.regex.Pattern;

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

    /**
     * Asserts that the file content matches the given regex (DOTALL, substring
     * search via {@link java.util.regex.Matcher#find()}).
     */
    public static void assertFileMatches(Path path, String regex) {
        assertFileExists(path);
        String content = readFile(path);
        if (!Pattern.compile(regex, Pattern.DOTALL).matcher(content).find()) {
            throw new RuntimeException("File '" + path
                    + "' does not match expected pattern: " + regex);
        }
    }

    /**
     * Asserts that each needle appears in the file and that they appear in the
     * given order. Useful to enforce "imports come before the entry function"
     * or "request 4 comes before request 8".
     */
    public static void assertFileContainsInOrder(Path path, String... needles) {
        assertFileExists(path);
        String content = readFile(path);
        int cursor = 0;
        String previous = null;
        for (String needle : needles) {
            int found = content.indexOf(needle, cursor);
            if (found < 0) {
                throw new RuntimeException("File '" + path
                        + "' missing expected snippet"
                        + (previous == null ? "" : " after '" + previous + "'")
                        + ": '" + needle + "'");
            }
            cursor = found + needle.length();
            previous = needle;
        }
    }

    /**
     * Asserts that {@code needle} appears between {@code sectionStart} and
     * {@code sectionEnd} in the file. Both markers are matched as literal
     * substrings; the first {@code sectionEnd} after {@code sectionStart}
     * closes the section. Throws if either marker is missing.
     */
    public static void assertSectionContains(Path path, String sectionStart,
            String sectionEnd, String needle) {
        String section = requireSection(path, sectionStart, sectionEnd);
        if (!section.contains(needle)) {
            throw new RuntimeException("File '" + path + "' section '"
                    + sectionStart + "' .. '" + sectionEnd
                    + "' does not contain expected text: '" + needle + "'");
        }
    }

    /**
     * Asserts that every {@code needle} appears between {@code sectionStart}
     * and {@code sectionEnd} in the file. The section is sliced once and
     * checked against each needle independently (no ordering enforced).
     */
    public static void assertSectionContains(Path path, String sectionStart,
            String sectionEnd, String[] needles) {
        String section = requireSection(path, sectionStart, sectionEnd);
        for (String needle : needles) {
            if (!section.contains(needle)) {
                throw new RuntimeException("File '" + path + "' section '"
                        + sectionStart + "' .. '" + sectionEnd
                        + "' does not contain expected text: '" + needle + "'");
            }
        }
    }

    /**
     * Symmetric counterpart of
     * {@link #assertSectionContains(Path, String, String, String)}.
     */
    public static void assertSectionDoesNotContain(Path path,
            String sectionStart, String sectionEnd, String needle) {
        String section = requireSection(path, sectionStart, sectionEnd);
        if (section.contains(needle)) {
            throw new RuntimeException("File '" + path + "' section '"
                    + sectionStart + "' .. '" + sectionEnd
                    + "' unexpectedly contains text: '" + needle + "'");
        }
    }

    private static String requireSection(Path path, String sectionStart,
            String sectionEnd) {
        assertFileExists(path);
        String content = readFile(path);
        int start = content.indexOf(sectionStart);
        if (start < 0) {
            throw new RuntimeException("File '" + path
                    + "' missing section start marker: '" + sectionStart + "'");
        }
        int end = content.indexOf(sectionEnd, start + sectionStart.length());
        if (end < 0) {
            throw new RuntimeException(
                    "File '" + path + "' missing section end marker '"
                            + sectionEnd + "' after '" + sectionStart + "'");
        }
        return content.substring(start, end);
    }
}
