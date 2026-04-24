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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class SourceHasherTest {

    @TempDir
    Path workDir;

    private void writeTestClass(String className, String content)
            throws IOException {
        Path dir = workDir.resolve("src/test/java/com/example");
        Files.createDirectories(dir);
        Files.writeString(dir.resolve(className + ".java"), content);
    }

    private void writePom(String content) throws IOException {
        Files.writeString(workDir.resolve("pom.xml"), content);
    }

    @Test
    void identicalInputsProduceSameHash() throws IOException {
        writeTestClass("HelloIT", "class HelloIT {}");
        writePom("<project/>");

        SourceHasher hasher = new SourceHasher();
        String a = hasher.calculateSourceHash(workDir, "HelloIT");
        String b = hasher.calculateSourceHash(workDir, "HelloIT");

        assertNotNull(a);
        assertEquals(a, b);
    }

    @Test
    void hashChangesWhenTestSourceChanges() throws IOException {
        writeTestClass("HelloIT", "class HelloIT {}");
        writePom("<project/>");

        SourceHasher hasher = new SourceHasher();
        String before = hasher.calculateSourceHash(workDir, "HelloIT");

        writeTestClass("HelloIT", "class HelloIT { /* modified */ }");
        String after = hasher.calculateSourceHash(workDir, "HelloIT");

        assertNotEquals(before, after);
    }

    @Test
    void hashChangesWhenPomChanges() throws IOException {
        writeTestClass("HelloIT", "class HelloIT {}");
        writePom("<project/>");

        SourceHasher hasher = new SourceHasher();
        String before = hasher.calculateSourceHash(workDir, "HelloIT");

        writePom("<project><modelVersion>4.0.0</modelVersion></project>");
        String after = hasher.calculateSourceHash(workDir, "HelloIT");

        assertNotEquals(before, after);
    }

    @Test
    void missingFilesStillProducesHash() {
        SourceHasher hasher = new SourceHasher();
        // Neither test file nor pom.xml exist — still returns a hex digest of
        // an empty digest update.
        String hash = hasher.calculateSourceHash(workDir, "Missing");
        assertNotNull(hash);
    }

    @Test
    void readStoredHash_returnsNullWhenMissing() {
        SourceHasher hasher = new SourceHasher();
        assertNull(hasher.readStoredHash(workDir.resolve("missing.hash")));
    }

    @Test
    void storeAndReadHash_roundTrips() throws IOException {
        Path file = workDir.resolve("nested/dir/source.hash");
        SourceHasher hasher = new SourceHasher();

        hasher.storeHash(file, "abc123");

        assertEquals("abc123", hasher.readStoredHash(file));
    }

    @Test
    void findsTestClassInItSourceLocation() throws IOException {
        Path dir = workDir.resolve("src/it/java/com/example");
        Files.createDirectories(dir);
        Files.writeString(dir.resolve("ItOnly.java"), "class ItOnly {}");

        SourceHasher hasher = new SourceHasher();
        String a = hasher.calculateSourceHash(workDir, "ItOnly");
        // Change the content to confirm the test file was included in the hash
        Files.writeString(dir.resolve("ItOnly.java"),
                "class ItOnly { /* tweaked */ }");
        String b = hasher.calculateSourceHash(workDir, "ItOnly");

        assertNotEquals(a, b);
    }
}
