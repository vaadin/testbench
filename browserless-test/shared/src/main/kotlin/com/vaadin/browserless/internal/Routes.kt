/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.browserless.internal

import java.io.Serializable
import java.lang.reflect.Field
import java.util.concurrent.atomic.AtomicReference
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.Tag
import com.vaadin.flow.router.BeforeEnterEvent
import com.vaadin.flow.router.ErrorParameter
import com.vaadin.flow.router.HasErrorParameter
import com.vaadin.flow.router.InternalServerError
import com.vaadin.flow.router.NotFoundException
import com.vaadin.flow.router.Route
import com.vaadin.flow.router.RouteData
import com.vaadin.flow.router.RouteNotFoundError
import com.vaadin.flow.router.internal.DefaultErrorHandler
import com.vaadin.flow.server.HttpStatusCode
import com.vaadin.flow.server.VaadinContext
import com.vaadin.flow.server.startup.ApplicationRouteRegistry
import com.vaadin.flow.server.startup.RouteRegistryInitializer
import io.github.classgraph.ClassGraph
import io.github.classgraph.ClassInfo
import io.github.classgraph.ScanResult

/**
 * A configuration object of all routes and error routes in the application. Simply use [autoDiscoverViews] to discover everything.
 *
 * To speed up the tests, you can create one instance of this class only, then reuse that instance in every
 * call to [MockVaadin.setup].
 * @property routes a list of all route views in your application. Vaadin will ignore any routes not present here.
 * @property errorRoutes a list of all route views in your application. Vaadin will ignore any routes not present here.
 * @property skipPwaInit if true, the PWA initialization code is skipped in Vaadin, which dramatically speeds up
 * the [MockVaadin.setup] from 2 seconds to 50ms. Since that's usually what you want to do, this defaults to true.
 */
data class Routes(
        val routes: MutableSet<Class<out Component>> = mutableSetOf(),
        val errorRoutes: MutableSet<Class<out HasErrorParameter<*>>> = mutableSetOf(MockRouteNotFoundError::class.java),
        var skipPwaInit: Boolean = true
) : Serializable {

    /**
     * Registers all routes to Vaadin 15 registry. Automatically called from [MockVaadin.setup].
     */
    @Suppress("UNCHECKED_CAST")
    fun register(sc: VaadinContext) {
        RouteRegistryInitializer().onStartup(routes.toSet(), sc.context)
        checkNotNull(sc.context.getAttribute("com.vaadin.flow.server.startup.ApplicationRouteRegistry${'$'}ApplicationRouteRegistryWrapper")) {
            "RouteRegistryInitializer did not register the ApplicationRouteRegistry!"
        }
        val registry: ApplicationRouteRegistry = ApplicationRouteRegistry.getInstance(sc)
        registry.setErrorNavigationTargets(errorRoutes.map { it as Class<out Component> }.toSet())
        if (skipPwaInit) {
            registry.clearPwaClass()
        }
    }

    /**
     * Auto-discovers everything, registers it into `this` and returns `this`.
     * * [Route]-annotated views go into [routes]
     * * [HasErrorParameter] error views go into [errorRoutes]
     * After this function finishes, you can still modify the [routes] and [errorRoutes] sets,
     * for example you can clear the [errorRoutes] if there is some kind of misdetection.
     * @param packageNames set the package name for the detector to be faster; or provide null to scan the whole classpath, but this is quite slow.
     * @return this
     */
    @JvmOverloads
    fun autoDiscoverViews(vararg packageNames: String? = arrayOf()): Routes = apply {
        val classGraph: ClassGraph = ClassGraph().enableClassInfo()
                .enableAnnotationInfo()
                .acceptPackages(*(packageNames.map { it ?: "" }.toTypedArray()))
        classGraph.scan().use { scanResult: ScanResult ->
            scanResult.getClassesWithAnnotation(Route::class.java.name).mapTo(routes) { info: ClassInfo ->
                findClassOrThrow(info.name).asSubclass(Component::class.java)
            }
            scanResult.getClassesImplementing(HasErrorParameter::class.java.name).mapTo(errorRoutes) { info: ClassInfo ->
                findClassOrThrow(info.name).asSubclass(HasErrorParameter::class.java)
            }
        }

        cleanupErrorRoutes()

        println("Auto-discovered views: $this")
    }

    fun merge(other: Routes): Routes {
        return Routes(LinkedHashSet(this.routes), LinkedHashSet(this.errorRoutes), this.skipPwaInit).apply {
            routes.addAll(other.routes)
            errorRoutes.addAll(other.errorRoutes)
            cleanupErrorRoutes()
        }
    }

    override fun toString(): String =
            "Routes(routes=${routes.joinToString { it.simpleName }}, errorRoutes=${errorRoutes.joinToString { it.simpleName }})"


    private fun cleanupErrorRoutes() {
        // https://github.com/mvysny/karibu-testing/issues/50
        // if the app defines its own NotFoundException handler, remove MockRouteNotFoundError
        if (errorRoutes.any { it != MockRouteNotFoundError::class.java && it.isRouteNotFound }) {
            errorRoutes.remove(MockRouteNotFoundError::class.java)
        }

        // Replace default InternalServeError exception handler with an
        // implementation that exposes error details for PrettyPrinter
        errorRoutes.remove(InternalServerError::class.java)
        errorRoutes.add(MockInternalSeverError::class.java)
    }
}

/**
 * Clears the PWA class config from this registry.
 */
@Suppress("UNCHECKED_CAST")
fun ApplicationRouteRegistry.clearPwaClass() {
    val pwaClassField: Field = ApplicationRouteRegistry::class.java.getDeclaredField("pwaConfigurationClass").apply { isAccessible = true }
    val ref: AtomicReference<Class<*>> = pwaClassField.get(this) as AtomicReference<Class<*>>
    ref.set(null)
    if (pwaConfigurationClass != null) {
        throw AssertionError("PWA configuration class should have been removed")
    }
}

/**
 * This route gets registered by default in [Routes], so that Karibu-Testing can catch
 * any navigation to a missing route and can respond with an informative exception.
 */
@Tag(Tag.DIV)
@DefaultErrorHandler
open class MockRouteNotFoundError : RouteNotFoundError() {

    var cause: NotFoundException? = null;

    override fun setErrorParameter(event: BeforeEnterEvent, parameter: ErrorParameter<NotFoundException>): Int {

        val message: String = buildString {
            val path: String = event.location.path
            append("No route found for '").append(path).append("'")
            if (parameter.hasCustomMessage()) {
                append(": ").append(parameter.customMessage)
            }
            append("\nAvailable routes: ")
            val routes: List<RouteData> = event.source.registry.registeredRoutes
            append(routes.map { it.toPrettyString() })
            append("\nIf you'd like to revert back to the original Vaadin RouteNotFoundError, please remove the ${MockRouteNotFoundError::class.java} from Routes.errorRoutes")
        }
        cause = NotFoundException(message).apply { initCause(parameter.caughtException) }
        return super.setErrorParameter(event, parameter)
    }

    private fun RouteData.toPrettyString(): String {
        val template = template
        val path: String = if (template.isNullOrBlank()) "<root>" else "/$template"
        return "${navigationTarget.simpleName} at '$path'"
    }
}

@DefaultErrorHandler
open class MockInternalSeverError : InternalServerError() {

    override fun setErrorParameter(event: BeforeEnterEvent, parameter: ErrorParameter<Exception>): Int {
        element.setProperty("targetView", event.location.path)
        if (parameter.hasCustomMessage()) {
            element.setProperty("failureMessage", parameter.customMessage)
        } else {
            element.setProperty("failureMessage", parameter.exception.message)
        }
        element.setProperty("exceptionType", parameter.exception::class.java.name)
        element.setProperty("stackTrace", parameter.exception.stackTraceToString())
        return super.setErrorParameter(event, parameter)
    }
}
