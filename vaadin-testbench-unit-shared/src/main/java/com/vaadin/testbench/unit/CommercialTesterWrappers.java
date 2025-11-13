/**
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.unit;

import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.ChartTester;

/**
 * Provides factory method to create testers for commercial components.
 */
@SuppressWarnings("unchecked")
public interface CommercialTesterWrappers {

    /**
     * Create a tester for the given Chart instance.
     *
     * @param chart
     *            the chart instance to be tested
     * @return a ChartTester instance wrapping the given chart
     */
    default ChartTester<Chart> test(Chart chart) {
        return BaseUIUnitTest.internalWrap(ChartTester.class, chart);
    }

}
