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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.charts.events.ChartClickEvent;
import com.vaadin.flow.component.charts.events.PointClickEvent;
import com.vaadin.flow.component.charts.events.SeriesClickEvent;
import com.vaadin.flow.component.charts.events.SeriesLegendItemClickEvent;
import com.vaadin.flow.component.charts.model.DataProviderSeries;
import com.vaadin.flow.component.charts.model.DataSeries;
import com.vaadin.flow.component.charts.model.DataSeriesItem;
import com.vaadin.flow.component.charts.model.ListSeries;
import com.vaadin.flow.component.charts.model.Series;
import com.vaadin.testbench.unit.ComponentTester;
import com.vaadin.testbench.unit.Tests;

/**
 * Tester for Chart components.
 *
 * @param <T>
 *            component type
 */
@Tests(Chart.class)
public class ChartTester<T extends Chart> extends ComponentTester<T> {
    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public ChartTester(T component) {
        super(component);
    }

    /**
     * Gets the values for the series at given index.
     *
     * At the moment only {@link ListSeries}, {@link DataSeries} and
     * {@link DataProviderSeries} are supported. For all other series types an
     * exception will be thrown.
     *
     * For {@link DataProviderSeries} Y value is expected to be a {@link Number}
     * subtype.
     *
     * @param seriesIndex
     *            zero-based index of the series
     * @return the list of values for the series.
     * @throws java.lang.IndexOutOfBoundsException
     *             if series for the given index does not exist.
     * @throws UnsupportedOperationException
     *             if the chart series at give index is not supported.
     */
    public List<Number> getSeriesValues(int seriesIndex) {
        ensureComponentIsUsable();
        Series series = getComponent().getConfiguration().getSeries()
                .get(seriesIndex);
        return getValuesFromSeries(series);
    }

    /**
     * Gets the values for the series with the given name.
     *
     * At the moment only {@link ListSeries}, {@link DataSeries} and
     * {@link DataProviderSeries} are supported. For all other series types an
     * exception will be thrown.
     *
     * For {@link DataProviderSeries} Y value is expected to be a {@link Number}
     * subtype.
     *
     * @param seriesName
     *            name of the series
     * @return the list of values for the series.
     * @throws UnsupportedOperationException
     *             if the chart series at give index is not supported.
     */
    public List<Number> getSeriesValues(String seriesName) {
        ensureComponentIsUsable();
        Objects.requireNonNull(seriesName, "series name must not be null");
        Series series = findSeriesByName(seriesName);
        return getValuesFromSeries(series);
    }

    /**
     * Gets the value of the point with given name from the series at given
     * index.
     *
     * The {@code name} depends on the series type. For {@link ListSeries} it is
     * expected to refer to an X-Axis category, for {@link DataSeries} it refers
     * to the item name, for {@link DataProviderSeries} it is matched against
     * the value of the configured {@literal x attribute}.
     *
     * At the moment only {@link ListSeries}, {@link DataSeries} and
     * {@link DataProviderSeries} are supported. For all other series types an
     * exception will be thrown.
     *
     * @param seriesIndex
     *            zero-based index of the series
     * @param name
     *            name of the point in the series
     * @return the value of the point at given coordinates.
     */
    public Number getPointValue(int seriesIndex, String name) {
        ensureComponentIsUsable();
        Series series = getComponent().getConfiguration().getSeries()
                .get(seriesIndex);
        return getPointValueFromSeries(series, name);
    }

    /**
     * Gets the value of the point with given name from the series at given
     * index.
     *
     * The {@code name} depends on the series type. For {@link ListSeries} it is
     * expected to refer to an X-Axis category, for {@link DataSeries} it refers
     * to the item name, for {@link DataProviderSeries} it is matched against
     * the value of the configured {@literal x attribute}.
     *
     * At the moment only {@link ListSeries}, {@link DataSeries} and
     * {@link DataProviderSeries} are supported. For all other series types an
     * exception will be thrown.
     *
     * @param seriesName
     *            name of the series
     * @param name
     *            name of the point in the series
     * @return the value of the point at given coordinates.
     */
    public Number getPointValue(String seriesName, String name) {
        ensureComponentIsUsable();
        Series series = findSeriesByName(seriesName);
        return getPointValueFromSeries(series, name);
    }

    /**
     * Simulates a click on the item of the legend with the given text.
     *
     * Current limitation: it assumes the text corresponds to a series name.
     *
     * @param item
     *            legend item
     */
    public void clickLegendItem(String item) {
        ensureComponentIsUsable();
        Series series = findSeriesByName(item);
        T component = getComponent();
        int seriesIndex = component.getConfiguration().getSeries()
                .indexOf(series);
        ComponentUtil.fireEvent(component,
                new SeriesLegendItemClickEvent(component, true, 0, 0, false,
                        false, false, false, 0, seriesIndex));
    }

    /**
     * Simulates a click on a point on the chart.
     *
     * @param seriesName
     *            name of the series
     * @param name
     *            name of the point in the series
     */
    public void clickPoint(String seriesName, String name) {
        ensureComponentIsUsable();
        T component = getComponent();
        Series series = findSeriesByName(seriesName);
        int seriesIndex = component.getConfiguration().getSeries()
                .indexOf(series);
        Point point = getPointFromSeries(series, name);
        ComponentUtil.fireEvent(component, new SeriesClickEvent(component, true,
                0, 0, false, false, false, false, 0, 0.0, 0.0, seriesIndex));

        String category = Stream
                .of(getComponent().getConfiguration().getxAxis()
                        .getCategories())
                .filter(name::equals).findFirst().orElse(null);

        ComponentUtil.fireEvent(component,
                new PointClickEvent(component, true, 0, 0, false, false, false,
                        false, 0, 0.0, 0.0, seriesIndex, category, point.index,
                        point.id));
    }

    /**
     * Simulates a click on the chart, but not on legend items or points.
     */
    public void clickChart() {
        ensureComponentIsUsable();
        T component = getComponent();
        ComponentUtil.fireEvent(component, new ChartClickEvent(component, true,
                0.0, 0.0, 0, 0, false, false, false, false, 0));

    }

    private Series findSeriesByName(String seriesName) {
        return getComponent().getConfiguration().getSeries().stream()
                .filter(s -> seriesName.equals(s.getName()))
                .reduce((s1, s2) -> {
                    throw new IllegalStateException(
                            "Multiple series found with name " + seriesName);
                }).orElseThrow(() -> new IllegalArgumentException(
                        "Series " + seriesName + " does not exists"));
    }

    // Visible for testing
    List<Number> getValuesFromSeries(Series series) {
        if (series instanceof ListSeries) {
            return new ArrayList<>(List.of(((ListSeries) series).getData()));
        } else if (series instanceof DataSeries) {
            return ((DataSeries) series).getData().stream()
                    .map(DataSeriesItem::getY).collect(Collectors.toList());
        } else if (series instanceof DataProviderSeries) {
            return ((DataProviderSeries<?>) series).getValues().stream()
                    .map(map -> map
                            .getOrDefault(DataProviderSeries.Y_ATTRIBUTE,
                                    Optional.empty())
                            .map(Number.class::cast).orElseThrow())
                    .collect(Collectors.toList());
        }
        throw new UnsupportedOperationException(
                "Getting values from series of type "
                        + series.getClass().getName() + " is not supported");
    }

    // Visible for testing
    Number getPointValueFromSeries(Series series, String name) {
        return getPointFromSeries(series, name).value;
    }

    private Point getPointFromSeries(Series series, String name) {
        if (series instanceof ListSeries) {
            List<String> categories = List.of(getComponent().getConfiguration()
                    .getxAxis().getCategories());
            if (categories.isEmpty()) {
                throw new IllegalStateException(
                        "X-Axis categories not configured");
            }
            int index = categories.indexOf(name);
            if (index == -1) {
                throw new IllegalArgumentException("Invalid X-Axis category "
                        + name + ". " + "Existing categories are "
                        + String.join(", ", categories));
            }
            if (categories.lastIndexOf(name) != index) {
                throw new IllegalStateException(
                        "Found multiple items with same X-Axis name: " + name);
            }
            return new Point(String.format("highcharts-%s-%s", name, index),
                    index, ((ListSeries) series).getData()[index]);
        } else if (series instanceof DataSeries) {
            DataSeries cast = (DataSeries) series;
            DataSeriesItem item = cast.get(name);
            if (item == null) {
                String names = cast.getData().stream()
                        .map(DataSeriesItem::getName)
                        .collect(Collectors.joining(", "));
                throw new IllegalArgumentException(
                        "Invalid DataSeriesItem name " + name + ". "
                                + "Existing names are " + names);
            }
            int pointIndex = cast.getData().indexOf(item);
            String pointId = cast.getId();
            if (pointId == null) {
                pointId = String.format("highcharts-%s-%s", name, pointIndex);
            }
            return new Point(pointId, pointIndex, item.getY());
        } else if (series instanceof DataProviderSeries) {
            List<Map<String, Optional<Object>>> allValues = ((DataProviderSeries<?>) series)
                    .getValues();
            List<Map<String, Optional<Object>>> pointAttributes = allValues
                    .stream()
                    .filter(map -> name.equals(map
                            .get(DataProviderSeries.X_ATTRIBUTE).orElse(null)))
                    .collect(Collectors.toList());
            if (pointAttributes.isEmpty()) {
                throw new IllegalArgumentException(
                        "Cannot find point. Invalid name for X-Axis: " + name);
            } else if (pointAttributes.size() > 1) {
                throw new IllegalStateException(
                        "Found multiple items with same X-Axis name: " + name);
            }
            Map<String, Optional<Object>> map = pointAttributes.get(0);
            Number value = map.get(DataProviderSeries.Y_ATTRIBUTE)
                    .map(Number.class::cast).orElse(null);
            int pointIndex = allValues.indexOf(map);
            return new Point(
                    String.format("highcharts-%s-%s", name, pointIndex),
                    pointIndex, value);
        }
        throw new UnsupportedOperationException(
                "Getting values from series of type "
                        + series.getClass().getName() + " is not supported");
    }

    private static class Point {
        private String id;
        private final int index;
        private final Number value;

        Point(String id, int index, Number value) {
            this.id = id;
            this.index = index;
            this.value = value;
        }
    }
}
