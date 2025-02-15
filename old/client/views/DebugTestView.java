package com.flourish.old.client.views;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("debug-test")
public class DebugTestView extends VerticalLayout {
    public DebugTestView() {
        add(new Span("Hello from DebugTestView! If you see this, Vaadin found the route."));
    }
}
