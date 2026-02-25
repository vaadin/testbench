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
package com.vaadin.flow.component.charts;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.browserless.BrowserlessTest;
import com.vaadin.browserless.CommercialTesterWrappers;
import com.vaadin.browserless.ViewPackages;
import com.vaadin.flow.component.charts.model.ListSeries;
import com.vaadin.flow.component.charts.model.Series;
import com.vaadin.flow.router.RouteConfiguration;

@ViewPackages
class ChartTesterTest extends BrowserlessTest
        implements CommercialTesterWrappers {

    ColumnChartView view;

    @BeforeEach
    public void registerView() {
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(ColumnChartView.class);
        view = navigate(ColumnChartView.class);
    }

    @Test
    void getSeriesValues_notUsable_throws() {
        view.chart.setVisible(false);
        ChartTester<Chart> chartTester = test(view.chart);
        IllegalStateException exception = Assertions.assertThrows(
                IllegalStateException.class,
                () -> chartTester.getSeriesValues(0));
        Assertions.assertTrue(exception.getMessage().contains("not usable"));
        exception = Assertions.assertThrows(IllegalStateException.class,
                () -> chartTester.getSeriesValues("Berlin"));
        Assertions.assertTrue(exception.getMessage().contains("not usable"));
    }

    @Test
    void getSeriesValues_byIndex_getsSeriesValues() {
        Assertions.assertIterableEquals(test(view.chart).getSeriesValues(0),
                List.of(view.tokyo.getData()));
        Assertions.assertIterableEquals(test(view.chart).getSeriesValues(1),
                List.of(view.newYork.getData()));
        Assertions.assertIterableEquals(test(view.chart).getSeriesValues(2),
                List.of(view.london.getData()));
        Assertions.assertIterableEquals(test(view.chart).getSeriesValues(3),
                List.of(view.berlin.getData()));
    }

    @Test
    void getSeriesValues_byName_getsSeriesValues() {
        Assertions.assertIterableEquals(
                test(view.chart).getSeriesValues("Tokyo"),
                List.of(view.tokyo.getData()));
        Assertions.assertIterableEquals(
                test(view.chart).getSeriesValues("New York"),
                List.of(view.newYork.getData()));
        Assertions.assertIterableEquals(
                test(view.chart).getSeriesValues("London"),
                List.of(view.london.getData()));
        Assertions.assertIterableEquals(
                test(view.chart).getSeriesValues("Berlin"),
                List.of(view.berlin.getData()));
    }

    @Test
    void getSeriesValues_invalidIndex_throws() {
        ChartTester<Chart> chartTester = test(view.chart);
        Assertions.assertThrows(IndexOutOfBoundsException.class,
                () -> chartTester.getSeriesValues(-1));
        Assertions.assertThrows(IndexOutOfBoundsException.class,
                () -> chartTester.getSeriesValues(5));
    }

    @Test
    void getSeriesValues_notExistingSeries_throws() {
        ChartTester<Chart> chartTester = test(view.chart);
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> chartTester.getSeriesValues("Rome"));
        Assertions.assertTrue(exception.getMessage().contains("Rome"));
        Assertions
                .assertTrue(exception.getMessage().contains("does not exists"));
    }

    @Test
    void getSeriesValues_duplicatedSeries_throws() {
        String seriesName = view.tokyo.getName();
        ListSeries duplicated = new ListSeries(seriesName,
                view.tokyo.getData());
        view.chart.getConfiguration().addSeries(duplicated);
        ChartTester<Chart> chartTester = test(view.chart);
        IllegalStateException exception = Assertions.assertThrows(
                IllegalStateException.class,
                () -> chartTester.getSeriesValues(seriesName));
        Assertions.assertTrue(exception.getMessage().contains(seriesName));
        Assertions
                .assertTrue(exception.getMessage().contains("Multiple series"));
    }

    @Test
    void getPointValue_byIndex_getsPointValue() {
        for (int i = 0; i < view.categories.length; i++) {
            Assertions.assertEquals(
                    test(view.chart).getPointValue(2, view.categories[i]),
                    view.london.getData()[i],
                    "Point " + view.categories[i] + " from series " + i);
        }
    }

    @Test
    void getPointValue_byName_getsPointValue() {
        for (int i = 0; i < view.categories.length; i++) {
            Assertions
                    .assertEquals(
                            test(view.chart).getPointValue(
                                    view.london.getName(), view.categories[i]),
                            view.london.getData()[i]);
        }
    }

    @Test
    void getPointValue_notExistingItemName_throws() {
        ChartTester<Chart> chartTester = test(view.chart);
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> chartTester.getPointValue("Berlin", "XYZ"));
        Assertions.assertTrue(exception.getMessage().contains("XYZ"));
        Assertions.assertTrue(exception.getMessage().contains("Invalid"));
        exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> chartTester.getPointValue(0, "XYZ"));
        Assertions.assertTrue(exception.getMessage().contains("XYZ"));
        Assertions.assertTrue(exception.getMessage().contains("Invalid"));
    }

    @Test
    void getPointValue_notUsable_throws() {
        view.chart.setVisible(false);
        ChartTester<Chart> chartTester = test(view.chart);
        IllegalStateException exception = Assertions.assertThrows(
                IllegalStateException.class,
                () -> chartTester.getPointValue(0, "Jan"));
        Assertions.assertTrue(exception.getMessage().contains("not usable"));
        exception = Assertions.assertThrows(IllegalStateException.class,
                () -> chartTester.getPointValue("Berlin", "Jan"));
        Assertions.assertTrue(exception.getMessage().contains("not usable"));
    }

    @Test
    void getPointValue_duplicatedName_throws() {
        view.categories[2] = "Feb";
        view.chart.getConfiguration().getxAxis().setCategories(view.categories);

        ChartTester<Chart> chartTester = test(view.chart);
        IllegalStateException exception = Assertions.assertThrows(
                IllegalStateException.class,
                () -> chartTester.getPointValue(0, "Feb"));
        Assertions.assertTrue(exception.getMessage().contains("Feb"));
        Assertions.assertTrue(exception.getMessage().contains("multiple"));
        exception = Assertions.assertThrows(IllegalStateException.class,
                () -> chartTester.getPointValue("Berlin", "Feb"));
        Assertions.assertTrue(exception.getMessage().contains("Feb"));
        Assertions.assertTrue(exception.getMessage().contains("multiple"));
    }

    @Test
    void clickLegendItem_notUsable_throws() {
        view.chart.setVisible(false);
        ChartTester<Chart> chartTester = test(view.chart);
        IllegalStateException exception = Assertions.assertThrows(
                IllegalStateException.class,
                () -> chartTester.clickLegendItem("Berlin"));
        Assertions.assertTrue(exception.getMessage().contains("not usable"));
    }

    @Test
    void clickLegendItem_notExisting_throws() {
        ChartTester<Chart> chartTester = test(view.chart);
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> chartTester.clickLegendItem("Rome"));
        Assertions.assertTrue(exception.getMessage().contains("Rome"));
        Assertions
                .assertTrue(exception.getMessage().contains("does not exist"));
    }

    @Test
    void clickLegendItem_eventFired() {
        // SeriesLegendItemClickEvent
        // PointLegendItemClickEvent ???
        AtomicReference<Series> seriesLegend = new AtomicReference<>();
        view.chart.addSeriesLegendItemClickListener(
                ev -> seriesLegend.set(ev.getSeries()));

        test(view.chart).clickLegendItem("Berlin");
        Assertions.assertSame(view.berlin, seriesLegend.get());
    }

    @Test
    void clickPoint_notUsable_throws() {
        view.chart.setVisible(false);
        ChartTester<Chart> chartTester = test(view.chart);
        IllegalStateException exception = Assertions.assertThrows(
                IllegalStateException.class,
                () -> chartTester.clickPoint("Berlin", "Feb"));
        Assertions.assertTrue(exception.getMessage().contains("not usable"));
    }

    @Test
    void clickPoint_notExisting_throws() {
        ChartTester<Chart> chartTester = test(view.chart);
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> chartTester.clickPoint("Rome", "Feb"));
        Assertions.assertTrue(exception.getMessage().contains("Rome"));
        Assertions
                .assertTrue(exception.getMessage().contains("does not exist"));

        exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> chartTester.clickPoint("London", "XYZ"));
        Assertions.assertTrue(exception.getMessage().contains("XYZ"));
        Assertions.assertTrue(exception.getMessage().contains("Invalid"));
    }

    @Test
    void clickPoint_eventsFired() {
        // SeriesClick
        // PointClick
        AtomicReference<Series> series = new AtomicReference<>();
        AtomicReference<Series> pointSeries = new AtomicReference<>();
        AtomicInteger point = new AtomicInteger();
        view.chart.addSeriesClickListener(ev -> series.set(ev.getSeries()));
        view.chart.addPointClickListener(ev -> {
            pointSeries.set(ev.getSeries());
            point.set(ev.getItemIndex());
        });

        test(view.chart).clickPoint("Berlin", "Feb");
        Assertions.assertSame(view.berlin, series.get());
        Assertions.assertSame(view.berlin, pointSeries.get());
        Assertions.assertEquals(1, point.get());
    }

    @Test
    void clickChart_notUsable_throws() {
        view.chart.setVisible(false);
        ChartTester<Chart> chartTester = test(view.chart);
        IllegalStateException exception = Assertions.assertThrows(
                IllegalStateException.class, chartTester::clickChart);
        Assertions.assertTrue(exception.getMessage().contains("not usable"));
    }

    @Test
    void clickChart_eventFired() {
        AtomicBoolean chartClicked = new AtomicBoolean();
        view.chart.addChartClickListener(ev -> chartClicked.set(true));

        test(view.chart).clickChart();
        Assertions.assertTrue(chartClicked.get());
    }

}
