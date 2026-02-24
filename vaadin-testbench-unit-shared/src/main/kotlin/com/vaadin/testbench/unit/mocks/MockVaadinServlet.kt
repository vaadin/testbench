/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.unit.mocks

import java.lang.reflect.Constructor
import java.lang.reflect.Method
import com.vaadin.flow.function.DeploymentConfiguration
import com.vaadin.flow.server.*
import com.vaadin.testbench.unit.internal.Routes
import com.vaadin.testbench.unit.internal.UIFactory
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

/**
 * Makes sure that [routes] are properly registered, and that [MockService]
 * is used instead of vanilla [VaadinServletService].
 *
 * To use a custom servlet instead of this one, just pass it to [MockVaadin.setup].
 */
@Deprecated("Replace the vaadin-testbench-unit dependency with browserless-test-junit6 and use the corresponding class from the com.vaadin.browserless package instead. This class will be removed in a future version.")
open class MockVaadinServlet @JvmOverloads constructor(
        val routes: Routes = Routes(),
        val uiFactory: UIFactory = UIFactory{ MockedUI() }
) : VaadinServlet() {

    override fun createDeploymentConfiguration(): DeploymentConfiguration {
        MockVaadinHelper.mockFlowBuildInfo(this)
        return super.createDeploymentConfiguration()
    }

    override fun createServletService(deploymentConfiguration: DeploymentConfiguration): VaadinServletService {
        val service: VaadinServletService = MockService(this, deploymentConfiguration, uiFactory)
        service.init()
        routes.register(service.context as VaadinServletContext)
        return service
    }
}

private val _VaadinServlet_getService: Method =
    VaadinServlet::class.java.getDeclaredMethod("getService")

/**
 * Workaround for https://github.com/mvysny/karibu-testing/issues/66
 */
internal val VaadinServlet.serviceSafe: VaadinServletService? get() {
    // we need to use the reflection. The problem is that the signature
    // of the method differs between Vaadin versions:
    //
    // Vaadin 14.6: getService() returns VaadinService
    // Vaadin 20+: getService() returns VaadinServletService
    //
    // calling the method directly will cause MethodNotFoundError on Vaadin 20+
    return _VaadinServlet_getService.invoke(this) as VaadinServletService?
}

/**
 * Workaround for https://github.com/mvysny/karibu-testing/issues/66
 */
internal fun createVaadinServletRequest(request: HttpServletRequest, service: VaadinService): VaadinServletRequest {
    // we need to use the reflection. The problem is that the signature
    // of the constructor differs between Vaadin versions:
    //
    // Vaadin 14.6: VaadinServletRequest(HttpServletRequest, VaadinServletService)
    // Vaadin 20+: VaadinServletRequest(HttpServletRequest, VaadinService)
    //
    // calling the constructor directly will cause MethodNotFoundError.
    val constructor: Constructor<*> =
        VaadinServletRequest::class.java.declaredConstructors.first { it.parameterCount == 2 }
    return constructor.newInstance(request, service) as VaadinServletRequest
}

private val _VaadinServletResponse_constructor: Constructor<*> =
    VaadinServletResponse::class.java.declaredConstructors.first { it.parameterCount == 2 }

/**
 * Workaround for https://github.com/mvysny/karibu-testing/issues/66
 */
internal fun createVaadinServletResponse(response: HttpServletResponse, service: VaadinService): VaadinServletResponse {
    // we need to use the reflection. The problem is that the signature
    // of the constructor differs between Vaadin versions:
    //
    // Vaadin 14.6: VaadinServletResponse(HttpServletResponse, VaadinServletService)
    // Vaadin 20+: VaadinServletResponse(HttpServletResponse, VaadinService)
    //
    // calling the constructor directly will cause MethodNotFoundError.
    return _VaadinServletResponse_constructor.newInstance(response, service) as VaadinServletResponse
}

private val _WebBrowser_constructor: Constructor<WebBrowser> =
    WebBrowser::class.java.getDeclaredConstructor(VaadinRequest::class.java).apply {
        isAccessible = true
    }

internal fun WebBrowser(request: VaadinRequest): WebBrowser =
    _WebBrowser_constructor.newInstance(request)

private val _VaadinService_createVaadinSession: Method =
    VaadinService::class.java.getDeclaredMethod("createVaadinSession", VaadinRequest::class.java).apply {
        isAccessible = true
    }

internal fun VaadinService._createVaadinSession(request: VaadinRequest): VaadinSession {
    return _VaadinService_createVaadinSession.invoke(this, request) as VaadinSession
}
