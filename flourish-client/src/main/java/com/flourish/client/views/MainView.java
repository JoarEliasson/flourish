package com.flourish.client.views;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("")
@PageTitle("Home")
/**
 * The main view of the Flourish application.
 * <p>
 * This view is the entry point of the application.
 * </p>
 *
 * @author Joar Eliasson
 * @version 1.0
 * @since 2025-02-13
 */
public class MainView extends VerticalLayout {

    public MainView() {
        add(new H1("Welcome to Flourish!"));
    }
}
