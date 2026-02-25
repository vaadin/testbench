/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vaadin.browserless.mocks;

import kotlin.jvm.functions.Function0;
import org.jetbrains.annotations.NotNull;

import com.vaadin.browserless.internal.MockVaadin;
import com.vaadin.browserless.internal.UIFactory;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinSession;

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
