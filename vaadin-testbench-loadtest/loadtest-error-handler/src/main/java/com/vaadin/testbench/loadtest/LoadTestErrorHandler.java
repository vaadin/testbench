package com.vaadin.testbench.loadtest;

import com.vaadin.flow.server.ErrorEvent;
import com.vaadin.flow.server.ErrorHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Error handler for load testing that propagates exceptions instead of
 * swallowing them.
 * <p>
 * Vaadin's {@code DefaultErrorHandler} catches all exceptions during UIDL
 * processing and shows a notification in the UI. The HTTP response stays 200
 * with valid UIDL, making it invisible to k6 load tests.
 * <p>
 * This handler re-throws exceptions as {@link RuntimeException}, causing
 * Vaadin to return an error meta response (e.g., {@code {"meta":{"appError":...}}})
 * that k6 checks can detect and fail on.
 * <p>
 * Auto-registered via {@link LoadTestServiceInitListener}. Just add
 * {@code k6-loadtest-error-handler} as a dependency — no code changes needed.
 *
 * @see LoadTestServiceInitListener
 */
public class LoadTestErrorHandler implements ErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(LoadTestErrorHandler.class);

    @Override
    public void error(ErrorEvent event) {
        Throwable throwable = event.getThrowable();
        log.error("Error during request processing", throwable);

        // Re-throw so Vaadin returns an error response instead of status 200
        if (throwable instanceof RuntimeException re) {
            throw re;
        }
        throw new RuntimeException("Server error during load test", throwable);
    }
}
