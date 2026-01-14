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

import com.vaadin.flow.di.Instantiator
import com.vaadin.flow.i18n.I18NProvider
import com.vaadin.flow.server.auth.MenuAccessControl
import net.bytebuddy.ByteBuddy
import net.bytebuddy.implementation.MethodCall
import net.bytebuddy.matcher.ElementMatchers

/**
 * Makes sure to load [MockNpmTemplateParser].
 */
open class MockInstantiator(val delegate: Instantiator) : Instantiator by delegate {

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any?> getOrCreate(type: Class<T>): T = when (type) {
        /*
        LitTemplateParser.LitTemplateParserFactory::class.java ->
            MockLitTemplateParserFactory as T
        MockInstantiatorV18.classNpmTemplateParserFactory ->
            MockInstantiatorV18.classMockNpmTemplateParserFactory.getConstructor().newInstance() as T
         */
        else -> delegate.getOrCreate(type)
    }

    override fun getMenuAccessControl(): MenuAccessControl = delegate.menuAccessControl

    override fun getI18NProvider(): I18NProvider? = delegate.i18NProvider

    companion object {
        @JvmStatic
        fun create(delegate: Instantiator): Instantiator {
            return MockInstantiator(delegate)
        }
    }
}

private object ByteBuddyUtils {
    /**
     * Subclasses [baseClass] and overrides [methodName] which will now return [withResult].
     */
    fun overrideMethod(baseClass: Class<*>, methodName: String, withResult: () -> Any?): Class<*> {
        return ByteBuddy().subclass(baseClass)
                .method(ElementMatchers.named(methodName))
                .intercept(MethodCall.call(withResult))
                .make()
                .load(ByteBuddyUtils::class.java.classLoader)
                .loaded
    }
}

/*
private object MockLitTemplateParserImpl : LitTemplateParserImpl() {
    override fun getSourcesFromTemplate(tag: String, url: String): String =
            MockNpmTemplateParser.mockGetSourcesFromTemplate(tag, url)

    // Vaadin 22.0.0.beta2+ adds a new `service` parameter, need to override that function as well.
    open fun getSourcesFromTemplate(service: VaadinService, tag: String, url: String): String =
            MockNpmTemplateParser.mockGetSourcesFromTemplate(tag, url)
}

private object MockLitTemplateParserFactory : LitTemplateParser.LitTemplateParserFactory() {
    override fun createParser() = MockLitTemplateParserImpl
}

*/