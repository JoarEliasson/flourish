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
 * A custom gauge component for displaying a plant's water level.
 *
 * <p>This gauge is based on Vaadin Charts' SOLIDGAUGE type. The scale ranges from -100 to 100,
 * where 100 indicates the plant was just watered (optimal condition), 0 means watering is due,
 * and -100 means the plant is overdue by one full watering interval.</p>
 *
 * <p>The gauge is pre-configured with appropriate color stops (green for high levels,
 * yellow near the mid-range, and red for low levels), a semi-circular display, and data labels.</p>
 *
 */
public class WaterGauge extends Chart {

    private final Configuration configuration;
    private final YAxis yAxis;
    private final ListSeries series;

    /**
     * Constructs a new WaterGauge with default configuration.
     */
    public WaterGauge() {
        super(ChartType.SOLIDGAUGE);
        configuration = getConfiguration();
        configuration.setTitle("Water Level");

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

        yAxis = new YAxis();
        yAxis.setMin(-100);
        yAxis.setMax(100);
        yAxis.setTickInterval(100);
        yAxis.getLabels().setY(-16);
        yAxis.setGridLineWidth(0);

        yAxis.setStops(
                new Stop(0.0f, new SolidColor("#e74c3c")), // red at lower extreme
                new Stop(0.5f, new SolidColor("#f1c40f")), // yellow near mid-range
                new Stop(1.0f, new SolidColor("#27ae60"))  // green at upper extreme
        );
        configuration.addyAxis(yAxis);

        PlotOptionsSolidgauge options = new PlotOptionsSolidgauge();
        DataLabels dataLabels = new DataLabels();
        dataLabels.setY(-20);
        options.setDataLabels(dataLabels);
        configuration.setPlotOptions(options);

        series = new ListSeries("Water Level", 0);
        configuration.addSeries(series);
    }

    /**
     * Sets the current water level value on the gauge.
     *
     * <p>The value should be in the range [-100, 100]:
     * <ul>
     *   <li>100 means the plant was just watered.</li>
     *   <li>0 means watering is due.</li>
     *   <li>-100 means one full interval past watering due.</li>
     * </ul>
     * </p>
     *
     * @param value the water level value.
     */
    public void setWaterLevel(double value) {
        if (value > 100) {
            value = 100;
        } else if (value < -100) {
            value = -100;
        }
        series.setData(value);
        drawChart();
    }
}
