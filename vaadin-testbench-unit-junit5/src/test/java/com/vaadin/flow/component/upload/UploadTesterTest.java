/**
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.upload;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.vaadin.flow.component.upload.AssertingTransferProgressListener.UploadedData;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.server.streams.UploadHandler;
import com.vaadin.testbench.unit.UIUnitTest;
import com.vaadin.testbench.unit.ViewPackages;

@ViewPackages
class UploadTesterTest extends UIUnitTest {

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
        AtomicBoolean allFinished = new AtomicBoolean();

        AssertingTransferProgressListener listener = new AssertingTransferProgressListener();
        view.uploadSingle.setUploadHandler(
                UploadHandler.inMemory(listener::fileUploaded, listener));
        view.uploadSingle.addAllFinishedListener(ev -> allFinished.set(true));

        single_.upload(file1);

        listener.assertStarted();
        listener.assertProgressCalled();
        listener.assertCompleted();
        listener.assertNotFailed();
        Assertions.assertTrue(allFinished.get(),
                "All Finished listener was not notified");
        Assertions.assertTrue(allFinished.get(),
                "Upload handler callback was not notified");

        UploadedData uploadedData = listener.assertFileReceived();
        Assertions.assertNotNull(uploadedData);
        Assertions.assertEquals(file1.getName(),
                uploadedData.metadata().fileName());
        Assertions.assertEquals("text/plain",
                uploadedData.metadata().contentType());
        Assertions.assertEquals(FIRST_FILE_CONTENTS,
                uploadedDataToString(uploadedData));
    }

    @Test
    void upload_singleFile_failure() {

        AtomicBoolean allFinished = new AtomicBoolean();
        AssertingTransferProgressListener listener = new AssertingTransferProgressListener();
        view.uploadSingle.setUploadHandler(listener.asFailingHandler());
        view.uploadSingle.addAllFinishedListener(ev -> allFinished.set(true));

        Assertions.assertThrows(UncheckedIOException.class,
                () -> single_.upload(file1));

        listener.assertStarted();
        listener.assertFailed();
        listener.assertNotCompleted();
        Assertions.assertTrue(allFinished.get(),
                "All Finished listener was not notified");

    }

    @Test
    void upload_multipleFiles_succeeds() {

        AtomicInteger allFinished = new AtomicInteger();
        AssertingTransferProgressListener listener = new AssertingTransferProgressListener();
        view.uploadMulti.setUploadHandler(
                UploadHandler.inMemory(listener::fileUploaded, listener));
        view.uploadMulti
                .addAllFinishedListener(ev -> allFinished.incrementAndGet());

        multi_.uploadAll(file1, file2, file3);

        listener.assertStarted(3);
        listener.assertCompleted(3);
        listener.assertNotFailed();
        Assertions.assertEquals(1, allFinished.get(),
                "All Finished should be invoked once");

        List<UploadedData> receivedFiles = listener.assertFilesReceived(3);
        Set<String> fileNames = receivedFiles.stream()
                .map(ud -> ud.metadata().fileName())
                .collect(Collectors.toSet());
        Assertions.assertEquals(
                Set.of(file1.getName(), file2.getName(), file3.getName()),
                fileNames);
    }

    @Test
    void uploadAll_noFiles_throws() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> multi_.uploadAll());
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

        AtomicBoolean allFinished = new AtomicBoolean();
        AssertingTransferProgressListener listener = new AssertingTransferProgressListener();
        view.uploadSingle.setUploadHandler(
                UploadHandler.inMemory(listener::fileUploaded, listener));

        view.uploadSingle.addAllFinishedListener(ev -> allFinished.set(true));

        wrapperAction.accept(file1.getName(), "text/plain");

        listener.assertStarted();
        listener.assertNotCompleted();
        listener.assertFailed();
        Assertions.assertTrue(allFinished.get(),
                "All Finished listener was not notified");
    }

    private String uploadedDataToString(UploadedData uploadedData) {
        return new String(uploadedData.data(), StandardCharsets.UTF_8);
    }
}
