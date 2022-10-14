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

import java.io.Serializable
import jakarta.servlet.http.HttpSession
import kotlin.test.expect
import com.github.mvysny.dynatest.DynaTest
import com.github.mvysny.dynatest.cloneBySerialization
import com.github.mvysny.dynatest.expectList
import com.github.mvysny.dynatest.expectThrows

class MockHttpSessionTest : DynaTest({
    lateinit var session: HttpSession
    beforeEach { session = MockHttpSession.create(MockContext()) }

    test("attributes") {
        expect(null) { session.getAttribute("foo") }
        expectList() { session.attributeNames.toList() }
        session.setAttribute("foo", "bar")
        expectList("foo") { session.attributeNames.toList() }
        expect("bar") { session.getAttribute("foo") }
        session.setAttribute("foo", null)
        expect(null) { session.getAttribute("foo") }
        expectList() { session.attributeNames.toList() }
        session.setAttribute("foo", "bar")
        expect("bar") { session.getAttribute("foo") }
        expectList("foo") { session.attributeNames.toList() }
        session.removeAttribute("foo")
        expect(null) { session.getAttribute("foo") }
        expectList() { session.attributeNames.toList() }
    }

    test("serializable") {
        session.setAttribute("foo", "bar")
        (session as Serializable).cloneBySerialization()
    }

    group("invalidate") {
        test("smoke") {
            session.invalidate()
            expect(false) { (session as MockHttpSession).isValid }
        }
        test("calling invalidate() second time throws") {
            session.invalidate()
            expectThrows(IllegalStateException::class) {
                session.invalidate()
            }
        }
        test("getAttribute() fails on invalidated session") {
            session.invalidate()
            expectThrows(IllegalStateException::class) {
                session.getAttribute("foo")
            }
        }
        test("getId() succeeds on invalidated session") {
            session.invalidate()
            session.id
        }
        test("getServletContext() succeeds on invalidated session") {
            session.invalidate()
            session.servletContext
        }
        test("maxActiveInterval succeeds on invalidated session") {
            session.invalidate()
            session.maxInactiveInterval = session.maxInactiveInterval + 1
        }
        test("getCreationTime() fails on invalidated session") {
            session.invalidate()
            expectThrows(IllegalStateException::class) {
                session.creationTime
            }
        }
        test("getLastAccessedTime() fails on invalidated session") {
            session.invalidate()
            expectThrows(IllegalStateException::class) {
                session.lastAccessedTime
            }
        }
        test("getAttributeNames() fails on invalidated session") {
            session.invalidate()
            expectThrows(IllegalStateException::class) {
                session.attributeNames
            }
        }
        test("getValueNames() fails on invalidated session") {
            session.invalidate()
            expectThrows(IllegalStateException::class) {
                session.valueNames
            }
        }
    }
})
