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

import java.util.Collections
import java.util.Enumeration
import javax.servlet.ServletConfig
import javax.servlet.ServletContext


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
