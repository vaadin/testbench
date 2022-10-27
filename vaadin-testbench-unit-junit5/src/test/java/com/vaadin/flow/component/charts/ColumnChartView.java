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

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.Configuration;
import com.vaadin.flow.component.charts.model.Crosshair;
import com.vaadin.flow.component.charts.model.ListSeries;
import com.vaadin.flow.component.charts.model.Tooltip;
import com.vaadin.flow.component.charts.model.XAxis;
import com.vaadin.flow.component.charts.model.YAxis;
import com.vaadin.flow.router.Route;

@Tag("div")
@Route(value = "column-chart", registerAtStartup = false)
public class ColumnChartView extends Component implements HasComponents {

    final Chart chart;
    final ListSeries tokyo = new ListSeries("Tokyo", 49.9, 71.5, 106.4, 129.2,
            144.0, 176.0, 135.6, 148.5, 216.4, 194.1, 95.6, 54.4);
    final ListSeries newYork = new ListSeries("New York", 83.6, 78.8, 98.5,
            93.4, 106.0, 84.5, 105.0, 104.3, 91.2, 83.5, 106.6, 92.3);
    final ListSeries london = new ListSeries("London", 48.9, 38.8, 39.3, 41.4,
            47.0, 48.3, 59.0, 59.6, 52.4, 65.2, 59.3, 51.2);
    final ListSeries berlin = new ListSeries("Berlin", 42.4, 33.2, 34.5, 39.7,
            52.6, 75.5, 57.4, 60.4, 47.6, 39.1, 46.8, 51.1);
    final String[] categories = new String[] { "Jan", "Feb", "Mar", "Apr",
            "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

    public ColumnChartView() {
        chart = new Chart();

        Configuration configuration = chart.getConfiguration();
        configuration.setTitle("Monthly Average Rainfall");
        configuration.setSubTitle("Source: WorldClimate.com");
        chart.getConfiguration().getChart().setType(ChartType.COLUMN);

        configuration.addSeries(tokyo);
        configuration.addSeries(newYork);
        configuration.addSeries(london);
        configuration.addSeries(berlin);

        XAxis x = new XAxis();
        x.setCrosshair(new Crosshair());
        x.setCategories(categories);
        configuration.addxAxis(x);

        YAxis y = new YAxis();
        y.setMin(0);
        y.setTitle("Rainfall (mm)");
        configuration.addyAxis(y);

        Tooltip tooltip = new Tooltip();
        tooltip.setShared(true);
        configuration.setTooltip(tooltip);

        add(chart);
    }

}
