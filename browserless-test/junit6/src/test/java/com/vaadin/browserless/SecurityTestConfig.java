/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vaadin.browserless;

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
import com.vaadin.flow.server.auth.DefaultMenuAccessControl;
import com.vaadin.flow.server.auth.MenuAccessControl;
import com.vaadin.flow.server.auth.NavigationAccessControl;

// Empty configuration class used only to be able to bootstrap spring
// ApplicationContext
class SecurityTestConfig {

    @Configuration
    public static class NavigationAccessControlConfig extends Commons {
        // Registers test views and view access checker for testing purpose
        @Bean
        VaadinServiceInitListener setupViewSecurityScenario() {
            return event -> {
                RouteConfiguration routeConfiguration = RouteConfiguration
                        .forApplicationScope();
                routeConfiguration.setAnnotatedRoute(LoginView.class);
                routeConfiguration.setAnnotatedRoute(ProtectedView.class);
                event.getSource().addUIInitListener(uiEvent -> {
                    NavigationAccessControl accessControl = new NavigationAccessControl();
                    accessControl.setLoginView(LoginView.class);
                    uiEvent.getUI().addBeforeEnterListener(accessControl);
                });
            };
        }
    }

    private static class Commons {
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
                                        new SimpleGrantedAuthority(
                                                "ROLE_DEV")));
                    }
                    throw new UsernameNotFoundException(
                            "User " + username + " not exists");
                }
            };
        }

        @Bean
        MenuAccessControl menuAccessControl() {
            // SpringMenuAccessControl has been introduced in Vaadin 24.5
            // but the Testbench codebase currently supports also 24.4
            // Using reflection to prevent runtime issues.
            Class<? extends MenuAccessControl> clazz = springMenuAccessControlClass();
            if (clazz != null) {
                try {
                    return clazz.getConstructor().newInstance();
                } catch (Exception e) {
                    throw new AssertionError(
                            "Cannot instantiate SpringMenuAccessControl");
                }
            }
            return new DefaultMenuAccessControl();
        }
    }

    @SuppressWarnings("unchecked")
    static Class<? extends MenuAccessControl> springMenuAccessControlClass() {
        try {
            return (Class<? extends MenuAccessControl>) Class.forName(
                    "com.vaadin.flow.spring.security.SpringMenuAccessControl");
        } catch (ClassNotFoundException e) {
            // No op
        }
        return null;
    }
}
