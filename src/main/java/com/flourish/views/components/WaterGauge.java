package com.flourish.views.components;

import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.charts.model.style.SolidColor;
import com.vaadin.flow.component.html.Div;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A custom WaterGauge that displays positive values (0..100) in a blue gauge oriented from
 * bottom (-180 deg) to top (0 deg), and negative values (0..100) in a red gauge oriented
 * from top (180 deg) to bottom (0 deg).
 *
 * <p>The actual numeric "water level" passed in can be negative or positive:
 * <ul>
 *   <li>If it's ≥ 0, we treat it as 0..100 in a "blue" gauge (freshly watered).</li>
 *   <li>If it's &lt; 0, we treat the absolute value in a "red" gauge (overdue).</li>
 * </ul>
 * <p>
 * Text for lastWatered, nextWatering, and days-left (or overdue days) is also displayer
 * at the bottom of the component for clarity.
 * </p>
 *
 * @author
 *   Joar Eliasson
 * @version
 *   1.1.0
 * @since
 *   2025-03-12
 */
public class WaterGauge extends Div {

    private static final long serialVersionUID = 1L;

    /**
     * Date/time format for lastWatered & nextWatering display.
     * Adjust as needed.
     */
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final Chart chart = new Chart(ChartType.SOLIDGAUGE);
    private final Configuration configuration;
    private final YAxis yAxis;
    private final ListSeries series;

    private double rawValue = 0.0;

    private final LabelGroup labelGroup = new LabelGroup();

    private static class LabelGroup extends Div {
        private final Div lastWateredLabel = new Div();
        private final Div nextWateringLabel = new Div();
        private final Div daysUntilLabel  = new Div();

        public LabelGroup() {
            getStyle()
                    .set("border-top", "1px solid #ddd")
                    .set("margin-top", "8px")
                    .set("padding-top", "8px");

            lastWateredLabel.getStyle().set("font-size", "0.9rem");
            nextWateringLabel.getStyle().set("font-size", "0.9rem");
            daysUntilLabel.getStyle().set("font-size", "0.9rem").set("font-weight", "bold");

            add(lastWateredLabel, nextWateringLabel, daysUntilLabel);
        }

        public void setLastWatered(LocalDateTime dt) {
            if (dt == null) {
                lastWateredLabel.setText("Last Watered: —");
            } else {
                lastWateredLabel.setText("Last Watered: " + dt.format(DATE_FORMAT));
            }
        }

        public void setNextWatering(LocalDateTime dt) {
            if (dt == null) {
                nextWateringLabel.setText("Next Watering: —");
            } else {
                nextWateringLabel.setText("Next Watering: " + dt.format(DATE_FORMAT));
            }
        }

        /**
         * @param daysLeft Positive means days remain, negative means overdue.
         */
        public void setDaysUntil(long daysLeft) {
            if (daysLeft < 0) {
                daysUntilLabel.setText("Overdue by " + Math.abs(daysLeft) + " day(s)!");
                daysUntilLabel.getStyle().set("color", "red");
            } else {
                daysUntilLabel.setText("Next watering in " + daysLeft + " day(s)");
                daysUntilLabel.getStyle().remove("color"); // revert to default
            }
        }
    }

    /**
     * Constructs a WaterGauge with default config. We alter the gauge
     * orientation, color stops, etc. at runtime based on the value's sign.
     */
    public WaterGauge() {
        setWidth("300px");
        setHeight(null);

        configuration = chart.getConfiguration();
        configuration.setTitle("Water Level");

        Pane pane = configuration.getPane();

        pane.setSize("70%");
        pane.setCenter("50%", "50%");
        Background bkg = new Background();
        bkg.setBackgroundColor(new SolidColor("#eeeeee"));
        bkg.setInnerRadius("60%");
        bkg.setOuterRadius("100%");
        bkg.setShape(BackgroundShape.ARC);
        pane.setBackground(bkg);

        yAxis = new YAxis();
        yAxis.setTickAmount(5);
        yAxis.setGridLineWidth(0);
        configuration.addyAxis(yAxis);

        PlotOptionsSolidgauge plotOpts = new PlotOptionsSolidgauge();
        plotOpts.setAnimation(true);
        DataLabels dataLabels = new DataLabels();
        dataLabels.setFormat("{y}%");
        dataLabels.setY(-15);
        plotOpts.setDataLabels(dataLabels);
        configuration.setPlotOptions(plotOpts);

        series = new ListSeries("Water Level", 0);
        configuration.addSeries(series);

        chart.setWidth("100%");
        chart.setHeight("220px");

        add(chart, labelGroup);
    }

    /**
     * Sets the raw water level. If >= 0, we consider that "just watered" territory
     * with a gauge from 0..100 in BLUE gradient, oriented from bottom to top.
     * If < 0, we consider that "overdue" territory with a gauge from 0..100 in a
     * RED gradient, oriented from top to bottom. The absolute value is used
     * as the fill percentage.
     *
     * @param level A double, e.g. +43 means 43% fresh. -25 means 25% overdue.
     */
    public void setWaterLevel(double level) {
        this.rawValue = level;
        updateGauge();
    }

    /**
     * Updates lastWatered, nextWatering, and calculates days until
     * (or overdue from) nextWatering to show the user.
     */
    public void setWateringDates(LocalDateTime lastWatered, LocalDateTime nextWatering) {
        labelGroup.setLastWatered(lastWatered);
        labelGroup.setNextWatering(nextWatering);

        if (lastWatered != null && nextWatering != null) {
            long daysLeft = java.time.temporal.ChronoUnit.DAYS.between(LocalDateTime.now(), nextWatering);
            labelGroup.setDaysUntil(daysLeft);
        } else {
            labelGroup.setDaysUntil(0);
        }
    }

    /**
     * Internal method to reconfigure the gauge based on rawValue's sign.
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
                    new Stop(0.0f, new SolidColor("#B3E6FF")),
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
}
