/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */

package com.vaadin.testbench.unit.quarkus;

import jakarta.enterprise.inject.spi.CDI;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;

import com.vaadin.testbench.unit.UIUnitTest;
import com.vaadin.testbench.unit.internal.MockVaadin;
import com.vaadin.testbench.unit.mocks.MockedUI;
import com.vaadin.testbench.unit.quarkus.mocks.MockQuarkusServlet;

/**
 * Base JUnit 5 class for UI unit testing applications based on Quarkus stack.
 *
 * This class sets up a mock Vaadin Quarkus environment, so that views and
 * components built upon dependency injection and AOP can be correctly be
 * handled during unit testing. A CDI container is required for the test to
 * work.
 *
 * With Quarkus testing framework, setting up the CDI environment can be
 * achieved by annotating the {@link UIUnitTest} class with
 * {@code @QuarkusTest}. The annotation registers a JUnit extension that deploys
 * and starts the whole application, including the initialization of the CDI
 * container. The drawback of this approach is that the application also starts
 * the HTTP server, effectively initializing the entire Vaadin application.
 * Tests are still performed in a mocked environment, but it is not possible
 * out-of-the-box to run them in isolation, with only the components needed by
 * the test.
 *
 * <pre>
 * {
 *     &#64;QuarkusTest
 *     class MainViewTest extends QuarkusUIUnitTest {
 *
 *         &#64;Test
 *         void accessView() {
 *             MainView mainView = navigate(MainView.class);
 *             Assertions.assertNotNull(mainView);
 *         }
 *     }
 * }
 * </pre>
 *
 * An alternative since Quarkus 3.2 could be the usage of (<a href=
 * "https://quarkus.io/guides/getting-started-testing#testing-components">@QuarkusComponentTest
 * annotation</a>), that targets testing of single CDI components. However, this
 * kind of tests require a lot of manual setup, because every component involved
 * in the test must be explicitly defined (including Vaadin Quarkus extension
 * classes, since deployment augmentation is not performed). In addition, beans
 * may still be removed by the CDI container because considered unused or not
 * found because of missing bean defining annotations. For the above reasons,
 * currently, using {@code @QuarkusComponentTest} is not recommended.
 */

public abstract class QuarkusUIUnitTest extends UIUnitTest {

    @BeforeEach
    protected void initVaadinEnvironment() {
        scanTesters();
        MockQuarkusServlet servlet = new MockQuarkusServlet(discoverRoutes(),
                CDI.current().getBeanManager(), MockedUI::new);
        MockVaadin.setup(MockedUI::new, servlet, lookupServices());
    }

    @Override
    protected Set<Class<?>> lookupServices() {
        return Set.of(QuarkusTestLookupInitializer.class);
    }
}
