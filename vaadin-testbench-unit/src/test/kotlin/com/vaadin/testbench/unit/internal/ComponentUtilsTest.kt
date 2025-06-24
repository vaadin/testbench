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
import com.github.mvysny.dynatest.expectThrows
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.Text
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.orderedlayout.FlexLayout
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextArea
import com.vaadin.flow.component.textfield.TextField
import kotlin.test.expect

fun DynaNodeGroup.componentUtilsTests() {
    beforeEach { MockVaadin.setup() }
    afterEach { MockVaadin.tearDown() }

    group("removeFromParent()") {
        test("component with no parent") {
            val t = Text("foo")
            t.removeFromParent()
            expect(null) { t.parent.orElse(null) }
        }
        test("nested component") {
            val fl = FlexLayout().apply { add(Span("foo")) }
            val label = fl.getComponentAt(0)
            expect(fl) { label.parent.get() }
            label.removeFromParent()
            expect(null) { label.parent.orElse(null) }
            expect(0) { fl.componentCount }
        }
        test("reattach") {
            val fl = FlexLayout().apply { add(Span("foo")) }
            val label = fl.getComponentAt(0)
            label.removeFromParent()
            fl.add(label)
            expect(fl) { label.parent.orElse(null) }
            expect(1) { fl.componentCount }
        }
    }

    test("serverClick()") {
        val b = Button()
        var clicked = 0
        b.addClickListener { clicked++ }
        b.serverClick()
        expect(1) { clicked }
    }

    test("tooltip") {
        val b = Button()
        expect(null) { b.tooltip.text }
        b.setTooltipText("")
        expect<String?>("") { b.tooltip.text } // https://youtrack.jetbrains.com/issue/KT-32501
        b.setTooltipText("foo")
        expect<String?>("foo") { b.tooltip.text } // https://youtrack.jetbrains.com/issue/KT-32501
        b.setTooltipText(null)
        expect(null) { b.tooltip.text }
    }

    test("addContextMenuListener smoke") {
        Button().addContextMenuListener({})
    }

    group("findAncestor") {
        test("null on no parent") {
            expect(null) { Button().findAncestor { false } }
        }
        test("null on no acceptance") {
            val button = Button()
            UI.getCurrent().add(button)
            expect(null) { button.findAncestor { false } }
        }
        test("finds UI") {
            val button = Button()
            UI.getCurrent().add(button)
            expect(UI.getCurrent()) { button.findAncestor { it is UI } }
        }
        test("doesn't find self") {
            val button = Button()
            UI.getCurrent().add(button)
            expect(UI.getCurrent()) { button.findAncestor { true } }
        }
    }

    group("findAncestorOrSelf") {
        test("null on no parent") {
            expect(null) { Button().findAncestorOrSelf { false } }
        }
        test("null on no acceptance") {
            val button = Button()
            UI.getCurrent().add(button)
            expect(null) { button.findAncestorOrSelf { false } }
        }
        test("finds self") {
            val button = Button()
            UI.getCurrent().add(button)
            expect(button) { button.findAncestorOrSelf { true } }
        }
    }

    test("isNestedIn") {
        expect(false) { Button().isNestedIn(UI.getCurrent()) }
        val button = Button()
        UI.getCurrent().add(button)
        expect(true) { button.isNestedIn(UI.getCurrent()) }
    }

    test("isAttached") {
        expect(true) { UI.getCurrent().isAttached() }
        expect(false) { Button("foo").isAttached() }
        expect(true) {
            val button = Button()
            UI.getCurrent().add(button)
            button.isAttached()
        }
        UI.getCurrent().close()
        expect(true) { UI.getCurrent().isAttached() }
    }

    test("insertBefore") {
        val l = HorizontalLayout()
        val first = Span("first")
        l.addComponentAsFirst(first)
        val second = Span("second")
        l.insertBefore(second, first)
        expect("second, first") { l.children.toList().map { it._text } .joinToString() }
        l.insertBefore(Span("third"), first)
        expect("second, third, first") { l.children.toList().map { it._text } .joinToString() }
    }

    test("hasChildren") {
        val l = HorizontalLayout()
        expect(false) { l.hasChildren }
        l.addComponentAsFirst(Span("first"))
        expect(true) { l.hasChildren }
        l.removeAll()
        expect(false) { l.hasChildren }
    }

    /*
    test("isNotEmpty") {
        val l = HorizontalLayout()
        expect(false) { l.isNotEmpty }
        l.addComponentAsFirst(Span("first"))
        expect(true) { l.isNotEmpty }
        l.removeAll()
        expect(false) { l.isNotEmpty }
    }

    test("isEmpty") {
        val l = HorizontalLayout()
        expect(true) { l.isEmpty }
        l.addComponentAsFirst(Span("first"))
        expect(false) { l.isEmpty }
        l.removeAll()
        expect(true) { l.isEmpty }
    }
     */

    group("classnames2") {
        test("addClassNames2") {
            val div = Div().apply { addClassNames2("foo  bar    baz") }
            expect(true) {
                div.classNames.containsAll(listOf("foo", "bar", "baz"))
            }
        }
        test("addClassNames2(vararg)") {
            val div = Div().apply { addClassNames2("foo  bar    baz", "  one  two") }
            expect(true) {
                div.classNames.containsAll(listOf("foo", "bar", "baz", "one", "two"))
            }
        }
        test("setClassNames2") {
            val div = Div().apply { addClassNames2("foo  bar    baz", "  one  two") }
            div.setClassNames2("  three four  ")
            expect(true) {
                div.classNames.containsAll(listOf("three", "four"))
            }
        }
        test("setClassNames2(vararg)") {
            val div = Div().apply { addClassNames2("foo  bar    baz", "  one  two") }
            div.setClassNames2("  three ", "four  ")
            expect(true) {
                div.classNames.containsAll(listOf("three", "four"))
            }
        }
        test("removeClassNames2") {
            val div = Div().apply { addClassNames2("foo  bar    baz", "  one  two") }
            div.removeClassNames2("  bar baz  ")
            expect(true) {
                div.classNames.containsAll(listOf("foo", "one", "two"))
            }
        }
        test("removeClassNames2(vararg)") {
            val div = Div().apply { addClassNames2("foo  bar    baz", "  one  two") }
            div.removeClassNames2("  bar ", "baz  ")
            expect(true) {
                div.classNames.containsAll(listOf("foo", "one", "two"))
            }
        }
    }

    test("placeholder") {
        var c: Component = TextField().apply { placeholder = "foo" }
        expect("foo") { c.placeholder }
        c.placeholder = ""
        expect("") { c.placeholder }
        c = TextArea().apply { placeholder = "foo" }
        expect("foo") { c.placeholder }
        c.placeholder = ""
        expect("") { c.placeholder }
        c = Button() // doesn't support placeholder
        expect(null) { c.placeholder }
        expectThrows(IllegalStateException::class, "Button doesn't support setting placeholder") {
            c.placeholder = "foo"
        }
    }

    group("label") {
        test("TextField") {
            val c: Component = TextField()
            expect("") { c.label }
            c.label = "foo"
            expect("foo") { c.label }
            c.label = ""
            expect("") { c.label }
        }
        test("Checkbox") {
            val c: Component = Checkbox()
            expect("") { c.label }
            c.label = "foo"
            expect("foo") { c.label }
            c.label = ""
            expect("") { c.label }
        }
    }

    test("caption") {
        var c: Component = Button("foo")
        expect("foo") { c.caption }
        c.caption = ""
        expect("") { c.caption }
        c = Checkbox().apply { caption = "foo" }
        expect("foo") { c.caption }
        c.caption = ""
        expect("") { c.caption }
        expect("") { FormLayout.FormItem().label }
        val fl = FormLayout()
        c = fl.addFormItem(Button(), "foo")
        expect("foo") { c.caption }
    }

    test("Button.caption") {
        val c = Button("foo")
        expect("foo") { c.caption }
        c.caption = ""
        expect("") { c.caption }
    }
}
