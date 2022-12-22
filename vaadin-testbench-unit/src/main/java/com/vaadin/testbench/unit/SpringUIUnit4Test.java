/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.unit;

import java.util.Set;

import com.vaadin.flow.internal.UsageStatistics;
import com.vaadin.testbench.TestBenchVersion;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;

import com.vaadin.testbench.unit.internal.MockVaadin;
import com.vaadin.testbench.unit.mocks.MockSpringServlet;
import com.vaadin.testbench.unit.mocks.MockedUI;
import com.vaadin.testbench.unit.mocks.SpringSecurityRequestCustomizer;

/**
 * Base JUnit 4 class for UI unit testing applications based on Spring
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
 * public class ViewTest extends SpringUIUnit4Test {
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
@RunWith(SpringRunner.class)
@TestExecutionListeners(listeners = UITestSpringLookupInitializer.class, mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
public abstract class SpringUIUnit4Test extends UIUnit4Test {

    static {
        UsageStatistics.markAsUsed("testbench/SpringUIUnit4Test",
                BaseUIUnitTest.testbenchVersion);
    }

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    protected Set<Class<?>> lookupServices() {
        return Set.of(UITestSpringLookupInitializer.class,
                SpringSecurityRequestCustomizer.class);
    }

    @Override
    public void initVaadinEnvironment() {
        scanTesters();
        MockSpringServlet servlet = new MockSpringServlet(discoverRoutes(),
                applicationContext, MockedUI::new);
        MockVaadin.setup(MockedUI::new, servlet, lookupServices());
    }

}
