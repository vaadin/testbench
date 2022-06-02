/**
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench.unit

import com.vaadin.flow.server.ServiceInitEvent
import com.vaadin.flow.server.VaadinServiceInitListener

/**
 * This class is picked up automatically by Vaadin (since it's registered via META-INF/services). We then test elsewhere
 * that MockVaadin-mocked env indeed picked up this init listener and executed it.
 */
class TestInitListener : VaadinServiceInitListener {
    override fun serviceInit(event: ServiceInitEvent) {
        serviceInitCalled = true
        event.source.addUIInitListener { e ->
            uiInitCalled = true
            e.ui.addBeforeEnterListener { uiBeforeEnterCalled = true }
        }
    }

    companion object {
        var serviceInitCalled: Boolean = false
        var uiInitCalled = false
        var uiBeforeEnterCalled = false
        fun clearInitFlags() {
            serviceInitCalled = false
            uiInitCalled = false
            uiBeforeEnterCalled = false
        }
    }
}
