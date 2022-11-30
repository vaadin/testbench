/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.unit.mocks

import com.vaadin.flow.component.UI
import com.vaadin.flow.server.VaadinService
import com.vaadin.flow.server.VaadinSession
import com.vaadin.testbench.unit.internal.MockVaadin

/**
 * A Vaadin Session with one important difference:
 *
 * * Creates a new session when this one is closed. This is used to simulate a logout
 *   which closes the session - we need to have a new fresh session to be able to continue testing.
 *   In order to do that, simply override [close], call `super.close()` then call
 *   [MockVaadin.afterSessionClose].
 */
open class MockVaadinSession(service: VaadinService,
                                    public val uiFactory: () -> UI
) : VaadinSession(service) {
    override fun close() {
        super.close()
        MockVaadin.afterSessionClose(this, uiFactory)
    }
}
