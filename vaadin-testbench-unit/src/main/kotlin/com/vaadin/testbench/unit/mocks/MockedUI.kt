/**
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench.unit.mocks

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.ComponentEventListener
import com.vaadin.flow.component.UI
import com.vaadin.flow.shared.Registration
import java.util.concurrent.atomic.AtomicReference

/**
 * A simple no-op UI used by default by [com.vaadin.testbench.unit.MockVaadin.setup].
 * The class is open, in order to be extensible in user's library
 */
open class MockedUI : UI() {

    override fun setChildComponentModal(childComponent: Component?, modal: Boolean) {
        super.setChildComponentModal(childComponent, modal)
        if (modal) {
            val registrationCombination: AtomicReference<Registration?> = AtomicReference<Registration?>()
            registrationCombination.set(childComponent?.addDetachListener(ComponentEventListener {
                roundTrip()
                registrationCombination.getAndSet(null)?.remove()
            }))
        }
        roundTrip();
    }

    private fun roundTrip() {
        internals.stateTree.collectChanges { }
        internals.stateTree.runExecutionsBeforeClientResponse()
    }
}
