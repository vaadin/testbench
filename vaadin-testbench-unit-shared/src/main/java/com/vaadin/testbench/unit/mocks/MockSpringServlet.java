/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.unit.mocks;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

import java.lang.reflect.Constructor;
import java.security.Principal;
import java.util.function.UnaryOperator;

import kotlin.jvm.functions.Function0;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.server.ServiceException;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.server.VaadinServletService;
import com.vaadin.flow.spring.SpringServlet;
import com.vaadin.testbench.unit.internal.Routes;
import com.vaadin.testbench.unit.internal.UtilsKt;

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
            super(SpringSecuritySupport.springSecurityRequestWrapper
                    .apply(request), vaadinService);
        }

    }

    /**
     * Augments the mock HTTP request backed by Vaadin request, with
     * authentication information provided by Spring Security framework.
     *
     * Nothing is done if Spring Security is not present on classpath.
     *
     * @param request
     *            the mock request instance
     */
    public static void applySpringSecurityIfPresent(MockRequest request) {
        if (SpringSecuritySupport.SPRING_SECURITY_PRESENT) {
            HttpServletRequest wrappedRequest = SpringSecuritySupport.springSecurityRequestWrapper
                    .apply(request);
            if (wrappedRequest instanceof MockRequest) {
                // Spring Security Web not on classpath
                applySimplifiedSpringSecurity(request);
            } else {
                request.setUserPrincipalInt(wrappedRequest.getUserPrincipal());
                request.setUserInRole(
                        (principal, role) -> wrappedRequest.isUserInRole(role));
            }
        }
    }

    private static void applySimplifiedSpringSecurity(MockRequest request) {
        Authentication authentication = SpringSecuritySupport.authentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            request.setUserPrincipalInt(null);
            request.setUserInRole((principal, role) -> false);
        } else {
            request.setUserPrincipalInt(authentication);
            request.setUserInRole(SpringSecuritySupport::isGranted);
        }
    }

    private static class SpringSecuritySupport {

        private static final String ROLE_PREFIX = "ROLE_";
        private static final boolean SPRING_SECURITY_PRESENT = hasSpringSecurity();
        private static final UnaryOperator<HttpServletRequest> springSecurityRequestWrapper = springSecurityRequestWrapper();

        private static Authentication authentication() {
            return SecurityContextHolder.getContext().getAuthentication();
        }

        private static boolean hasSpringSecurity() {
            return UtilsKt.findClass(
                    "org.springframework.security.core.context.SecurityContextHolder") != null;
        }

        private static UnaryOperator<HttpServletRequest> springSecurityRequestWrapper() {
            try {
                Constructor<?> constructor = UtilsKt.findClassOrThrow(
                        "org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper")
                        .getConstructor(HttpServletRequest.class, String.class);
                return req -> {
                    try {
                        return (HttpServletRequest) constructor.newInstance(req,
                                ROLE_PREFIX);
                    } catch (Exception e) {
                        throw new IllegalStateException(e);
                    }
                };
            } catch (Exception ex) {
                LoggerFactory.getLogger(MockSpringServlet.class).debug(
                        "Spring security WEB not found on classpath, principal and roles may not be available during tests");
            }
            return UnaryOperator.identity();
        }

        private static boolean isGranted(Principal principal, String role) {
            if (principal instanceof Authentication) {
                Authentication auth = (Authentication) principal;
                String prefixedRole;
                if (role != null && !role.startsWith(ROLE_PREFIX)) {
                    prefixedRole = ROLE_PREFIX + role;
                } else {
                    prefixedRole = role;
                }
                return auth.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .anyMatch(granted -> granted.equals(prefixedRole));
            }
            return false;
        }
    }

}
