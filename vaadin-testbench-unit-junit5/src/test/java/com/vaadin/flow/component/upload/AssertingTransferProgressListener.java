/*
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */

package com.vaadin.flow.component.upload;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;

import com.vaadin.flow.server.streams.InMemoryUploadHandler;
import com.vaadin.flow.server.streams.TransferContext;
import com.vaadin.flow.server.streams.TransferProgressListener;
import com.vaadin.flow.server.streams.UploadEvent;
import com.vaadin.flow.server.streams.UploadHandler;
import com.vaadin.flow.server.streams.UploadMetadata;

class AssertingTransferProgressListener implements TransferProgressListener {

    final List<UploadedData> uploadedData = new ArrayList<>();
    protected int started;
    protected int completed;
    protected int failed;
    protected int progressCount;

    void assertStarted() {
        Assertions.assertEquals(1, started, "Upload should have started");
    }

    void assertStarted(int count) {
        Assertions.assertEquals(count, started,
                "Upload should have started " + count + " times");
    }

    void assertNotStarted() {
        Assertions.assertEquals(0, started, "Upload should not have started");
    }

    void assertCompleted() {
        Assertions.assertEquals(1, completed, "Upload should have completed");
    }

    void assertCompleted(int count) {
        Assertions.assertEquals(count, completed,
                "Upload should have completed " + count + " times");
    }

    void assertNotCompleted() {
        Assertions.assertEquals(0, completed,
                "Upload should not have completed");
    }

    void assertNotFailed() {
        Assertions.assertEquals(0, failed, "Upload should not have failed");
    }

    void assertFailed() {
        Assertions.assertEquals(1, failed, "Upload should have failed");
    }

    void assertFailed(int count) {
        Assertions.assertEquals(count, failed,
                "Upload should have failed " + count + " times");
    }

    void assertProgressCalled() {
        Assertions.assertTrue(progressCount > 0,
                "Progress should have been called");
    }

    UploadedData assertFileReceived() {
        Assertions.assertEquals(1, uploadedData.size(),
                "Expected a single uploaded file, but got "
                        + uploadedData.size());
        return uploadedData.get(0);
    }

    List<UploadedData> assertFilesReceived(int count) {
        Assertions.assertEquals(count, uploadedData.size(), "Expected " + count
                + " uploaded file, but got " + uploadedData.size());
        return new ArrayList<>(uploadedData);
    }

    @Override
    public void onStart(TransferContext context) {
        started++;
    }

    @Override
    public void onProgress(TransferContext context, long transferredBytes,
            long totalBytes) {
        progressCount++;
    }

    @Override
    public void onError(TransferContext context, IOException reason) {
        failed++;
    }

    @Override
    public void onComplete(TransferContext context, long transferredBytes) {
        completed++;
    }

    @Override
    public long progressReportInterval() {
        // Fixed to trigger at least one progress update
        return 10L;
    }

    void fileUploaded(UploadMetadata metadata, byte[] data) {
        uploadedData.add(new UploadedData(metadata, data));
    }

    record UploadedData(UploadMetadata metadata, byte[] data) {

    }

    UploadHandler asFailingHandler() {
        return new FailinInMemoryUploadHandler(this);
    }

    private static class FailinInMemoryUploadHandler
            extends InMemoryUploadHandler {

        private FailinInMemoryUploadHandler(TransferProgressListener listener) {
            super((metadata, data) -> Assertions.fail(
                    "Completion callback should not be called on upload failure"));
            addTransferProgressListener(listener);
        }

        @Override
        public void handleUploadRequest(UploadEvent event) throws IOException {
            UploadEvent newEvent = new UploadEvent(event.getRequest(),
                    event.getResponse(), event.getSession(),
                    event.getFileName(), event.getFileSize(),
                    event.getContentType(), event.getOwningElement(), null) {
                @Override
                public InputStream getInputStream() {
                    return new InputStream() {
                        @Override
                        public int read() throws IOException {
                            throw new IOException(
                                    "Error reading uploaded file");
                        }
                    };
                }
            };
            super.handleUploadRequest(newEvent);
        }
    }

}
