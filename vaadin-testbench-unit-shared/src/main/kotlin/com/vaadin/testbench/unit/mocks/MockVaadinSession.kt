/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.unit.mocks

import com.vaadin.flow.server.VaadinService
import com.vaadin.flow.server.VaadinSession
import com.vaadin.testbench.unit.internal.MockVaadin
import com.vaadin.testbench.unit.internal.UIFactory

/**
 * A Vaadin Session with one important difference:
 *
 * * Creates a new session when this one is closed. This is used to simulate a logout
 *   which closes the session - we need to have a new fresh session to be able to continue testing.
 *   In order to do that, simply override [close], call `super.close()` then call
 *   [MockVaadin.afterSessionClose].
 */
@Deprecated("Replace the vaadin-testbench-unit dependency with browserless-test-junit6 and use the corresponding class from the com.vaadin.browserless package instead. This class will be removed in a future version.")
open class MockVaadinSession(service: VaadinService,
                             val uiFactory: UIFactory
) : VaadinSession(service) {
    override fun close() {
        super.close()
        MockVaadin.afterSessionClose(this, uiFactory)
    }
}
