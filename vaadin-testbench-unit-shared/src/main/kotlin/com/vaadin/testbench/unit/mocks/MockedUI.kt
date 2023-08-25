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

import java.lang.reflect.Method
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.ComponentEventListener
import com.vaadin.flow.component.UI
import com.vaadin.flow.shared.Registration
import java.util.concurrent.atomic.AtomicReference
import com.vaadin.flow.router.NavigationTrigger
import com.vaadin.flow.router.QueryParameters
import com.vaadin.flow.router.Location


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

    override fun navigate(locationString: String, queryParameters: QueryParameters) {

        // server-side routing only for tests as there is no client to handle routing.
        UI::class.java.getDeclaredMethod("renderViewForRoute", Location::class.java, NavigationTrigger::class.java)
                .apply { isAccessible = true }
                .invoke(this, Location(locationString, queryParameters), NavigationTrigger.UI_NAVIGATE)
        return
    }

    private fun roundTrip() {
        internals.stateTree.collectChanges { }
        internals.stateTree.runExecutionsBeforeClientResponse()
    }
}
