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

import com.github.mvysny.dynatest.DynaTest
import com.github.mvysny.dynatest.expectList
import com.github.mvysny.dynatest.expectThrows
import jakarta.servlet.http.Cookie
import kotlin.test.expect

class MockResponseTest : DynaTest({
    lateinit var request: MockResponse
    beforeEach { request = MockResponse() }

    test("headers") {
        expect(null) { request.getHeader("foo") }
        expectList() { request.headerNames.toList() }
        expectList() { request.getHeaders("foo").toList() }
        request.setHeader("foo", "bar")
        expect("bar") { request.getHeader("foo") }
        expectList("foo") { request.headerNames.toList() }
        expectList("bar") { request.getHeaders("foo").toList() }
        request.headers["foo"] = arrayOf("bar", "baz")
        expect("bar") { request.getHeader("foo") }
        expectList("foo") { request.headerNames.toList() }
        expectList("bar", "baz") { request.getHeaders("foo").toList() }
    }

    test("cookies") {
        request.cookies += Cookie("foo", "bar")
        expect("bar") { request.getCookie("foo").value }
        expect(null) { request.findCookie("qqq") }
        expectThrows(IllegalStateException::class, "no such cookie with name baz. Available cookies: foo=bar") {
            request.getCookie("baz")
        }
    }
})
