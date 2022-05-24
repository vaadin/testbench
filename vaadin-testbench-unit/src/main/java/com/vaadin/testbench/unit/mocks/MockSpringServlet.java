/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */

package com.vaadin.testbench.unit.mocks;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

import kotlin.jvm.functions.Function0;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.server.ServiceException;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.server.VaadinServletService;
import com.vaadin.flow.spring.SpringServlet;
import com.vaadin.testbench.unit.internal.Routes;

/**
 * Makes sure that the {@link #routes} are properly registered, and that
 * {@link MockSpringServletService} is used instead of vanilla
 * {@link com.vaadin.flow.spring.SpringVaadinServletService}.
 *
 * @author mavi
 */
public class MockSpringServlet extends SpringServlet {
    @NotNull
    public final Routes routes;
    @NotNull
    public final ApplicationContext ctx;
    @NotNull
    public final Function0<UI> uiFactory;

    public MockSpringServlet(@NotNull Routes routes,
            @NotNull ApplicationContext ctx, @NotNull Function0<UI> uiFactory) {
        super(ctx, false);
        this.ctx = ctx;
        this.routes = routes;
        this.uiFactory = uiFactory;
    }

    @Override
    protected DeploymentConfiguration createDeploymentConfiguration()
            throws ServletException {
        MockVaadinHelper.mockFlowBuildInfo(this);
        return super.createDeploymentConfiguration();
    }

    @Override
    protected VaadinServletService createServletService(
            DeploymentConfiguration deploymentConfiguration)
            throws ServiceException {
        final VaadinServletService service = new MockSpringServletService(this,
                deploymentConfiguration, ctx, uiFactory);
        service.init();
        routes.register(service.getContext());
        return service;
    }

    @Override
    protected VaadinServletRequest createVaadinRequest(
            HttpServletRequest request) {
        return new MockSpringReq(request, getService());
    }

    static class MockSpringReq extends VaadinServletRequest {

        /**
         * Wraps a http servlet request and associates with a vaadin service.
         *
         * @param request
         *            the http servlet request to wrap
         * @param vaadinService
         */
        public MockSpringReq(HttpServletRequest request,
                VaadinServletService vaadinService) {
            super(request, vaadinService);
        }

        @Override
        public Principal getUserPrincipal() {
            return SecurityContextHolder.getContext().getAuthentication();
        }
    }
}
