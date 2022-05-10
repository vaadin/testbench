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

import java.io.File
import javax.servlet.ServletContext
import com.vaadin.flow.di.Lookup
import com.vaadin.flow.di.LookupInitializer
import com.vaadin.flow.server.VaadinContext
import com.vaadin.flow.server.VaadinServlet
import com.vaadin.flow.server.VaadinServletContext
import com.vaadin.flow.server.startup.LookupServletContainerInitializer
import elemental.json.Json
import elemental.json.JsonObject

object MockVaadinHelper {

    private val flowBuildInfo: JsonObject? by lazy(LazyThreadSafetyMode.PUBLICATION) {
        getTokenFileFromClassloader()
    }

    @JvmStatic
    fun mockFlowBuildInfo(servlet: VaadinServlet) {
        // we need to skip the test at DeploymentConfigurationFactory.verifyMode otherwise
        // testing a Vaadin 15 component module in npm mode without webpack.config.js nor flow-build-info.json would fail.
        if (flowBuildInfo == null) {
            // probably inside a Vaadin 15 component module. create a dummy token file so that
            // DeploymentConfigurationFactory.verifyMode() is happy.
            val tokenFile: File = File.createTempFile("flow-build-info", "json")
            tokenFile.writeText("{}")
            servlet.servletContext.setInitParameter("vaadin.frontend.token.file", tokenFile.absolutePath)
        }

        servlet.servletContext.setInitParameter("compatibilityMode", "false")
    }

    fun createMockContext(): ServletContext {
        val ctx = MockContext()
        init(ctx)
        return ctx
    }

    fun createMockVaadinContext(): VaadinContext =
            VaadinServletContext(createMockContext())

    fun getTokenFileFromClassloader(): JsonObject? {

        // Use DefaultApplicationConfigurationFactory.getTokenFileFromClassloader() to make sure to read
        // the same flow-build-info.json that Vaadin reads.

        val ctx: VaadinContext = MockVaadinHelper.createMockVaadinContext()
        val acf = lookup(ctx, Class.forName("com.vaadin.flow.server.startup.ApplicationConfigurationFactory"))
        checkNotNull(acf) { "ApplicationConfigurationFactory is null" }
        val dacfClass = Class.forName("com.vaadin.flow.server.startup.DefaultApplicationConfigurationFactory")
        if (dacfClass.isInstance(acf)) {
            val m = dacfClass.getDeclaredMethod("getTokenFileFromClassloader", VaadinContext::class.java)
            m.isAccessible = true
            val json = m.invoke(acf, ctx) as String? ?: return null
            return Json.parse(json)
        }
        return null
    }

    /**
     * Calls `Lookup.lookup(Class)`.
     */
    fun lookup(ctx: VaadinContext, clazz: Class<*>): Any? {
        val lookup = verifyHasLookup(ctx)
        return lookup.lookup(clazz)
    }

    /**
     * Verifies that the ctx has an instance of `com.vaadin.flow.di.Lookup` set, and returns it.
     * @return the instance of `com.vaadin.flow.di.Lookup`.
     */
    private fun verifyHasLookup(ctx: ServletContext): Lookup {
        val lookup: Any? = ctx.getAttribute("com.vaadin.flow.di.Lookup")
        checkNotNull(lookup) {
            "The context doesn't contain the Vaadin 19 Lookup class. Available attributes: " + ctx.attributeNames.toList()
        }
        return lookup as Lookup
    }
    private fun verifyHasLookup(ctx: VaadinContext): Lookup =
            verifyHasLookup((ctx as VaadinServletContext).context)

    private fun init(ctx: ServletContext) {

        val loaderInitializer = LookupServletContainerInitializer()

        val loaders = mutableSetOf<Class<*>>(
                LookupInitializer::class.java,
                Class.forName("com.vaadin.flow.di.LookupInitializer${'$'}ResourceProviderImpl")
        )

        fun tryLoad(clazz: String) {
            try {
                loaders.add(Class.forName(clazz))
            } catch (ex: ClassNotFoundException) {
                // sometimes customers don't include entire vaadin-core and exclude stuff like fusion on purpose.
                // load the class only if it exists.
            }
        }

        tryLoad("com.vaadin.flow.component.polymertemplate.rpc.PolymerPublishedEventRpcHandler")
        tryLoad("com.vaadin.fusion.frontend.EndpointGeneratorTaskFactoryImpl")
        loaderInitializer.onStartup(loaders, ctx)

        // verify that the Lookup has been set
        verifyHasLookup(ctx)
    }

}
