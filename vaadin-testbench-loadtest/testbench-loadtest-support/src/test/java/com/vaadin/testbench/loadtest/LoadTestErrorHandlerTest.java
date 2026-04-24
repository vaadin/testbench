/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.loadtest;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.vaadin.flow.server.ErrorEvent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LoadTestErrorHandlerTest {

    @Test
    void error_rethrowsRuntimeExceptionAsIs() {
        RuntimeException cause = new IllegalStateException("boom");
        ErrorEvent event = Mockito.mock(ErrorEvent.class);
        Mockito.when(event.getThrowable()).thenReturn(cause);

        LoadTestErrorHandler handler = new LoadTestErrorHandler();

        RuntimeException thrown = assertThrows(RuntimeException.class,
                () -> handler.error(event));
        assertSame(cause, thrown);
    }

    @Test
    void error_wrapsCheckedExceptionInRuntimeException() {
        IOException cause = new IOException("io failure");
        ErrorEvent event = Mockito.mock(ErrorEvent.class);
        Mockito.when(event.getThrowable()).thenReturn(cause);

        LoadTestErrorHandler handler = new LoadTestErrorHandler();

        RuntimeException thrown = assertThrows(RuntimeException.class,
                () -> handler.error(event));
        assertSame(cause, thrown.getCause());
        assertEquals("Server error during load test", thrown.getMessage());
    }
}
