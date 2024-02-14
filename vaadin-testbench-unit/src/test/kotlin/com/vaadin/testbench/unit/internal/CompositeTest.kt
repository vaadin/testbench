/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */

package com.vaadin.testbench.unit.internal

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.Composite
import com.vaadin.flow.component.Tag
import com.vaadin.flow.component.html.Span
import com.github.mvysny.dynatest.DynaNodeGroup
import com.github.mvysny.dynatest.DynaTestDsl

@DynaTestDsl
internal fun DynaNodeGroup.compositeTests() {
    beforeEach { MockVaadin.setup() }
    afterEach { MockVaadin.tearDown() }

    test("Composite<*> causes virtual children to be fetched twice") {
        class MyComposite : Composite<VirtualChildComponent>()

        val comp = MyComposite()
        comp._expectOne<Span> { text = "virtual child" }
    }
}

@Tag("my-test")
class VirtualChildComponent : Component() {
    init {
        val child = Span("virtual child")
        element.appendVirtualChild(child.element)
    }
}
