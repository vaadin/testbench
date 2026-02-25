/*
 * Copyright (C) 2020-2026 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */

package com.vaadin.browserless.internal

import kotlin.test.expect
import com.vaadin.flow.component.Tag
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.littemplate.LitTemplate
import com.vaadin.flow.component.polymertemplate.PolymerTemplate
import com.vaadin.flow.component.polymertemplate.TemplateParser
import com.vaadin.flow.server.VaadinService
import com.vaadin.flow.templatemodel.TemplateModel
import com.github.mvysny.dynatest.DynaTest
import org.jsoup.nodes.Element

class AllTests : DynaTest({

    beforeEach { MockVaadin.setup() }
    afterEach { MockVaadin.tearDown() }


    test("Component.isTemplate") {
        expect(false) { Button("foo").isTemplate }
        expect(true) { MyLitTemplate().isTemplate }
        expect(true) { MyPolymerTemplate().isTemplate }
    }

})

internal interface MyModel : TemplateModel
internal class MyTemplateParser : TemplateParser {
    override fun getTemplateContent(
        clazz: Class<out PolymerTemplate<*>>?,
        tag: String?,
        service: VaadinService?
    ): TemplateParser.TemplateData {
        return TemplateParser.TemplateData("", Element(tag!!))
    }

}
@Tag("my-polymer")
internal class MyPolymerTemplate : PolymerTemplate<MyModel>(MyTemplateParser()) {

}
@Tag("my-lit")
internal class MyLitTemplate : LitTemplate()