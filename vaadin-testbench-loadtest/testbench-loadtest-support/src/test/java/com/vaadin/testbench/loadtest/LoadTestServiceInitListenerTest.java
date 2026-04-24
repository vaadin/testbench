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
import org.mockito.Mockito;

import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinService;

class LoadTestServiceInitListenerTest {

    @Test
    void serviceInit_registersSessionAndUiInitListeners() {
        VaadinService service = Mockito.mock(VaadinService.class);
        ServiceInitEvent event = Mockito.mock(ServiceInitEvent.class);
        Mockito.when(event.getSource()).thenReturn(service);

        LoadTestServiceInitListener listener = new LoadTestServiceInitListener();
        listener.serviceInit(event);

        Mockito.verify(service).addSessionInitListener(Mockito.any());
        Mockito.verify(service).addUIInitListener(Mockito.any());
    }
}
