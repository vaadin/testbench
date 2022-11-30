/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.unit;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import java.util.Collection;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.vaadin.flow.function.VaadinApplicationInitializationBootstrap;
import com.vaadin.flow.server.VaadinContext;
import com.vaadin.flow.spring.SpringLookupInitializer;
import com.vaadin.testbench.unit.internal.UtilsKt;
import com.vaadin.testbench.unit.mocks.MockWebApplicationContext;
import com.vaadin.testbench.unit.mocks.SpringSecurityRequestCustomizer;

/**
 * A SpringLookupInitializer that adapts Spring ApplicationContext to
 * WebApplicationContext and registers it into ServletContext so that lookup can
 * be initialized correctly.
 *
 * For internal use only.
 */
public class UITestSpringLookupInitializer extends SpringLookupInitializer
        implements TestExecutionListener {

    private static final ThreadLocal<ApplicationContext> applicationContext = new ThreadLocal<>();

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        // SpringLookup requires a WebApplicationContext. Store current test
        // ApplicationContext so that it can be adapted later on by this
        // initializer
        UITestSpringLookupInitializer.applicationContext
                .set(testContext.getApplicationContext());
        ApplicationContext appCtx = testContext.getApplicationContext();
        // Register a MockRequestCustomizer bean so that request will have
        // access to authentication details from Spring Security
        if (appCtx instanceof ConfigurableApplicationContext
                && !appCtx.containsBean(
                        SpringSecurityRequestCustomizer.class.getName())) {
            ((ConfigurableApplicationContext) appCtx).getBeanFactory()
                    .registerSingleton(
                            SpringSecurityRequestCustomizer.class.getName(),
                            new SpringSecurityRequestCustomizer());
        }
    }

    @Override
    public void afterTestMethod(TestContext testContext) throws Exception {
        UITestSpringLookupInitializer.applicationContext.remove();
    }

    @Override
    public void initialize(VaadinContext context,
            Map<Class<?>, Collection<Class<?>>> services,
            VaadinApplicationInitializationBootstrap bootstrap)
            throws ServletException {
        ApplicationContext appCtx = applicationContext.get();
        ServletContext servletContext = UtilsKt.getContext(context);
        WebApplicationContext webAppCtx = WebApplicationContextUtils
                .getWebApplicationContext(servletContext);
        if (webAppCtx == null) {
            if (appCtx instanceof WebApplicationContext) {
                webAppCtx = (WebApplicationContext) appCtx;
            } else {
                webAppCtx = new MockWebApplicationContext(appCtx,
                        servletContext);
            }
            servletContext.setAttribute(
                    WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE,
                    webAppCtx);
        }
        super.initialize(context, services, bootstrap);
    }

}
