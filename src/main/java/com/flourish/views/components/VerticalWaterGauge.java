package com.flourish.views.components;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.Configuration;
import com.vaadin.flow.component.charts.model.DataLabels;
import com.vaadin.flow.component.charts.model.ListSeries;
import com.vaadin.flow.component.charts.model.PlotOptionsColumn;
import com.vaadin.flow.component.charts.model.YAxis;
import com.vaadin.flow.component.charts.model.style.SolidColor;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * <Strong>Please note that this class is not currently used!</Strong>
 * <em>It is kept for illustrative purposes as an alternative to the current WaterGauge</em>
 * <p>A vertical bar gauge component for displaying a plant's water level.</p>
 *
 * <p>Uses Vaadin Charts with a single-column chart to represent
 * the watering status. Range is [-100, 120] (similar to the
 * original SOLIDGAUGE version). The bar animates between values
 * when calling {@link #setWaterLevel(double)}.</p>
 *
 * <p>This component also shows two small labels for:
 * <ul>
 *   <li>Last Watered</li>
 *   <li>Next Watering</li>
 * </ul>
 * for the user to see relevant timestamps at a glance.</p>
 *
 * @author
 *   Joar Eliasson
 * @version
 *   1.1.0
 * @since
 *   2025-03-12
 */
@CssImport(value = "./styles/vertical-water-gauge.css", themeFor = "vaadin-chart")
public class VerticalWaterGauge extends Div {

    private static final long serialVersionUID = 1L;

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final Span lastWateredSpan = new Span();
    private final Span nextWateringSpan = new Span();

    private final Chart chart = new Chart(ChartType.COLUMN);
    private final ListSeries series = new ListSeries("Water Level");

    private double currentValue = 0.0;

    /**
     * Constructs a new VerticalWaterGauge.
     */
    public VerticalWaterGauge() {
        setWidth("100");
        setHeight(null);
        getStyle().set("border", "1px solid #ddd")
                .set("border-radius", "8px")
                .set("padding", "8px");

        lastWateredSpan.getStyle().set("display", "block");
        nextWateringSpan.getStyle().set("display", "block");

        Configuration config = chart.getConfiguration();
        config.setTitle("Water Level");

        config.getxAxis().setVisible(false);
        config.getxAxis().setTickLength(0);

        YAxis yAxis = config.getyAxis();
        yAxis.setTitle("");
        yAxis.setMin(-100);
        yAxis.setMax(120);
        yAxis.setTickInterval(50);
        yAxis.setGridLineWidth(0);

        PlotOptionsColumn columnOptions = new PlotOptionsColumn();
        columnOptions.setAnimation(true);
        columnOptions.setBorderRadius(5);

        columnOptions.setColor(SolidColor.LIGHTBLUE);

        DataLabels dataLabels = new DataLabels();
        dataLabels.setEnabled(true);
        dataLabels.setFormat("{y}%");
        dataLabels.setInside(true);
        dataLabels.setStyle(new com.vaadin.flow.component.charts.model.style.Style());
        dataLabels.getStyle().setColor(SolidColor.BLACK);
        columnOptions.setDataLabels(dataLabels);

        config.getTooltip().setEnabled(true);
        config.getTooltip().setPointFormat("{series.name}: {point.y}%");

        series.setPlotOptions(columnOptions);

        series.addData(0);
        config.addSeries(series);

        chart.setWidth(100, Unit.PERCENTAGE);
        chart.setHeight("400px");

        add(lastWateredSpan, nextWateringSpan, chart);
    }

    /**
     * Updates the vertical bar gauge with a new water level value.
     *
     * <p>Values above 120 or below -100 are clamped, just like the
     * SOLIDGAUGE version. This updates the single data point
     * and triggers chart re-render.</p>
     *
     * @param newValue The new gauge value (–100..120).
     */
    public void setWaterLevel(double newValue) {
        if (newValue > 120) {
            newValue = 120;
        } else if (newValue < -100) {
            newValue = -100;
        }
        this.currentValue = newValue;
        series.setData(newValue);
        chart.drawChart();
    }

    /**
     * Sets the "last watered" timestamp, displayed above the chart.
     *
     * @param lastWatered The LocalDateTime the plant was last watered.
     */
    public void setLastWatered(LocalDateTime lastWatered) {
        if (lastWatered == null) {
            lastWateredSpan.setText("Last Watered: (unknown)");
        } else {
            lastWateredSpan.setText("Last Watered: " + lastWatered.format(FORMAT));
        }
    }

    /**
     * Sets the "next watering" timestamp, displayed above the chart.
     *
     * @param nextWatering The LocalDateTime when the plant is next due for watering.
     */
    public void setNextWatering(LocalDateTime nextWatering) {
        if (nextWatering == null) {
            nextWateringSpan.setText("Next Watering: (unknown)");
        } else {
            nextWateringSpan.setText("Next Watering: " + nextWatering.format(FORMAT));
        }
    }

}
