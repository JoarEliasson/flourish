package com.flourish.views.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;

/**
 * A Vaadin Flow component that wraps a custom <my-liquid-gauge> web component.
 */
@Tag("my-liquid-gauge")
@JsModule("./MyLiquidGauge.js")
public class MyLiquidGauge extends Component {

    public MyLiquidGauge() {
        getElement().getStyle().set("width", "200px");
        getElement().getStyle().set("height", "200px");
    }

    /**
     * Sets the liquid fill value between 0 and 1.
     */
    public void setValue(double value) {
        getElement().setProperty("value", value);
    }
}
