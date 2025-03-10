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
import com.vaadin.flow.component.charts.model.YAxis;
import com.vaadin.flow.component.charts.model.style.SolidColor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A solid gauge component for displaying a plant's water level, with
 * improved color stops for negative (overdue) values and the ability
 * to display extra watering-related info in the subtitle.
 *
 * <ul>
 *   <li>Range: [-100 ... 120]</li>
 *   <li>Color-coded from red (overdue) to green/blue (recently watered).</li>
 *   <li>Method to set the gauge value, plus an optional subtitle for watering frequency & dates.</li>
 * </ul>
 *
 * <p>Usage Example:</p>
 * <pre>{@code
 * WaterGauge gauge = new WaterGauge();
 * gauge.setWaterLevel(75);  // updates the gauge
 * gauge.setWateringDetails(7, lastWatered, nextWatering); // optional subtitle
 * }</pre>
 *
 * @author
 *
 * @version
 *
 * @since
 *   2025-03-15
 */
public class WaterGauge extends Chart {

    private final Configuration configuration;
    private final YAxis yAxis;
    private final ListSeries series;

    /**
     * Constructs a new WaterGauge with extended color stops and a range of [-100 ... 120].
     */
    public WaterGauge() {
        super(ChartType.SOLIDGAUGE);
        configuration = getConfiguration();
        configuration.setTitle("Water Level");

        // Optional: place a short instructions text beneath the main title
        configuration.setSubTitle("");  // initially empty; can be set later

        // Setup the gauge "pane"
        Pane pane = configuration.getPane();
        pane.setSize("125%");
        pane.setCenter("50%", "70%");
        pane.setStartAngle(-90);
        pane.setEndAngle(90);

        Background bkg = new Background();
        bkg.setBackgroundColor(new SolidColor("#eeeeee"));
        bkg.setInnerRadius("60%");
        bkg.setOuterRadius("100%");
        bkg.setShape(BackgroundShape.ARC);
        pane.setBackground(bkg);

        // Y-axis from -100 (overdue) to 120 (freshly watered)
        yAxis = new YAxis();
        yAxis.setMin(-100);
        yAxis.setMax(120);
        yAxis.setTickInterval(100);
        yAxis.getLabels().setY(-16);
        yAxis.setGridLineWidth(0);

        // Multi-stop gradient for negative => positive => above 100
        //  0.0 maps to -100, 1.0 maps to 120
        yAxis.setStops(
                new Stop(0.00f, new SolidColor("#e74c3c")), // at -100 => red
                new Stop(0.40f, new SolidColor("#f39c12")), // around -20 => orange
                new Stop(0.60f, new SolidColor("#f1c40f")), // around 10 => yellow
                new Stop(0.90f, new SolidColor("#27ae60")), // around 80 => green
                new Stop(1.00f, new SolidColor("#3498db"))  // at 120 => blue
        );
        configuration.addyAxis(yAxis);

        // Data labels near the arc
        PlotOptionsSolidgauge plotOptions = new PlotOptionsSolidgauge();
        DataLabels dataLabels = new DataLabels();
        dataLabels.setY(-20);
        plotOptions.setDataLabels(dataLabels);

        // Enable basic animation for transitions
        plotOptions.setAnimation(true);

        configuration.setPlotOptions(plotOptions);

        // Single data series to represent water level
        series = new ListSeries("Water Level", 0);
        configuration.addSeries(series);

        // Draw the chart initially at 0%
        drawChart();
    }

    /**
     * Sets the current water level value on the gauge.
     *
     * <p>The value should be in the range [-100, 120]:
     * <ul>
     *   <li>100 means the plant is at the ideal watering threshold.</li>
     *   <li>Values > 100 can represent just-watered or extremely recent watering.</li>
     *   <li>0 means watering is due now.</li>
     *   <li>-100 means overdue by one full watering interval.</li>
     * </ul>
     * </p>
     *
     * @param value the water level value.
     */
    public void setWaterLevel(double value) {
        if (value > 120) {
            value = 120;
        } else if (value < -100) {
            value = -100;
        }
        series.setData(value);
        drawChart();
    }

    /**
     * Optionally sets a subtitle that displays watering details, e.g. frequency in days,
     * last watered, and next watering date/time.
     *
     * @param freqDays   the watering frequency (days).
     * @param last       the last time the plant was watered.
     * @param next       the next scheduled watering time.
     */
    public void setWateringDetails(int freqDays, LocalDateTime last, LocalDateTime next) {
        String format = "yyyy-MM-dd HH:mm";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(format);

        String subtitle = String.format("Frequency: %d days | Last: %s | Next: %s",
                freqDays,
                last.format(dtf),
                next.format(dtf));

        configuration.setSubTitle(subtitle);
        drawChart();
    }
}
