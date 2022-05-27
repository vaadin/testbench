/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */

package com.vaadin.testbench.unit;

import javax.servlet.ServletException;
import java.util.Collection;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.StaticWebApplicationContext;

import com.vaadin.flow.function.VaadinApplicationInitializationBootstrap;
import com.vaadin.flow.server.VaadinContext;
import com.vaadin.flow.spring.SpringLookupInitializer;
import com.vaadin.testbench.unit.internal.UtilsKt;

/**
 * A SpringLookupInitializer that adapts Spring ApplicationContext to
 * WebApplicationContext and registers it into ServletContext so that lookup can
 * be initialized correctly.
 *
 * For internal use only.
 */
public class TestSpringLookupInitializer extends SpringLookupInitializer {

    private static ThreadLocal<ApplicationContext> applicationContext = new ThreadLocal<>();

    static void setApplicationContext(ApplicationContext appCtx) {
        if (appCtx != null) {
            applicationContext.set(appCtx);
        } else {
            applicationContext.remove();
        }
    }

    @Override
    public void initialize(VaadinContext context,
            Map<Class<?>, Collection<Class<?>>> services,
            VaadinApplicationInitializationBootstrap bootstrap)
            throws ServletException {
        ApplicationContext appCtx = applicationContext.get();
        applicationContext.remove();

        StaticWebApplicationContext webAppCtx = new StaticWebApplicationContext();
        webAppCtx.setServletContext(UtilsKt.getContext(context));
        webAppCtx.setParent(appCtx);
        webAppCtx.getServletContext().setAttribute(
                WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE,
                webAppCtx);
        super.initialize(context, services, bootstrap);
    }
}
