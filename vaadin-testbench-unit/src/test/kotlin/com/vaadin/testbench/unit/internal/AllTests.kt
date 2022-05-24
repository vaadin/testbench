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

import java.net.URL
import java.util.Locale
import kotlin.test.expect
import com.github.mvysny.dynatest.DynaNodeGroup
import com.github.mvysny.dynatest.DynaTest
import com.github.mvysny.dynatest.DynaTestDsl

class AllTests : DynaTest({

    beforeEach {
        // make sure that Validator produces messages in English
        Locale.setDefault(Locale.ENGLISH)
    }

    test("flow-build-info.json doesn't exist") {
        val res: URL? = Thread.currentThread().contextClassLoader.getResource("META-INF/VAADIN/config/flow-build-info.json")
        expect(null, "flow-build-info.json exists on the classpath!") { res }
    }

    group("Depth First Tree Iterator") {
        depthFirstTreeIteratorTests()
    }

    group("basic utils") {
        basicUtilsTestbatch()
    }

    group("Element Utils") {
        elementUtilsTests()
    }

    group("Component Utils") {
        componentUtilsTests()
    }

    group("routes test") {
        routesTestBatch()
    }

    group("mock vaadin") {
        mockVaadinTest()
    }

    group("pretty print tree") {
        prettyPrintTreeTest()
    }

    group("locator") {
        group("with lifecycle hook testing") {
            locatorTest()
        }
        group("no lifecycle hook testing") {
            locatorTest2()
        }
    }

    group("search spec") {
        searchSpecTest()
    }

})
