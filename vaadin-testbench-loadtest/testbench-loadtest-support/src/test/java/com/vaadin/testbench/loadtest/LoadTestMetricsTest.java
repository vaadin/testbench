/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.loadtest;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.AfterNavigationListener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class LoadTestMetricsTest {

    @Test
    void trackUI_registersAfterNavigationAndDetachListeners() {
        LoadTestMetrics metrics = new LoadTestMetrics();
        UI ui = Mockito.mock(UI.class);

        // No UIs have been tracked yet. The listeners below are registered
        // on a Mockito mock so they never fire, which means this pre-tracking
        // count is the only value the test can observe.
        assertEquals(0, metrics.getActiveUiCount());

        metrics.trackUI(ui);

        ArgumentCaptor<AfterNavigationListener> navCaptor = ArgumentCaptor
                .forClass(AfterNavigationListener.class);
        Mockito.verify(ui).addAfterNavigationListener(navCaptor.capture());
        assertNotNull(navCaptor.getValue());

        // Detach listener registered on the UI itself.
        Mockito.verify(ui).addDetachListener(Mockito.any());
    }
}
