/**
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench.unit.mocks;

import kotlin.jvm.functions.Function0;
import org.jetbrains.annotations.NotNull;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.spring.SpringVaadinSession;
import com.vaadin.testbench.unit.internal.MockVaadin;

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
public class MockSpringVaadinSession extends SpringVaadinSession {
    @NotNull
    private final Function0<UI> uiFactory;

    public MockSpringVaadinSession(@NotNull VaadinService service,
            @NotNull Function0<UI> uiFactory) {
        super(service);
        this.uiFactory = uiFactory;
    }

    @Override
    public void close() {
        super.close();
        MockVaadin.afterSessionClose(this, uiFactory);
    }
}
