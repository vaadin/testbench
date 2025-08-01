/**
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.upload;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.vaadin.flow.server.StreamResourceRegistry;
import com.vaadin.flow.server.StreamVariable;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinResponse;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.communication.streaming.StreamingEndEventImpl;
import com.vaadin.flow.server.communication.streaming.StreamingErrorEventImpl;
import com.vaadin.flow.server.communication.streaming.StreamingStartEventImpl;
import com.vaadin.flow.server.streams.TransferUtil;
import com.vaadin.flow.server.streams.UploadEvent;
import com.vaadin.flow.server.streams.UploadHandler;
import com.vaadin.testbench.unit.ComponentTester;
import com.vaadin.testbench.unit.Tests;
import com.vaadin.testbench.unit.internal.MockVaadin;

/**
 * Tester for Upload components.
 *
 * @param <T>
 *            the component type.
 */
@Tests(Upload.class)
public class UploadTester<T extends Upload> extends ComponentTester<T> {

    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public UploadTester(T component) {
        super(component);
    }

    /**
     * Send the file data to the Upload component as if it is uploaded in the
     * browser.
     *
     * @param fileName
     *            name of the file to upload
     * @param contentType
     *            content type of the file to upload
     * @param contents
     *            file contents as an array of bytes
     * @throws UncheckedIOException
     *             if the upload component fails to handle file contents
     */
    public void upload(String fileName, String contentType,
            InputStream contents) {
        doUpload(List.of(
                new UploadItem(fileName, contentType, contents::readAllBytes)));
    }

    /**
     * Send the file data to the Upload component as if it is uploaded in the
     * browser.
     *
     * @param fileName
     *            name of the file to upload
     * @param contentType
     *            content type of the file to upload
     * @param contents
     *            file contents as an array of bytes
     * @throws UncheckedIOException
     *             if the upload component fails to handle file contents
     */
    public void upload(String fileName, String contentType, byte[] contents) {
        doUpload(
                List.of(new UploadItem(fileName, contentType, () -> contents)));
    }

    /**
     * Send the file data to the Upload component as if it is uploaded in the
     * browser.
     *
     * The content type is detected from file name.
     *
     * @param file
     *            the file to upload
     * @throws UncheckedIOException
     *             if the upload component fails to handle file contents
     */
    public void upload(File file) {
        doUpload(List.of(new UploadItem(file.getName(),
                URLConnection.guessContentTypeFromName(file.getName()),
                () -> Files.readAllBytes(file.toPath()))));
    }

    /**
     * Simulates uploading multiple files at once.
     *
     * @param files
     *            files to upload
     */
    public void uploadAll(File... files) {
        uploadAll(List.of(files));
    }

    /**
     * Simulates uploading multiple files at once.
     *
     * @param files
     *            files to upload
     */
    public void uploadAll(Collection<File> files) {
        Receiver receiver = getComponent().getReceiver();
        if (receiver != null && !(receiver instanceof MultiFileReceiver)) {
            throw new IllegalStateException(
                    "Upload component is not configured with a MultiFileReceiver");
        }
        if (files.isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one file must be provided");
        }
        doUpload(files.stream()
                .map(f -> new UploadItem(f.getName(),
                        URLConnection.guessContentTypeFromName(f.getName()),
                        () -> Files.readAllBytes(f.toPath())))
                .collect(Collectors.toList()));
    }

    /**
     * Simulates upload interruption by user on browser.
     *
     * @param fileName
     *            name of uploading file
     * @param contentType
     *            content type of the uploading file
     */
    public void uploadAborted(String fileName, String contentType) {
        doFailUpload(fileName, contentType);
    }

    /**
     * Simulates upload interruption by user on browser.
     *
     * @param file
     *            uploading file
     */
    public void uploadAborted(File file) {
        doFailUpload(file.getName(),
                URLConnection.guessContentTypeFromName(file.getName()));
    }

    /**
     * Simulates a failure during file upload.
     *
     * @param file
     *            uploading file
     */
    public void uploadFailed(File file) {
        doFailUpload(file.getName(),
                URLConnection.guessContentTypeFromName(file.getName()));
    }

    /**
     * Simulates a failure during file upload.
     *
     * @param fileName
     *            name of uploading file
     * @param contentType
     *            content type of the uploading file
     */
    public void uploadFailed(String fileName, String contentType) {
        doFailUpload(fileName, contentType);
    }

    private void fireAllFinish() {
        try {
            getMethod("fireAllFinish").invoke(getComponent());
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private void doFailUpload(String fileName, String contentType) {
        ensureComponentIsUsable();
        if (useLegacyAPI()) {
            StreamVariable streamVariable = getGetStreamVariable();
            try {
                streamVariable.streamingStarted(
                        new StreamingStartEventImpl(fileName, contentType, 0));
                streamVariable.streamingFailed(new StreamingErrorEventImpl(
                        fileName, contentType, 0, 0, null));
            } finally {
                fireAllFinish();
            }
        } else {
            try {
                doUpload(List.of(new UploadItem(fileName, contentType, null)));
            } catch (UncheckedIOException ex) {
                // an exception is expected since we are simulating an error
            }
        }
    }

    private void doUpload(Collection<UploadItem> items) {
        if (useLegacyAPI()) {
            doLegacyUpload(items);
        } else {
            // A round trip is necessary to ensure upload handler registration
            roundTrip();
            var target = getComponent().getElement().getAttribute("target");
            StreamResourceRegistry.ElementStreamResource resource = VaadinSession
                    .getCurrent().getResourceRegistry()
                    .getResource(
                            StreamResourceRegistry.ElementStreamResource.class,
                            URI.create(target))
                    .orElseThrow(() -> new IllegalStateException(
                            "Upload handler is not registered"));
            if (resource
                    .getElementRequestHandler() instanceof UploadHandler uploadHandler) {
                RuntimeException caughtException;
                try {
                    items.forEach(item -> doUpload(item, uploadHandler));
                } finally {
                    caughtException = runUIQueue();
                    fireAllFinish();
                }
                if (caughtException != null) {
                    throw caughtException;
                }
            } else {
                throw new IllegalStateException(
                        "Invalid or null upload handler "
                                + resource.getElementRequestHandler());
            }
        }
    }

    private boolean useLegacyAPI() {
        return getComponent().getReceiver() != null;
    }

    private RuntimeException runUIQueue() {
        try {
            MockVaadin.runUIQueue();
        } catch (RuntimeException ex) {
            return ex;
        } catch (Exception ex) {
            // upload callbacks are executed in UI.access blocks.
            // we need to purge the queue to ensure listeners are
            // invoked
            // runUIQueue throws ExecutionException in case of failure
            // but the method does not declare any thrown exception
            // (kotlin magic)
            if (ex instanceof ExecutionException) {
                if (ex.getCause() instanceof RuntimeException re) {
                    throw re;
                } else {
                    throw new RuntimeException(ex.getCause());
                }
            }
            return new RuntimeException(ex);
        }
        return null;
    }

    private void doUpload(UploadItem item, UploadHandler uploadHandler) {
        long contentLength;
        InputStream inputStream;
        if (item.contentsProducer == null) {
            contentLength = 0L;
            inputStream = new InputStream() {
                @Override
                public int read() throws IOException {
                    throw new IOException("Simulated upload failure");
                }
            };
        } else {
            byte[] content = getUploadedItemContent(item.contentsProducer);
            contentLength = content.length;
            inputStream = new ByteArrayInputStream(content);
        }

        UploadEvent event = new UploadEvent(VaadinRequest.getCurrent(),
                VaadinResponse.getCurrent(), VaadinSession.getCurrent(),
                item.fileName, contentLength, item.contentType,
                getComponent().getElement(), null, null) {
            @Override
            public InputStream getInputStream() {
                return inputStream;
            }
        };
        try {
            Method method = TransferUtil.class.getDeclaredMethod(
                    "handleUploadRequest", UploadHandler.class,
                    UploadEvent.class);
            method.setAccessible(true);
            method.invoke(null, uploadHandler, event);
            uploadHandler.responseHandled(true, VaadinResponse.getCurrent());
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new IllegalStateException("Cannot handle upload request", e);
        } catch (InvocationTargetException e) {
            uploadHandler.responseHandled(false, VaadinResponse.getCurrent());
            if (e.getCause() instanceof RuntimeException re) {
                throw re;
            } else if (e.getCause() instanceof IOException ioe) {
                throw new UncheckedIOException(ioe);
            } else {
                throw new UncheckedIOException(new IOException(e));
            }
        }
    }

    private void doLegacyUpload(Collection<UploadItem> items) {
        ensureComponentIsUsable();

        try {
            StreamVariable streamVariable = getGetStreamVariable();
            AtomicReference<Exception> errorCollector = new AtomicReference<>();
            // Collect all upload related events. They will be fired after all
            // items completed because otherwise the current uploading count is
            // decremented too early and check with max file size may fail
            List<StreamVariable.StreamingEvent> events = items.stream()
                    .map(item -> doUpload(streamVariable, item,
                            ex -> errorCollector.compareAndSet(null, ex)))
                    .filter(Optional::isPresent).map(Optional::get)
                    .collect(Collectors.toList());

            events.forEach(ev -> handleUploadResult(streamVariable, ev));

            Exception ex = errorCollector.get();
            if (ex instanceof RuntimeException) {
                throw (RuntimeException) ex;
            } else if (ex instanceof IOException) {
                throw new UncheckedIOException((IOException) ex);
            } else if (ex != null) {
                throw new RuntimeException(ex);
            }
        } finally {
            fireAllFinish();
        }
    }

    private Optional<StreamVariable.StreamingEvent> doUpload(
            StreamVariable streamVariable, UploadItem item,
            Consumer<Exception> errorHandler) {
        String fileName = item.fileName;
        String contentType = item.contentType;
        Callable<byte[]> contentsProducer = item.contentsProducer;

        byte[] contents = getUploadedItemContent(contentsProducer);

        try {
            streamVariable.streamingStarted(new StreamingStartEventImpl(
                    fileName, contentType, contents.length));
            streamVariable.getOutputStream().write(contents);
            return Optional.of(new StreamingEndEventImpl(fileName, contentType,
                    contents.length));
        } catch (IOException ex) {
            errorHandler.accept(ex);
        } catch (Exception ex) {
            errorHandler.accept(ex);
            return Optional.of(new StreamingErrorEventImpl(fileName,
                    contentType, contents.length, 0, ex));
        }
        return Optional.empty();
    }

    private static byte[] getUploadedItemContent(
            Callable<byte[]> contentsProducer) {
        byte[] contents;
        try {
            contents = contentsProducer.call();
        } catch (RuntimeException ex) {
            throw ex;
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        } catch (Exception ex) {
            throw new UncheckedIOException(new IOException(ex));
        }
        Objects.requireNonNull(contents, "file contents cannot be null");
        return contents;
    }

    private void handleUploadResult(StreamVariable streamVariable,
            StreamVariable.StreamingEvent event) {
        if (event instanceof StreamVariable.StreamingErrorEvent) {
            streamVariable.streamingFailed(
                    (StreamVariable.StreamingErrorEvent) event);
        } else if (event instanceof StreamVariable.StreamingEndEvent) {
            streamVariable.streamingFinished(
                    (StreamVariable.StreamingEndEvent) event);
        }
    }

    private StreamVariable getGetStreamVariable() {
        try {
            return (StreamVariable) getMethod("getStreamVariable")
                    .invoke(getComponent());
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static class UploadItem {
        private final String fileName;
        private final String contentType;
        private final Callable<byte[]> contentsProducer;

        UploadItem(String fileName, String contentType,
                Callable<byte[]> contentsProducer) {
            this.fileName = Objects.requireNonNull(fileName,
                    "fileName cannot be null");
            this.contentType = contentType;
            this.contentsProducer = contentsProducer;
        }
    }

}
