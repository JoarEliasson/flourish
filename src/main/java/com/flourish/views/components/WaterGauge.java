package com.flourish.views.components;

import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.Background;
import com.vaadin.flow.component.charts.model.BackgroundShape;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.Configuration;
import com.vaadin.flow.component.charts.model.DataLabels;
import com.vaadin.flow.component.charts.model.ListSeries;
import com.vaadin.flow.component.charts.model.Pane;
import com.vaadin.flow.component.charts.model.PlotOptionsSolidgauge;
import com.vaadin.flow.component.charts.model.Stop;
import com.vaadin.flow.component.charts.model.Title;
import com.vaadin.flow.component.charts.model.YAxis;
import com.vaadin.flow.component.charts.model.style.Style;
import com.vaadin.flow.component.charts.model.style.SolidColor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.theme.Theme;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Represents a dark-themed solid gauge for displaying a plant's water level.
 *
 * <p>Positive values (0..100) appear as a blue gauge oriented from bottom to top.
 * Negative values (-1..-100) appear as a red gauge from top to bottom.
 * All backgrounds are set to transparent so the gauge can blend with the
 * parent layout's dark background.</p>
 *
 * <p>Includes labels for last watering, next watering, and
 * days until or overdue. Relies on an external {@code WaterGauge.css} file
 * for container-level styling.</p>
 *
 * <p>Use {@link #setWaterLevel(double)} to set the numeric gauge,
 * and {@link #setWateringDates(LocalDateTime, LocalDateTime)} for date labels.</p>
 *
 * @author
 *   Joar Eliasson
 * @version
 *   1.1.0
 * @since
 *   2025-03-14
 */
public class WaterGauge extends Div {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final Chart chart;
    private final Configuration configuration;
    private final YAxis yAxis;
    private final ListSeries series;
    private final LabelGroup labelGroup;
    private double rawValue;

    /**
     * Constructs a new WaterGauge with transparent backgrounds and
     * light-colored text, suitable for a dark-inspired theme.
     */
    public WaterGauge() {
        addClassName("water-gauge");
        getStyle().set("background-color", "transparent");


        chart = new Chart(ChartType.SOLIDGAUGE);
        chart.getConfiguration().getChart().setBackgroundColor(new SolidColor(0, 0, 0, 0.0));
        chart.getConfiguration().getLegend().setBackgroundColor(new SolidColor(0, 0, 0, 0.0));
        configuration = chart.getConfiguration();

        Title title = new Title("Water Level");
        Style titleStyle = new Style();
        titleStyle.setColor(SolidColor.WHITE);
        title.setStyle(titleStyle);
        configuration.setTitle(title);
        configuration.getLegend().setBackgroundColor(new SolidColor(0, 0, 0, 0.0));

        Pane pane = configuration.getPane();
        pane.setSize("70%");
        pane.setCenter("50%", "50%");

        Background arcBackground = new Background();
        arcBackground.setBackgroundColor(new SolidColor(0, 0, 0, 0.0));
        arcBackground.setInnerRadius("60%");
        arcBackground.setOuterRadius("100%");
        arcBackground.setShape(BackgroundShape.ARC);
        pane.setBackground(arcBackground);

        yAxis = new YAxis();
        yAxis.setTickAmount(5);
        yAxis.setGridLineWidth(0);

        Style axisLabelStyle = new Style();
        axisLabelStyle.setColor(SolidColor.WHITE);
        yAxis.getLabels().setStyle(axisLabelStyle);
        configuration.addyAxis(yAxis);

        PlotOptionsSolidgauge plotOptions = new PlotOptionsSolidgauge();
        plotOptions.setAnimation(true);

        DataLabels dataLabels = new DataLabels();
        dataLabels.setFormat("{y}%");
        dataLabels.setY(-15);

        Style dataLabelStyle = new Style();
        dataLabelStyle.setColor(SolidColor.WHITE);
        dataLabels.setStyle(dataLabelStyle);

        plotOptions.setDataLabels(dataLabels);
        configuration.setPlotOptions(plotOptions);

        series = new ListSeries("Water Level", 0);
        configuration.addSeries(series);

        chart.setWidth("100%");
        chart.setHeight("220px");

        labelGroup = new LabelGroup();
        add(chart, labelGroup);
    }

    /**
     * Sets the gauge value. Positive values (0..100) appear blue;
     * negative (-1..-100) appear red. Values exceeding 100 are clamped.
     *
     * @param level the water level
     */
    public void setWaterLevel(double level) {
        rawValue = level;
        updateGauge();
    }

    /**
     * Sets the last and next watering times, calculating days until or overdue.
     *
     * @param lastWatered the last watering time
     * @param nextWatering the next scheduled watering time
     */
    public void setWateringDates(LocalDateTime lastWatered, LocalDateTime nextWatering) {
        labelGroup.setLastWatered(lastWatered);
        labelGroup.setNextWatering(nextWatering);
        if (lastWatered != null && nextWatering != null) {
            long daysLeft = ChronoUnit.DAYS.between(LocalDateTime.now(), nextWatering);
            labelGroup.setDaysUntil(daysLeft);
        } else {
            labelGroup.setDaysUntil(0);
        }
    }

    /**
     * Updates the gauge arc orientation, color stops, and displayed value.
     */
    private void updateGauge() {
        double absVal = Math.abs(rawValue);
        if (absVal > 100) {
            absVal = 100;
        }
        if (rawValue >= 0) {
            configuration.getPane().setStartAngle(-180);
            configuration.getPane().setEndAngle(0);
            yAxis.setMin(0);
            yAxis.setMax(100);
            yAxis.setStops(
                    new Stop(0.0f, new SolidColor("#A3D1FF")),
                    new Stop(1.0f, new SolidColor("#0055FF"))
            );
        } else {
            configuration.getPane().setStartAngle(180);
            configuration.getPane().setEndAngle(0);
            yAxis.setMin(0);
            yAxis.setMax(100);
            yAxis.setStops(
                    new Stop(0.0f, new SolidColor("#FFC0C0")),
                    new Stop(1.0f, new SolidColor("#FF0000"))
            );
        }
        series.setData(absVal);
        chart.drawChart();
    }

    /**
     * Holds textual labels for last watering, next watering, and days until or overdue.
     */
    private static class LabelGroup extends Div {

        private final Div lastWateredLabel = new Div();
        private final Div nextWateringLabel = new Div();
        private final Div daysUntilLabel = new Div();

        LabelGroup() {
            addClassName("water-gauge-label-group");
            add(lastWateredLabel, nextWateringLabel, daysUntilLabel);
        }

        void setLastWatered(LocalDateTime dt) {
            if (dt == null) {
                lastWateredLabel.setText("Last Watered: —");
            } else {
                lastWateredLabel.setText("Last Watered: " + dt.format(DATE_FORMAT));
            }
        }

        void setNextWatering(LocalDateTime dt) {
            if (dt == null) {
                nextWateringLabel.setText("Next Watering: —");
            } else {
                nextWateringLabel.setText("Next Watering: " + dt.format(DATE_FORMAT));
            }
        }

        void setDaysUntil(long daysLeft) {
            if (daysLeft < 0) {
                daysUntilLabel.setText("Overdue by " + Math.abs(daysLeft) + " day(s)!");
                daysUntilLabel.getStyle().set("color", "red");
            } else {
                daysUntilLabel.setText("Next watering in " + daysLeft + " day(s)");
                daysUntilLabel.getStyle().remove("color");
            }
        }
    }
}
