/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.unit.mocks;

import kotlin.jvm.functions.Function0;
import org.jetbrains.annotations.NotNull;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.testbench.unit.internal.MockVaadin;
import com.vaadin.testbench.unit.internal.UIFactory;

/**
 * A Vaadin Session with one important difference:
 *
 * <ul>
 * <li>Creates a new session when this one is closed. This is used to simulate a
 * logout which closes the session - we need to have a new fresh session to be
 * able to continue testing. In order to do that, simply override
 * {@link #close()}, call `super.close()` then call
 * {@link MockVaadin#afterSessionClose}.</li>
 * </ul>
 *
 * @author mavi
 */
public class MockSpringVaadinSession extends VaadinSession {
    @NotNull
    private final UIFactory uiFactory;

    @Deprecated(forRemoval = true)
    public MockSpringVaadinSession(@NotNull VaadinService service,
            @NotNull Function0<UI> uiFactory) {
        super(service);
        this.uiFactory = uiFactory::invoke;
    }

    public MockSpringVaadinSession(@NotNull VaadinService service,
            @NotNull UIFactory uiFactory) {
        super(service);
        this.uiFactory = uiFactory;
    }

    @Override
    public void close() {
        super.close();
        MockVaadin.afterSessionClose(this, uiFactory);
    }
}
