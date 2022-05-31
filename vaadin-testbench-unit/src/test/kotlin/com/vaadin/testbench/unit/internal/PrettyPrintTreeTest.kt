/**
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench.unit.internal

import kotlin.test.expect
import kotlin.test.fail
import com.vaadin.flow.component.Html
import com.vaadin.flow.component.Text
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.contextmenu.ContextMenu
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.html.Anchor
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.Image
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.textfield.TextArea
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.BeforeEnterEvent
import com.vaadin.flow.router.ErrorParameter
import com.vaadin.flow.router.InternalServerError
import com.vaadin.flow.router.Location
import com.vaadin.flow.router.NavigationEvent
import com.vaadin.flow.router.NavigationTrigger
import com.vaadin.flow.server.VaadinService
import com.example.base.HelloWorldView
import com.github.mvysny.dynatest.DynaNodeGroup
import com.github.mvysny.dynatest.DynaTestDsl
import com.github.mvysny.karibudsl.v10.contextMenu
import com.github.mvysny.karibudsl.v10.div
import com.github.mvysny.karibudsl.v10.item
import com.github.mvysny.karibudsl.v10.text
import com.github.mvysny.karibudsl.v10.tooltip
import org.intellij.lang.annotations.Language

@DynaTestDsl
internal fun DynaNodeGroup.prettyPrintTreeTest() {
    lateinit var routes: Routes
    beforeGroup { routes = Routes().autoDiscoverViews("com.example.base") }
    beforeEach { MockVaadin.setup(routes) }
    afterEach { MockVaadin.tearDown() }

    test("Simple dump") {
        val div = Div().apply {
            text("Foo")
        }
        expect("""
└── Div[text='Foo']
    └── Text[text='Foo']
""".trim()) { div.toPrettyTree().trim() }
    }

    test("toPrettyStringHtmlComponent()") {
        expect("Text[text='foo']") { Text("foo").toPrettyString() }
        expect("Div[INVIS]") { Div().apply { isVisible = false }.toPrettyString() }
        expect("Html[<b>foo bar baz <i>foobar</i></b>]") {
            Html("\n    <b>foo\nbar\n    baz\n<i>foobar</i></b>").toPrettyString()
        }
        expect("HtmlSpan[innerHTML='aaa<b>bbbb</b>ccc']") {
            HtmlSpan("aaa<b>bbbb</b>ccc").toPrettyString()
        }
        expect("Div[@title='foobar']") {
            Div().apply { tooltip = "foobar" }.toPrettyString()
        }
        expect("Span[text='hi', @slot='prefix']") {
            val testSpan = Span("hi")
            TextField().prefixComponent = testSpan
            testSpan.toPrettyString()
        }
    }
    test("toPrettyStringTextField()") {
        expect("TextField[#25, value='']") {
            TextField().apply { id_ = "25" }.toPrettyString()
        }
        expect("TextArea[label='label', value='some text']") { TextArea("label").apply { value = "some text" }.toPrettyString() }
        expect("TextField[#25, value='', errorMessage='failed validation']") {
            TextField().apply { id_ = "25"; errorMessage = "failed validation" }.toPrettyString()
        }
        expect("TextField[label='foobar', value='']") {
            TextField("foobar").toPrettyString()
        }
    }
    test("toPrettyStringButton()") {
        expect("Button[caption='click me']") { Button("click me").toPrettyString() }
        expect("Button[icon='vaadin:abacus', @theme='icon']") { Button(VaadinIcon.ABACUS.create()).toPrettyString() }

    }
//    test("toPrettyStringGrid()") {
        /* TODO: uncomment when importing Grid stuff
        expect("Grid[<String>, dataprovider='ListDataProvider2{0 items}']") { Grid<String>(String::class.java).apply { setItems2(listOf()) }.toPrettyString() }
        expect("Column[header='My Header']") {
            Grid<Any>().run { addColumn { it }.apply { header2 = "My Header" } }.toPrettyString()
        }
        expect("Column[header='My Header', key='foo']") {
            Grid<Any>().run { addColumn { it }.apply { header2 = "My Header"; key = "foo" } }.toPrettyString()
        }
         */
//    }
    test("toPrettyStringAnchor()") {
        expect("Anchor[]") { Anchor().toPrettyString() }
        expect("Anchor[href='vaadin.com']") { Anchor("vaadin.com").toPrettyString() }
    }
    test("toPrettyStringImage()") {
        expect("Image[]") { Image().toPrettyString() }
        expect("Image[@src='vaadin.com']") { Image("vaadin.com", "").toPrettyString() }
    }
    test("toPrettyStringIcon()") {
        expect("Icon[@icon='vaadin:abacus']") { VaadinIcon.ABACUS.create().toPrettyString() }
    }
    test("toPrettyStringForm()") {
        expect("FormItem[label='foo']") { FormLayout().addFormItem(TextField(), "foo").toPrettyString() }
    }
    test("toPrettyStringCustomComponent()") {
        expect("MyComponentWithToString[my-div(25)]") {
            MyComponentWithToString().toPrettyString()
        }
    }

    test("styles not duplicated") {
        val div = Div()
        expect("Div[]") { div.toPrettyString() }
        div.setWidthFull()
        expect("Div[@style='width:100%']") { div.toPrettyString() }
        div.style.set("flex-shrink", "1")
        expect("Div[@style='width:100%;flex-shrink:1']") { div.toPrettyString() }
    }

    test("menu dump") {
        lateinit var cm: ContextMenu
        UI.getCurrent().div {
            cm = contextMenu {
                item("menu") {
                    isEnabled = false
                    item("click me", { fail("shouldn't be called") })
                }
                item("save as")
            }
        }
        expect("""
└── ContextMenu[opened='false']
    ├── MenuItem[DISABLED, text='menu']
    │   └── MenuItem[text='click me']
    └── MenuItem[text='save as']""".trim()) { cm.toPrettyTree().trim() }

    }

    /* TODO: uncomment when importing Grid stuff
    group("grid") {
        test("column headers") {
            val grid: Grid<String> = UI.getCurrent().grid<String> {
                addColumn(karibuDslI18n).setHeader("Hello!")
                setItems2(listOf())
            }
            expect("""
└── Grid[<String>, dataprovider='ListDataProvider2{0 items}']
    └── Column[header='Hello!']
""".trim()) { grid.toPrettyTree().trim() }
        }

        test("grid menu dump") {
            lateinit var cm: GridContextMenu<String>
            UI.getCurrent().grid<String> {
                cm = gridContextMenu {
                    item("menu") {
                        isEnabled = false
                        item("click me", { fail("shouldn't be called") })
                    }
                    item("save as")
                }
            }
            expect("""
└── GridContextMenu[]
    ├── GridMenuItem[DISABLED, text='menu']
    │   └── GridMenuItem[text='click me']
    └── GridMenuItem[text='save as']""".trim()) { cm.toPrettyTree().trim() }
        }

        // tests https://github.com/mvysny/karibu-testing/issues/37
        test("grid filters dump") {
            val grid: Grid<String> = UI.getCurrent().grid<String> {
                val col: Grid.Column<String> = addColumn(karibuDslI18n)
                appendHeaderRow().getCell(col).setComponent(TextField("Filter:"))
                setItems2(listOf())
            }
            expect("""
└── Grid[<String>, dataprovider='ListDataProvider2{0 items}']
    ├── TextField[label='Filter:', value='']
    └── Column[]""".trim()) { grid.toPrettyTree().trim() }
        }

        // tests https://github.com/mvysny/karibu-testing/issues/37
        test("grid filters dump for joined column") {
            val grid: Grid<String> = UI.getCurrent().grid<String> {
                val col1: Grid.Column<String> = addColumn(karibuDslI18n).setHeader("foo")
                val col2: Grid.Column<String> = addColumn(karibuDslI18n).setHeader("bar")
                appendHeaderRow()
                prependHeaderRow().join(col1, col2).setComponent(TextField("Filter:"))
                setItems2(listOf())
            }
            expect("""
└── Grid[<String>, dataprovider='ListDataProvider2{0 items}']
    ├── TextField[label='Filter:', value='']
    └── ColumnGroup[]
        ├── ColumnGroup[]
        │   └── Column[header='foo']
        └── ColumnGroup[]
            └── Column[header='bar']""".trim()) { grid.toPrettyTree().trim() }
        }
    }
         */

    group("toPrettyStringInternalServerError()") {
        fun createEvent(): BeforeEnterEvent {
            val router = VaadinService.getCurrent().router
            val navigationEvent = NavigationEvent(router, Location("helloworld"), UI.getCurrent(), NavigationTrigger.UI_NAVIGATE)
            val event = BeforeEnterEvent(navigationEvent, HelloWorldView::class.java, emptyList())
            return event
        }

        fun createErrorComponent(error: Exception?, message: String? = null): InternalServerError {
            val errorView = InternalServerError()
            val errorParam = ErrorParameter(Exception::class.java, error, message)
            errorView.setErrorParameter(createEvent(), errorParam)
            return errorView;
        }
        test("no cause exception") {
            expect("InternalServerError[targetView='helloworld', error='There was an exception while trying to navigate to 'helloworld'']") {
                val error = createErrorComponent(RuntimeException("OOPS!"))
                error.toPrettyString().trim()
                        .replace(Regex("(?s), stacktrace.*]"), "]")
            }
        }
        test("root cause exception") {
            expect("InternalServerError[targetView='helloworld', error='java.lang.Exception: BOOM!']") {
                val error = createErrorComponent(RuntimeException("OOPS!", Exception("BOOM!")))
                error.toPrettyString().trim()
                        .replace(Regex("(?s), stacktrace.*]"), "]")
            }
        }
        test("custom message") {
            expect("InternalServerError[targetView='helloworld', error='Something failed']") {
                val error = createErrorComponent(RuntimeException("BOOM!"), "Something failed")
                error.toPrettyString().trim()
                        .replace(Regex("(?s), stacktrace.*]"), "]")
            }
        }
    }

}

class MyComponentWithToString : Div() {
    override fun toString(): String = "my-div(25)"
}

/**
 * Populates its contents with given html snippet. The advantage over [com.vaadin.flow.component.Html]
 * is that any html is accepted - it doesn't have to be wrapped in a single root element.
 *
 * Note that it is the developer's responsibility to sanitize and remove any
 * dangerous parts of the HTML before sending it to the user through this
 * component. Passing raw input data to the user will possibly lead to
 * cross-site scripting attacks.
 *
 * This component does not expand the HTML fragment into a server side DOM tree
 * so you cannot traverse or modify the HTML on the server. The root element can
 * be accessed through [element] and the inner HTML through
 * [innerHTML].
 * @param innerHTML the HTML snippet to populate the span with.
 */
private class HtmlSpan(@Language("html") innerHTML: String = "") : Span() {
    /**
     * Sets the inner html. Removes any children added via [add].
     */
    @get:Language("html")
    @set:Language("html")
    var innerHTML: String
        get() = element.getProperty("innerHTML", "")
        set(value) {
            removeAll()
            element.setProperty("innerHTML", value)
        }

    init {
        this.innerHTML = innerHTML
    }
}
