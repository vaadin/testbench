/**
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.unit;

import java.util.Collections;

import com.example.SingleParam;
import com.example.TemplatedParam;
import com.example.base.HelloWorldView;
import com.example.base.WelcomeView;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.vaadin.testbench.unit.internal.MockRouteNotFoundError;

@ViewPackages(packages = "com.example")
public class UIUnitNavigationTest extends UIUnitTest {

    @Test
    public void getCurrentView_returnsExpectedView() {
        Assertions.assertTrue(getCurrentView() instanceof WelcomeView,
                "WelcomeView has the empty RouteAlias so it should be served as default view");

        HelloWorldView helloWorldView = navigate(HelloWorldView.class);

        Assertions.assertTrue(getCurrentView().equals(helloWorldView),
                "getCurrentView should return the same instance as gotten on navigation");
    }

    @Test
    public void navigationWithLocation_checksGeneratedViewType() {
        navigate("helloworld", HelloWorldView.class);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> navigate("welcome", HelloWorldView.class),
                "Navigation to path not returning given class should throw");

    }

    @Test
    public void navigationToParameterView_noParameterGiven_throwsException() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> navigate(SingleParam.class),
                "Illegal argument should be thrown for missing parameter");
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> navigate("param", SingleParam.class),
                "No matching route should be found for string without parameter");
        Assertions.assertTrue(exception.getMessage()
                .contains("Navigation resulted in unexpected class"));
        Assertions.assertTrue(exception.getMessage()
                .contains(MockRouteNotFoundError.class.getName()));
    }

    @Test
    public void navigationToParametrisedView_returnsInstantiatedView() {
        final String PARAMETER = "single";

        final SingleParam single = navigate(SingleParam.class, PARAMETER);
        Assertions.assertEquals(PARAMETER, single.parameter,
                "View should contain given parameter");

        final TemplatedParam param = navigate(TemplatedParam.class,
                Collections.singletonMap("param", PARAMETER));
        Assertions.assertEquals(PARAMETER, param.parameter,
                "Template parameter should be available.");
    }
}
