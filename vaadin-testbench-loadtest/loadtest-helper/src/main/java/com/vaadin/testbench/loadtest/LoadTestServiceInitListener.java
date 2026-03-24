package com.vaadin.testbench.loadtest;

import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Registers the {@link LoadTestErrorHandler} on every new Vaadin session.
 * <p>
 * This replaces the default error handler so that server-side exceptions
 * (e.g., {@code ObjectOptimisticLockingFailureException}) propagate as error
 * responses instead of being silently swallowed, making them visible to k6
 * load test checks.
 * <p>
 * Auto-registered via {@code META-INF/services/com.vaadin.flow.server.VaadinServiceInitListener}.
 * Just add {@code loadtest-helper} as a dependency — no code changes needed.
 */
public class LoadTestServiceInitListener implements VaadinServiceInitListener {

    private static final Logger log = LoggerFactory.getLogger(LoadTestServiceInitListener.class);

    private final LoadTestMetrics metrics = new LoadTestMetrics();

    @Override
    public void serviceInit(ServiceInitEvent event) {
        event.getSource().addSessionInitListener(sessionEvent -> {
            sessionEvent.getSession().setErrorHandler(new LoadTestErrorHandler());
            log.debug("LoadTestErrorHandler registered for session");
        });

        event.getSource().addUIInitListener(uiEvent -> metrics.trackUI(uiEvent.getUI()));
    }
}
