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

import java.util.List;
import java.util.UUID;

import com.testapp.security.LoginView;
import com.testapp.security.ProtectedView;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.flow.server.auth.ViewAccessChecker;

// Empty configuration class used only to be able to bootstrap spring
// ApplicationContext
@Configuration
class SecurityTestConfig {

    // Registers test views and view access checker for testing purpose
    @Bean
    VaadinServiceInitListener setupViewSecurityScenario() {
        return event -> {
            RouteConfiguration routeConfiguration = RouteConfiguration
                    .forApplicationScope();
            routeConfiguration.setAnnotatedRoute(LoginView.class);
            routeConfiguration.setAnnotatedRoute(ProtectedView.class);
            event.getSource().addUIInitListener(uiEvent -> {
                ViewAccessChecker viewAccessChecker = new ViewAccessChecker();
                viewAccessChecker.setLoginView(LoginView.class);
                uiEvent.getUI().addBeforeEnterListener(viewAccessChecker);
            });
        };
    }

    @Bean
    UserDetailsService mockUserDetailsService() {

        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username)
                    throws UsernameNotFoundException {
                if ("user".equals(username)) {
                    return new User(username, UUID.randomUUID().toString(),
                            List.of(new SimpleGrantedAuthority(
                                    "ROLE_SUPERUSER"),
                                    new SimpleGrantedAuthority("ROLE_DEV")));
                }
                throw new UsernameNotFoundException(
                        "User " + username + " not exists");
            }
        };
    }
}
