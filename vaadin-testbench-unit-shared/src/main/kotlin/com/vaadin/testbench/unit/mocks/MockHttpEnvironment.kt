/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.unit.mocks

import java.util.Collections
import java.util.Enumeration
import jakarta.servlet.ServletConfig
import jakarta.servlet.ServletContext


open class MockServletConfig(val context: ServletContext) : ServletConfig {

    /**
     * Per-servlet init parameters.
     */
    var servletInitParams: MutableMap<String, String> = mutableMapOf()

    override fun getInitParameter(name: String): String? = servletInitParams[name]

    override fun getInitParameterNames(): Enumeration<String> = Collections.enumeration(servletInitParams.keys)

    override fun getServletName(): String = "Vaadin Servlet"

    override fun getServletContext(): ServletContext = context
}

internal fun <K, V> MutableMap<K, V>.putOrRemove(key: K, value: V?) {
    if (value == null) remove(key) else set(key, value)
}

object MockHttpEnvironment {
    /**
     * [MockRequest.getLocalPort]
     */
    var localPort: Int = 8080

    /**
     * [MockRequest.getServerPort]
     */
    var serverPort: Int = 8080

    /**
     * [MockRequest.getRemotePort]
     */
    var remotePort: Int = 8080

    /**
     * [MockRequest.getAuthType]
     */
    var authType: String? = null

    /**
     * [MockRequest.isSecure]
     */
    var isSecure: Boolean = false
}
