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

import java.io.Serializable
import java.lang.reflect.Field
import java.util.concurrent.atomic.AtomicReference
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.Tag
import com.vaadin.flow.router.BeforeEnterEvent
import com.vaadin.flow.router.ErrorParameter
import com.vaadin.flow.router.HasErrorParameter
import com.vaadin.flow.router.NotFoundException
import com.vaadin.flow.router.Route
import com.vaadin.flow.router.RouteData
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
                Class.forName(info.name).asSubclass(Component::class.java)
            }
            scanResult.getClassesImplementing(HasErrorParameter::class.java.name).mapTo(errorRoutes) { info: ClassInfo ->
                Class.forName(info.name).asSubclass(HasErrorParameter::class.java)
            }
        }

        // https://github.com/mvysny/karibu-testing/issues/50
        // if the app defines its own NotFoundException handler, remove MockRouteNotFoundError
        if (errorRoutes.any { it != MockRouteNotFoundError::class.java && it.isRouteNotFound }) {
            errorRoutes.remove(MockRouteNotFoundError::class.java)
        }

        println("Auto-discovered views: $this")
    }

    override fun toString(): String =
            "Routes(routes=${routes.joinToString { it.simpleName }}, errorRoutes=${errorRoutes.joinToString { it.simpleName }})"
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
open class MockRouteNotFoundError: Component(), HasErrorParameter<NotFoundException> {
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
        throw NotFoundException(message).apply { initCause(parameter.caughtException) }
    }

    private fun RouteData.toPrettyString(): String {
        val template = template
        val path: String = if (template.isNullOrBlank()) "<root>" else "/$template"
        return "${navigationTarget.simpleName} at '$path'"
    }
}
