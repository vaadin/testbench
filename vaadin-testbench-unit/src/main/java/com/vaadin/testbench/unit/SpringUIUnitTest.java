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

import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.vaadin.flow.component.UI;
import com.vaadin.testbench.unit.internal.MockVaadin;
import com.vaadin.testbench.unit.mocks.MockSpringServlet;

/**
 * Base JUnit 5 class for UI unit testing applications based on Spring
 * Framework.
 *
 * This class provides functionality of the Spring TestContext Framework, in
 * addition to set up a mock Vaadin Spring environment, so that views and
 * components built upon dependency injection and AOP can be correctly be
 * handled during unit testing.
 *
 * Usually when unit testing a UI view it is not needed to bootstrap the whole
 * application. Subclasses can therefore be annotated
 * with @{@link org.springframework.test.context.ContextConfiguration} or other
 * Spring Testing annotations to load only required component or to provide mock
 * services implementations.
 *
 * <pre>
 * {@code
 * &#64;ContextConfiguration(classes = ViewTestConfig.class)
 * class ViewTest extends SpringUIUnitTest {
 *
 * }
 * &#64;Configuration
 * class ViewTestConfig {
 *     &#64;Bean
 *     MyService myService() {
 *         return new my MockMyService();
 *     }
 * }
 * }
 * </pre>
 */
@ExtendWith({ SpringExtension.class })
@ContextConfiguration(classes = SpringSupport.class)
public abstract class SpringUIUnitTest extends UIUnitTest {

    @Autowired
    private ApplicationContext applicationContext;

    @BeforeEach
    protected void initVaadinEnvironment() {
        TestSpringLookupInitializer.setApplicationContext(applicationContext);
        MockSpringServlet servlet = new MockSpringServlet(
                discoverRoutes(scanPackage()), applicationContext, UI::new);
        MockVaadin.setup(UI::new, servlet,
                Set.of(TestSpringLookupInitializer.class));
    }

    @AfterEach
    protected void cleanVaadinEnvironment() {
        // Ensure ThreadLocal is cleaned
        TestSpringLookupInitializer.setApplicationContext(null);
        super.cleanVaadinEnvironment();
    }
}
