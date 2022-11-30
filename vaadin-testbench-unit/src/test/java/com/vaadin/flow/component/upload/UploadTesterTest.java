/**
 * Copyright (C) 2000-${year} Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.upload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.testbench.unit.UIUnitTest;
import com.vaadin.testbench.unit.ViewPackages;

@ViewPackages
class UploadWrapTest extends UIUnitTest {

    private static final String FIRST_FILE_CONTENTS = "First file";
    private static final String SECOND_FILE_CONTENTS = "Second file";
    private static final String THIRD_FILE_CONTENTS = "Third file";

    @TempDir
    private static Path tempDir;
    private static File file1;
    private static File file2;
    private static File file3;

    UploadView view;
    UploadTester<Upload> single_;
    UploadTester<Upload> multi_;

    @BeforeAll
    static void setupTestFiles() throws IOException {
        file1 = tempDir.resolve("upload1.txt").toFile();
        Files.writeString(file1.toPath(), FIRST_FILE_CONTENTS);
        file2 = tempDir.resolve("file2.txt").toFile();
        Files.writeString(file2.toPath(), SECOND_FILE_CONTENTS);
        file3 = tempDir.resolve("third.txt").toFile();
        Files.writeString(file3.toPath(), THIRD_FILE_CONTENTS);
    }

    @BeforeEach
    void registerView() {
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(UploadView.class);
        view = navigate(UploadView.class);
        single_ = test(view.uploadSingle);
        multi_ = test(view.uploadMulti);
    }

    @Test
    void upload_notUsable_throws() {
        view.uploadSingle.setVisible(false);
        Assertions.assertThrows(IllegalStateException.class,
                () -> single_.upload(file1));
        Assertions.assertThrows(IllegalStateException.class,
                () -> single_.uploadAborted(file1));
        Assertions.assertThrows(IllegalStateException.class,
                () -> single_.uploadFailed(file1));
    }

    @Test
    void upload_singleFile_succeeds() {

        AtomicBoolean started = new AtomicBoolean();
        AtomicBoolean finished = new AtomicBoolean();
        AtomicBoolean allFinished = new AtomicBoolean();
        AtomicBoolean succeeded = new AtomicBoolean();
        AtomicBoolean failed = new AtomicBoolean();

        view.uploadSingle.addStartedListener(ev -> {
            Assertions.assertEquals(ev.getFileName(), file1.getName());
            Assertions.assertFalse(finished.get(),
                    "Finished should not have been notified on start");
            Assertions.assertFalse(allFinished.get(),
                    "All Finished should not have been notified on start");
            Assertions.assertFalse(succeeded.get(),
                    "Succeeded should not have been notified on start");
            started.set(true);
        });
        view.uploadSingle.addSucceededListener(ev -> {
            Assertions.assertTrue(started.get(),
                    "Started should have been notified on succeeded");
            Assertions.assertFalse(finished.get(),
                    "Finished should not have been notified on start");
            Assertions.assertFalse(allFinished.get(),
                    "All Finished should not have been notified on start");
            succeeded.set(true);
        });
        view.uploadSingle.addFinishedListener(ev -> {
            Assertions.assertTrue(started.get(),
                    "Started should have been notified on succeeded");
            Assertions.assertTrue(succeeded.get(),
                    "Succeeded should have been notified on succeeded");
            Assertions.assertFalse(allFinished.get(),
                    "All Finished should not have been notified on start");
            finished.set(true);
        });
        view.uploadSingle.addAllFinishedListener(ev -> {
            Assertions.assertTrue(started.get(),
                    "Started should have been notified on succeeded");
            Assertions.assertTrue(succeeded.get(),
                    "Succeeded should have been notified on succeeded");
            Assertions.assertTrue(finished.get(),
                    "Finished should have been notified on succeeded");
            allFinished.set(true);
        });
        view.uploadSingle.addFailedListener(ev -> Assertions
                .fail("Failed listener should not be notified"));

        single_.upload(file1);

        Assertions.assertTrue(started.get(),
                "Started listener was not notified");
        Assertions.assertTrue(succeeded.get(),
                "Succeeded listener was not notified");
        Assertions.assertFalse(failed.get(), "Failed listener was notified");
        Assertions.assertTrue(finished.get(),
                "Finished listener was not notified");
        Assertions.assertTrue(allFinished.get(),
                "All Finished listener was not notified");

        Assertions.assertEquals(file1.getName(), view.receiver.getFileName());
        Assertions.assertEquals("text/plain",
                view.receiver.getFileData().getMimeType());
        Assertions.assertEquals(FIRST_FILE_CONTENTS,
                inputStreamToString(view.receiver.getInputStream()));
    }

    @Test
    void upload_singleFile_failure() {

        AtomicBoolean started = new AtomicBoolean();
        AtomicBoolean finished = new AtomicBoolean();
        AtomicBoolean allFinished = new AtomicBoolean();
        AtomicBoolean succeeded = new AtomicBoolean();
        AtomicBoolean failed = new AtomicBoolean();

        view.uploadSingle.setReceiver((fileName, mimeType) -> {
            throw new UncheckedIOException(new IOException("BOOM!"));
        });
        view.uploadSingle.addStartedListener(ev -> {
            Assertions.assertEquals(ev.getFileName(), file1.getName());
            Assertions.assertFalse(finished.get(),
                    "Finished should not have been notified on start");
            Assertions.assertFalse(allFinished.get(),
                    "All Finished should not have been notified on start");
            Assertions.assertFalse(succeeded.get(),
                    "Succeeded should not have been notified on start");
            Assertions.assertFalse(failed.get(),
                    "Succeeded should not have been notified on start");
            started.set(true);
        });
        view.uploadSingle.addFailedListener(ev -> {
            Assertions.assertTrue(started.get(),
                    "Started should have been notified on succeeded");
            Assertions.assertFalse(finished.get(),
                    "Finished should not have been notified on start");
            Assertions.assertFalse(allFinished.get(),
                    "All Finished should not have been notified on start");
            failed.set(true);
        });
        view.uploadSingle.addFinishedListener(ev -> {
            Assertions.assertTrue(started.get(),
                    "Started should have been notified on succeeded");
            Assertions.assertTrue(failed.get(),
                    "Failed should have been notified on succeeded");
            Assertions.assertFalse(allFinished.get(),
                    "All Finished should not have been notified on start");
            finished.set(true);
        });
        view.uploadSingle.addAllFinishedListener(ev -> {
            Assertions.assertTrue(started.get(),
                    "Started should have been notified on succeeded");
            Assertions.assertTrue(failed.get(),
                    "Failed should have been notified on succeeded");
            Assertions.assertTrue(finished.get(),
                    "Finished should have been notified on succeeded");
            allFinished.set(true);
        });
        view.uploadSingle.addSucceededListener(ev -> Assertions
                .fail("Succeeded listener should not be notified"));

        Assertions.assertThrows(UncheckedIOException.class,
                () -> single_.upload(file1));

        Assertions.assertTrue(started.get(),
                "Started listener was not notified");
        Assertions.assertFalse(succeeded.get(),
                "Succeeded listener was notified");
        Assertions.assertTrue(failed.get(), "Failed listener was not notified");
        Assertions.assertTrue(finished.get(),
                "Finished listener was not notified");
        Assertions.assertTrue(allFinished.get(),
                "All Finished listener was not notified");

    }

    @Test
    void upload_multipleFiles_succeeds() {
        AtomicInteger started = new AtomicInteger();
        AtomicInteger finished = new AtomicInteger();
        AtomicInteger allFinished = new AtomicInteger();
        AtomicInteger succeeded = new AtomicInteger();
        AtomicInteger failed = new AtomicInteger();

        view.uploadMulti.addStartedListener(ev -> started.incrementAndGet());
        view.uploadMulti.addFailedListener(ev -> failed.incrementAndGet());
        view.uploadMulti.addFinishedListener(ev -> finished.incrementAndGet());
        view.uploadMulti
                .addAllFinishedListener(ev -> allFinished.incrementAndGet());
        view.uploadMulti
                .addSucceededListener(ev -> succeeded.incrementAndGet());

        multi_.uploadAll(file1, file2, file3);

        Assertions.assertEquals(3, started.get(),
                "Expected Started to be invoked once per uploaded file, but was called "
                        + succeeded.get() + " times");
        Assertions.assertEquals(3, succeeded.get(),
                "Expected Succeeded to be invoked once per uploaded file, but was called "
                        + succeeded.get() + " times");
        Assertions.assertEquals(3, finished.get(),
                "Expected Finished to be invoked once per uploaded file, but was called "
                        + finished.get() + " times");
        Assertions.assertEquals(0, failed.get(),
                "Failed should not be invoked");
        Assertions.assertEquals(1, allFinished.get(),
                "All Finished should be invoked once");

        Set<String> receivedFiles = view.multiReceiver.getFiles();
        Assertions.assertEquals(3, receivedFiles.size());
        Assertions.assertTrue(receivedFiles.containsAll(
                Set.of(file1.getName(), file2.getName(), file3.getName())));
    }

    @Test
    void uploadAll_noFiles_throws() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> multi_.uploadAll());
    }

    @Test
    void uploadAll_singleReceiver_throws() {
        Assertions.assertThrows(IllegalStateException.class,
                () -> single_.uploadAll(file1, file2, file3));
    }

    @Test
    void uploadAborted_failureNotified() {
        assertFailedUpload(single_::uploadAborted);
    }

    @Test
    void uploadFailed_failureNotified() {
        assertFailedUpload(single_::uploadFailed);
    }

    @Test
    void upload_fileCountExceeded_throws() {
        view.uploadMulti.setMaxFiles(2);
        Assertions.assertThrows(IllegalStateException.class,
                () -> multi_.uploadAll(file1, file2, file3));
    }

    void assertFailedUpload(BiConsumer<String, String> wrapperAction) {
        AtomicBoolean started = new AtomicBoolean();
        AtomicBoolean finished = new AtomicBoolean();
        AtomicBoolean allFinished = new AtomicBoolean();
        AtomicBoolean succeeded = new AtomicBoolean();
        AtomicBoolean failed = new AtomicBoolean();

        view.uploadSingle.addStartedListener(ev -> {
            Assertions.assertEquals(ev.getFileName(), file1.getName());
            Assertions.assertFalse(finished.get(),
                    "Finished should not have been notified on start");
            Assertions.assertFalse(allFinished.get(),
                    "All Finished should not have been notified on start");
            Assertions.assertFalse(succeeded.get(),
                    "Succeeded should not have been notified on start");
            Assertions.assertFalse(failed.get(),
                    "Succeeded should not have been notified on start");
            started.set(true);
        });
        view.uploadSingle.addFailedListener(ev -> {
            Assertions.assertTrue(started.get(),
                    "Started should have been notified on succeeded");
            Assertions.assertFalse(finished.get(),
                    "Finished should not have been notified on start");
            Assertions.assertFalse(allFinished.get(),
                    "All Finished should not have been notified on start");
            failed.set(true);
        });
        view.uploadSingle.addFinishedListener(ev -> {
            Assertions.assertTrue(started.get(),
                    "Started should have been notified on succeeded");
            Assertions.assertTrue(failed.get(),
                    "Failed should have been notified on succeeded");
            Assertions.assertFalse(allFinished.get(),
                    "All Finished should not have been notified on start");
            finished.set(true);
        });
        view.uploadSingle.addAllFinishedListener(ev -> {
            Assertions.assertTrue(started.get(),
                    "Started should have been notified on succeeded");
            Assertions.assertTrue(failed.get(),
                    "Failed should have been notified on succeeded");
            Assertions.assertTrue(finished.get(),
                    "Finished should have been notified on succeeded");
            allFinished.set(true);
        });
        view.uploadSingle.addSucceededListener(ev -> Assertions
                .fail("Succeeded listener should not be notified"));

        wrapperAction.accept(file1.getName(), "text/plain");

        Assertions.assertTrue(started.get(),
                "Started listener was not notified");
        Assertions.assertFalse(succeeded.get(),
                "Succeeded listener was notified");
        Assertions.assertTrue(failed.get(), "Failed listener was not notified");
        Assertions.assertTrue(finished.get(),
                "Finished listener was not notified");
        Assertions.assertTrue(allFinished.get(),
                "All Finished listener was not notified");
    }

    private String inputStreamToString(InputStream inputStream) {
        try {
            return new String(inputStream.readAllBytes(),
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
