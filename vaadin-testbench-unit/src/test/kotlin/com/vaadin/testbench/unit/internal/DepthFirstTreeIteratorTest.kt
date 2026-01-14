/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.unit.internal

import kotlin.test.expect
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.github.mvysny.dynatest.DynaNodeGroup
import com.github.mvysny.dynatest.expectList

fun DynaNodeGroup.depthFirstTreeIteratorTests() {
    test("DepthFirstTreeIterator") {
        val i = DepthFirstTreeIterator("0") { if (it.length > 2) listOf() else listOf("${it}0", "${it}1", "${it}2")}
        expectList("0", "00", "000", "001", "002", "01", "010", "011", "012", "02", "020", "021", "022") { i.asSequence().toList() }
    }

    test("walk") {
        val expected = mutableListOf<Component>()
        val root = VerticalLayout().apply {
            expected.add(this)
            add(Button("Foo").apply { expected.add(this) })
            add(HorizontalLayout().apply {
                expected.add(this)
                add(Span().apply { expected.add(this) })
            })
            add(VerticalLayout().apply { expected.add(this) })
        }
        expect(expected) { root.walk().toList() }
        expect(root) { root.walk().toList()[0] }
    }
}
