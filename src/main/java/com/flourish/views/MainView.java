package com.flourish.views;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

/**
 * A simple main view at "/main".
 * <p>
 * If you're not logged in, Vaadin Spring Security will redirect to "/signin".
 * </p>
 */
@Route("main")
public class MainView extends VerticalLayout {

    public MainView() {
        add(new H1("Welcome to Main View!"));
    }
}
