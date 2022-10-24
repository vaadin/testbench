/**
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.flow.component.charts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.component.charts.model.Configuration;
import com.vaadin.flow.component.charts.model.DataProviderSeries;
import com.vaadin.flow.component.charts.model.DataSeries;
import com.vaadin.flow.component.charts.model.DataSeriesItem;
import com.vaadin.flow.component.charts.model.HeatSeries;
import com.vaadin.flow.component.charts.model.ListSeries;
import com.vaadin.flow.component.charts.model.TreeSeries;
import com.vaadin.flow.component.charts.model.TreeSeriesItem;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;

class ChartTesterSeriesValueTest {

    private ChartTester<Chart> tester = new ChartTester<>(new Chart());

    @Test
    void getValuesFromSeries_listSeries_getsValues() {
        ListSeries series = setupListSeries();
        Assertions.assertIterableEquals(tester.getValuesFromSeries(series),
                List.of(series.getData()));
    }

    @Test
    void getPointValue_listSeries_getsCorrectValue() {
        ListSeries series = setupListSeries();
        Assertions.assertEquals(series.getData()[0],
                tester.getPointValueFromSeries(series, "Jan"));
        Assertions.assertEquals(series.getData()[1],
                tester.getPointValueFromSeries(series, "Feb"));
        Assertions.assertEquals(series.getData()[2],
                tester.getPointValueFromSeries(series, "Mar"));
        Assertions.assertEquals(series.getData()[3],
                tester.getPointValueFromSeries(series, "Apr"));
        Assertions.assertEquals(series.getData()[4],
                tester.getPointValueFromSeries(series, "May"));
        Assertions.assertEquals(series.getData()[5],
                tester.getPointValueFromSeries(series, "Jun"));
        Assertions.assertEquals(series.getData()[6],
                tester.getPointValueFromSeries(series, "Jul"));
        Assertions.assertEquals(series.getData()[7],
                tester.getPointValueFromSeries(series, "Aug"));
        Assertions.assertEquals(series.getData()[8],
                tester.getPointValueFromSeries(series, "Sep"));
        Assertions.assertEquals(series.getData()[9],
                tester.getPointValueFromSeries(series, "Oct"));
        Assertions.assertEquals(series.getData()[10],
                tester.getPointValueFromSeries(series, "Nov"));
        Assertions.assertEquals(series.getData()[11],
                tester.getPointValueFromSeries(series, "Dec"));
    }

    @Test
    void getPointValue_listSeries_invalidCategoryThrows() {
        ListSeries series = setupListSeries();
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> tester.getPointValueFromSeries(series, "XYZ"));
        Assertions.assertTrue(exception.getMessage().contains("XYZ"));
        Assertions.assertTrue(
                exception.getMessage().contains("Invalid X-Axis category"));
    }

    @Test
    void getPointValue_listSeries_missingXAxisCategoriesThrows() {
        ListSeries series = new ListSeries("MyTest");
        IllegalStateException exception = Assertions.assertThrows(
                IllegalStateException.class,
                () -> tester.getPointValueFromSeries(series, "XYZ"));
        Assertions.assertTrue(exception.getMessage()
                .contains("X-Axis categories not configured"));
    }

    @Test
    void getValuesFromSeries_dataSeries_getsValues() {
        DataSeries series = createDataSeries();

        Assertions.assertIterableEquals(tester.getValuesFromSeries(series),
                series.getData().stream().map(DataSeriesItem::getY)
                        .collect(Collectors.toList()));
    }

    @Test
    void getPointValue_dataSeries_getsCorrectValue() {
        DataSeries series = createDataSeries();
        for (DataSeriesItem item : series.getData()) {
            Assertions.assertEquals(item.getY(),
                    tester.getPointValueFromSeries(series, item.getName()));
        }
    }

    @Test
    void getPointValue_dataSeries_notExistingThrows() {
        DataSeries series = createDataSeries();
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> tester.getPointValueFromSeries(series, "XYZ"));
        Assertions.assertTrue(exception.getMessage().contains("XYZ"));
        Assertions.assertTrue(
                exception.getMessage().contains("Invalid DataSeriesItem name"));
    }

    @Test
    void getValuesFromSeries_treeSeries_unsupported() {
        TreeSeries series = setupTreeSeries();

        UnsupportedOperationException exception = Assertions.assertThrows(
                UnsupportedOperationException.class,
                () -> tester.getValuesFromSeries(series));
        Assertions.assertTrue(exception.getMessage()
                .contains(TreeSeries.class.getSimpleName()));
        Assertions.assertTrue(
                exception.getMessage().contains("is not supported"));
    }

    @Test
    void getPointValue_treeSeries_unsupported() {
        TreeSeries series = setupTreeSeries();

        UnsupportedOperationException exception = Assertions.assertThrows(
                UnsupportedOperationException.class,
                () -> tester.getValuesFromSeries(series));
        Assertions.assertTrue(exception.getMessage()
                .contains(TreeSeries.class.getSimpleName()));
        Assertions.assertTrue(
                exception.getMessage().contains("is not supported"));
    }

    @SuppressWarnings("unchecked")
    @Test
    void getValuesFromSeries_dataProviderSeries_getsValues() {
        DataProviderSeries<Order> series = setupDataProviderSeries();
        Collection<Order> orders = ((ListDataProvider<Order>) series
                .getDataProvider()).getItems();

        Assertions.assertIterableEquals(tester.getValuesFromSeries(series),
                orders.stream().map(Order::getTotalPrice)
                        .collect(Collectors.toList()));
    }

    @Test
    void getPointValue_dataProviderSeries_getsCorrectValue() {
        DataProviderSeries<Order> series = setupDataProviderSeries();
        Collection<Order> orders = ((ListDataProvider<Order>) series
                .getDataProvider()).getItems();
        for (Order item : orders) {
            Assertions.assertEquals(item.getTotalPrice(), tester
                    .getPointValueFromSeries(series, item.getDescription()));
        }
    }

    @Test
    void getPointValue_dataProviderSeries_notExistingThrows() {
        DataProviderSeries<Order> series = setupDataProviderSeries();
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> tester.getPointValueFromSeries(series, "XYZ"));
        Assertions.assertTrue(exception.getMessage().contains("XYZ"));
        Assertions.assertTrue(
                exception.getMessage().contains("Invalid name for X-Axis"));
    }

    @Test
    void getPointValue_dataProviderSeries_missingXAxisAttributeThrows() {
        DataProviderSeries<Order> series = setupDataProviderSeries();
        series.setX(null);
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> tester.getPointValueFromSeries(series, "XYZ"));
        Assertions.assertTrue(exception.getMessage().contains("XYZ"));
        Assertions.assertTrue(
                exception.getMessage().contains("Invalid name for X-Axis"));
    }

    @Test
    void getPointValue_dataProviderSeries_duplicatedXAxisAttributeThrows() {
        DataProviderSeries<Order> series = setupDataProviderSeries();
        series.setX(order -> "XYZ");
        IllegalStateException exception = Assertions.assertThrows(
                IllegalStateException.class,
                () -> tester.getPointValueFromSeries(series, "XYZ"));
        Assertions.assertTrue(exception.getMessage().contains("XYZ"));
        Assertions.assertTrue(exception.getMessage()
                .contains("multiple items with same X-Axis name"));
    }

    @Test
    void getValuesFromSeries_heatSeries_unsupported() {
        HeatSeries series = setupHeatSeries();
        UnsupportedOperationException exception = Assertions.assertThrows(
                UnsupportedOperationException.class,
                () -> tester.getValuesFromSeries(series));
        Assertions.assertTrue(exception.getMessage()
                .contains(HeatSeries.class.getSimpleName()));
        Assertions.assertTrue(
                exception.getMessage().contains("is not supported"));
    }

    @Test
    void getPointValue_heatSeries_unsupported() {
        HeatSeries series = setupHeatSeries();

        UnsupportedOperationException exception = Assertions.assertThrows(
                UnsupportedOperationException.class,
                () -> tester.getValuesFromSeries(series));
        Assertions.assertTrue(exception.getMessage()
                .contains(HeatSeries.class.getSimpleName()));
        Assertions.assertTrue(
                exception.getMessage().contains("is not supported"));
    }

    private ListSeries setupListSeries() {
        ListSeries series = new ListSeries("Tokyo", 49.9, 71.5, 106.4, 129.2,
                144.0, 176.0, 135.6, 148.5, 216.4, 194.1, 95.6, 54.4);
        Configuration config = tester.getComponent().getConfiguration();
        config.addSeries(series);
        config.getxAxis().setCategories("Jan", "Feb", "Mar", "Apr", "May",
                "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
        return series;
    }

    private HeatSeries setupHeatSeries() {
        HeatSeries series = new HeatSeries("Sales per employee",
                new Number[][] { { 0, 0, 0 }, { 0, 1, 19 }, { 0, 2, 8 },
                        { 0, 3, 24 }, { 0, 4, 67 }, { 1, 0, 92 }, { 1, 1, 58 },
                        { 1, 2, 78 }, { 1, 3, 117 }, { 1, 4, 48 }, { 2, 0, 35 },
                        { 2, 1, 15 }, { 2, 2, 123 }, { 2, 3, 64 }, { 2, 4, 52 },
                        { 3, 0, 72 }, { 3, 1, 132 }, { 3, 2, 114 },
                        { 3, 3, 19 }, { 3, 4, 16 }, { 4, 0, 38 }, { 4, 1, 5 },
                        { 4, 2, 8 }, { 4, 3, 117 }, { 4, 4, 115 }, { 5, 0, 88 },
                        { 5, 1, 32 }, { 5, 2, 12 }, { 5, 3, 6 }, { 5, 4, 120 },
                        { 6, 0, 13 }, { 6, 1, 44 }, { 6, 2, 88 }, { 6, 3, 98 },
                        { 6, 4, 96 }, { 7, 0, 31 }, { 7, 1, 1 }, { 7, 2, 82 },
                        { 7, 3, 32 }, { 7, 4, 30 }, { 8, 0, 85 }, { 8, 1, 97 },
                        { 8, 2, 123 }, { 8, 3, 64 }, { 8, 4, 84 }, { 9, 0, 47 },
                        { 9, 1, 114 }, { 9, 2, 31 }, { 9, 3, 48 },
                        { 9, 4, 91 } });
        tester.getComponent().getConfiguration().addSeries(series);
        return series;
    }

    private TreeSeries setupTreeSeries() {
        TreeSeries series = new TreeSeries();
        TreeSeriesItem apples = new TreeSeriesItem("A", "Apples");
        apples.setColorIndex(0);
        TreeSeriesItem bananas = new TreeSeriesItem("B", "Bananas");
        bananas.setColorIndex(2);
        TreeSeriesItem oranges = new TreeSeriesItem("O", "Oranges");
        oranges.setColorIndex(3);
        TreeSeriesItem anneA = new TreeSeriesItem("Anne", apples, 5);
        TreeSeriesItem rickA = new TreeSeriesItem("Rick", apples, 3);
        TreeSeriesItem peterA = new TreeSeriesItem("Peter", apples, 4);
        TreeSeriesItem anneB = new TreeSeriesItem("Anne", bananas, 4);
        TreeSeriesItem rickB = new TreeSeriesItem("Rick", bananas, 10);
        TreeSeriesItem peterB = new TreeSeriesItem("Peter", bananas, 1);
        TreeSeriesItem anneO = new TreeSeriesItem("Anne", oranges, 1);
        TreeSeriesItem rickO = new TreeSeriesItem("Rick", oranges, 3);
        TreeSeriesItem peterO = new TreeSeriesItem("Peter", oranges, 3);
        TreeSeriesItem susanne = new TreeSeriesItem("Susanne", 2);
        susanne.setParent("Kiwi");
        susanne.setColorIndex(4);
        series.addAll(apples, bananas, oranges, anneA, rickA, peterA, anneB,
                rickB, peterB, anneO, rickO, peterO, susanne);
        tester.getComponent().getConfiguration().addSeries(series);
        return series;
    }

    private DataSeries createDataSeries() {
        DataSeries series = new DataSeries();
        series.add(new DataSeriesItem("Chrome", 61.41));
        series.add(new DataSeriesItem("Internet Explorer", 11.84));
        series.add(new DataSeriesItem("Firefox", 10.85));
        series.add(new DataSeriesItem("Edge", 4.67));
        series.add(new DataSeriesItem("Safari", 4.18));
        series.add(new DataSeriesItem("Sogou Explorer", 1.64));
        series.add(new DataSeriesItem("Opera", 6.2));
        series.add(new DataSeriesItem("QQ", 1.2));
        series.add(new DataSeriesItem("Others", 2.61));
        tester.getComponent().getConfiguration().addSeries(series);
        return series;
    }

    private DataProviderSeries<Order> setupDataProviderSeries() {
        List<Order> orders = new ArrayList<>();
        orders.add(new Order("Domain Name", 3, 7.99));
        orders.add(new Order("SSL Certificate", 1, 119.00));
        orders.add(new Order("Web Hosting", 1, 19.95));
        orders.add(new Order("Email Box", 20, 0.15));
        orders.add(new Order("E-Commerce Setup", 1, 25.00));
        orders.add(new Order("Technical Support", 1, 50.00));
        DataProvider<Order, ?> dataProvider = new ListDataProvider<>(orders);
        DataProviderSeries<Order> series = new DataProviderSeries<>(
                dataProvider, Order::getTotalPrice);
        series.setX(Order::getDescription);
        tester.getComponent().getConfiguration().addSeries(series);
        return series;
    }

    private static class Order {

        private final String description;
        private final int quantity;
        private final double unitPrice;

        public Order(String description, int quantity, double unitPrice) {
            this.description = description;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }

        public String getDescription() {
            return description;
        }

        public int getQuantity() {
            return quantity;
        }

        public double getUnitPrice() {
            return unitPrice;
        }

        public double getTotalPrice() {
            return unitPrice * quantity;
        }
    }
}
