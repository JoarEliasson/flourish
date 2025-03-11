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

/**
 * An enhanced gauge component for displaying a plant's water level.
 *
 * <p>This class uses Vaadin Charts' SOLIDGAUGE to visualize the "health" or "freshness"
 * of the plant's watering status. The range is expanded to [-100, 120], where:
 * <ul>
 *   <li>-100 means the plant is overdue by a full interval (red zone).</li>
 *   <li>0 means the plant is exactly due for watering (yellow zone).</li>
 *   <li>100 means the plant was just watered (green zone).</li>
 *   <li>Values above 100 move toward a bluish color, indicating "extra freshly watered."</li>
 * </ul>
 *
 * <p>Basic chart animations are enabled. To update the gauge, call {@link #setWaterLevel(double)}.
 * Values are clamped between -100 and 120.</p>
 *
 * @author
 *
 * @version
 *
 * @since
 *   2025-03-12
 */
public class WaterGauge extends Chart {

    private static final long serialVersionUID = 1L;

    private final Configuration configuration;
    private final YAxis yAxis;
    private final ListSeries series;

    /**
     * Constructs a new WaterGauge with expanded range, color gradients, and animations.
     */
    public WaterGauge() {
        super(ChartType.SOLIDGAUGE);

        // Get and configure the chart's main Configuration object
        configuration = getConfiguration();
        configuration.setTitle("Water Level");

        // Configure the circular "pane" area
        Pane pane = configuration.getPane();
        pane.setSize("125%");
        pane.setCenter("50%", "70%"); // Center horizontally at 50%, but push down 70%
        pane.setStartAngle(-90);
        pane.setEndAngle(90);

        // Create a background shape (arc in this case)
        Background background = new Background();
        background.setBackgroundColor(new SolidColor("#eeeeee"));
        background.setInnerRadius("60%");
        background.setOuterRadius("100%");
        background.setShape(BackgroundShape.ARC);
        pane.setBackground(background);

        // Configure the Y-axis to go from -100 to 120
        yAxis = new YAxis();
        yAxis.setMin(-100);
        yAxis.setMax(120);
        yAxis.setTickInterval(50);
        yAxis.getLabels().setY(-16); // position the labels a bit higher
        yAxis.setGridLineWidth(0);   // remove radial grid lines

        /*
         * The color stops gradient is mapped from the axis minimum (-100) to maximum (120).
         *  - stop(0.0) => -100
         *  - stop(0.3) => -40
         *  - stop(0.5) =>   0
         *  - stop(0.83) => 80
         *  - stop(1.0) => 120
         */
        yAxis.setStops(
                new Stop(0.0f, new SolidColor("#e74c3c")),   // Red at -100
                new Stop(0.3f, new SolidColor("#ffcf33")),   // Orange around -40
                new Stop(0.5f, new SolidColor("#f1c40f")),   // Yellow near 0
                new Stop(0.83f, new SolidColor("#27ae60")),  // Green near 80
                new Stop(1.0f, new SolidColor("#3498db"))    // Blue at 120
        );
        configuration.addyAxis(yAxis);

        // Plot options for SOLIDGAUGE, including animations and data label format
        PlotOptionsSolidgauge plotOptions = new PlotOptionsSolidgauge();
        plotOptions.setAnimation(true);       // enable basic animation on data changes
        DataLabels dataLabels = new DataLabels();
        dataLabels.setY(-20);                // position label a bit higher
        dataLabels.setFormat("{y}%");        // show e.g. "42%"
        plotOptions.setDataLabels(dataLabels);
        configuration.setPlotOptions(plotOptions);

        series = new ListSeries("Water Level", 0);
        configuration.addSeries(series);
    }

    /**
     * Sets the current water level value on the gauge.
     *
     * <ul>
     *   <li>Values &gt; 120 are clamped to 120 (blue zone).</li>
     *   <li>Values &lt; -100 are clamped to -100 (red zone).</li>
     *   <li>Values between -100 and 120 are linearly mapped across the color stops.</li>
     * </ul>
     *
     * @param value the water level value
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
}
