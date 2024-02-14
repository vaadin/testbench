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

import com.github.mvysny.dynatest.DynaTest
import java.net.URL
import java.util.*
import kotlin.test.expect

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

    group("Composite") {
        compositeTests()
    }

    group("search spec") {
        searchSpecTest()
    }
    group("shortcuts") {
        shortcutsTestBatch()
    }


})
