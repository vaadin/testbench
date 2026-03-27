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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Calculates hashes of source files to detect changes and enable caching. Used
 * to skip re-recording TestBench tests when sources haven't changed.
 */
public class SourceHasher {

    private static final Logger log = Logger
            .getLogger(SourceHasher.class.getName());

    public SourceHasher() {
    }

    /**
     * Calculates a combined hash of the test class source file and pom.xml.
     *
     * @param testWorkDir
     *            the project directory containing the test
     * @param testClass
     *            the test class name (e.g., "HelloWorldIT")
     * @return hex string of the combined hash, or null if files not found
     */
    public String calculateSourceHash(Path testWorkDir, String testClass) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Find and hash the test class source file
            Path testFile = findTestFile(testWorkDir, testClass);
            if (testFile != null && Files.exists(testFile)) {
                byte[] content = Files.readAllBytes(testFile);
                digest.update(content);
                log.fine("Hashed test file: " + testFile);
            } else {
                log.fine("Test file not found for: " + testClass);
            }

            // Hash the pom.xml
            Path pomFile = testWorkDir.resolve("pom.xml");
            if (Files.exists(pomFile)) {
                byte[] content = Files.readAllBytes(pomFile);
                digest.update(content);
                log.fine("Hashed pom.xml: " + pomFile);
            }

            byte[] hash = digest.digest();
            return HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException | IOException e) {
            log.warning("Failed to calculate source hash: " + e.getMessage());
            return null;
        }
    }

    /**
     * Finds a test class source file by searching common locations.
     */
    private Path findTestFile(Path projectDir, String className) {
        // Common test source locations
        List<String> searchPaths = List.of("src/test/java", "src/it/java");

        for (String searchPath : searchPaths) {
            Path searchDir = projectDir.resolve(searchPath);
            if (Files.exists(searchDir)) {
                try (Stream<Path> files = Files.walk(searchDir)) {
                    Path found = files
                            .filter(p -> p.getFileName().toString()
                                    .equals(className + ".java"))
                            .findFirst().orElse(null);
                    if (found != null) {
                        return found;
                    }
                } catch (IOException e) {
                    log.fine("Error searching " + searchDir + ": "
                            + e.getMessage());
                }
            }
        }
        return null;
    }

    /**
     * Reads a stored hash from a file.
     *
     * @param hashFile
     *            path to the hash file
     * @return the stored hash, or null if file doesn't exist
     */
    public String readStoredHash(Path hashFile) {
        if (Files.exists(hashFile)) {
            try {
                return Files.readString(hashFile).trim();
            } catch (IOException e) {
                log.fine("Failed to read hash file: " + e.getMessage());
            }
        }
        return null;
    }

    /**
     * Stores a hash to a file.
     *
     * @param hashFile
     *            path to the hash file
     * @param hash
     *            the hash to store
     */
    public void storeHash(Path hashFile, String hash) {
        try {
            Files.createDirectories(hashFile.getParent());
            Files.writeString(hashFile, hash);
            log.fine("Stored hash to: " + hashFile);
        } catch (IOException e) {
            log.warning("Failed to store hash: " + e.getMessage());
        }
    }
}
