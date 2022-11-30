/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.unit.internal

import com.github.mvysny.dynatest.DynaNodeGroup
import com.github.mvysny.dynatest.DynaTestDsl
import com.github.mvysny.dynatest.expectThrows
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.ClickEvent
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.dom.DomEvent
import elemental.json.Json
import kotlin.test.expect

@DynaTestDsl
internal fun DynaNodeGroup.basicUtilsTestbatch() {

    group("checkEditableByUser") {
        test("disabled textfield fails") {
            expectThrows(java.lang.IllegalStateException::class, "The AttachedTextField[DISABLED, value=''] is not enabled") {
                AttachedTextField().apply { isEnabled = false }.checkEditableByUser()
            }
        }
        test("invisible textfield fails") {
            expectThrows(
                java.lang.IllegalStateException::class,
                "The AttachedTextField[INVIS, value=''] is not effectively visible"
            ) {
                AttachedTextField().apply { isVisible = false }.checkEditableByUser()
            }
        }
        test("non attached textfield fails") {
            expectThrows(
                java.lang.IllegalStateException::class,
                "The TextField[value=''] is not attached"
            ) {
                TextField().checkEditableByUser()
            }
        }
        test("textfield in invisible layout fails") {
            expectThrows(java.lang.IllegalStateException::class, "The TextField[value=''] is not effectively visible") {
                VerticalLayout().apply {
                    isVisible = false
                    textField().also { it.checkEditableByUser() }
                }
            }
        }
        test("textfield succeeds") {
            AttachedTextField().checkEditableByUser()
        }
    }

    group("expectNotEditableByUser") {
        test("disabled textfield fails") {
            AttachedTextField().apply { isEnabled = false }.expectNotEditableByUser()
        }
        test("invisible textfield fails") {
            AttachedTextField().apply { isVisible = false }.expectNotEditableByUser()
        }
        test("textfield in invisible layout fails") {
            VerticalLayout().apply {
                isVisible = false
                textField().also { it.expectNotEditableByUser() }
            }
        }
        test("textfield succeeds") {
            expectThrows(AssertionError::class, "The AttachedTextField[value=''] is editable") {
                AttachedTextField().expectNotEditableByUser()
            }
        }
    }

    group("fireDomEvent()") {
        test("smoke") {
            Div()._fireDomEvent("click")
        }
        test("listeners are called") {
            val div = Div()
            lateinit var event: DomEvent
            div.element.addEventListener("click") { e -> event = e }
            div._fireDomEvent("click")
            expect("click") { event.type }
        }
        test("higher-level listeners are called") {
            val div = Div()
            lateinit var event: ClickEvent<Div>
            div.addClickListener { e -> event = e }
            div._fireDomEvent("click", Json.createObject().apply { put("event.screenX", 20.0) })
            expect(20) { event.screenX }
        }
    }

    test("_focus") {
        val f = AttachedTextField()
        var called = false
        f.addFocusListener { called = true }
        f._focus()
        expect(true) { called }
    }

    test("_blur") {
        val f = AttachedTextField()
        var called = false
        f.addBlurListener { called = true }
        f._blur()
        expect(true) { called }
    }
}
