/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
@file:Suppress("OverridingDeprecatedMember", "DEPRECATION")

package com.vaadin.testbench.unit.mocks

import java.io.Serializable
import java.util.Enumeration
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

import jakarta.servlet.ServletContext
import jakarta.servlet.http.HttpSession

/**
 * A standalone implementation of the [HttpSession] interface.
 */
open class MockHttpSession(
        private val sessionId: String,
        private val servletContext: ServletContext,
        private val creationTime: Long,
        private var maxInactiveInterval: Int
) : HttpSession, Serializable {
    private val attributes = ConcurrentHashMap<String, Any>()
    private val valid = AtomicBoolean(true)

    val isValid: Boolean get() = valid.get()

    constructor(session: HttpSession) : this(session.id, session.servletContext, session.lastAccessedTime, session.maxInactiveInterval) {
        copyAttributes(session)
    }

    fun destroy() {
        attributes.clear()
    }

    override fun getCreationTime(): Long {
        checkValid()
        return creationTime
    }

    override fun getId(): String = sessionId

    override fun getLastAccessedTime(): Long {
        checkValid()
        return 0
    }

    override fun getServletContext(): ServletContext = servletContext

    override fun setMaxInactiveInterval(interval: Int) {
        this.maxInactiveInterval = interval
    }

    override fun getMaxInactiveInterval(): Int = maxInactiveInterval

    override fun getAttribute(name: String): Any? {
        checkValid()
        return attributes[name]
    }

    override fun getAttributeNames(): Enumeration<String> {
        checkValid()
        return attributes.keys()
    }

    override fun setAttribute(name: String, value: Any?) {
        checkValid()
        attributes.putOrRemove(name, value)
    }

    override fun removeAttribute(name: String) {
        checkValid()
        attributes.remove(name)
    }

    fun copyAttributes(httpSession: HttpSession): MockHttpSession {
        httpSession.attributeNames.toList().forEach {
            attributes[it] = httpSession.getAttribute(it)
        }
        return this
    }

    override fun invalidate() {
        checkValid()
        valid.set(false)
    }

    override fun isNew(): Boolean {
        checkValid()
        return false
    }

    private fun checkValid() {
        if (!isValid) {
            throw IllegalStateException("invalidated: $this")
        }
    }

    override fun toString(): String =
        "MockHttpSession(sessionId='$sessionId', creationTime=$creationTime, maxInactiveInterval=$maxInactiveInterval, attributes=$attributes, isValid=$isValid)"

    companion object {
        private val sessionIdGenerator = AtomicInteger()
        fun create(ctx: ServletContext): MockHttpSession =
            MockHttpSession(
                sessionIdGenerator.incrementAndGet().toString(),
                ctx,
                System.currentTimeMillis(),
                30
            )
    }
}
