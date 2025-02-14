package com.flourish.client.views;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Main view displayed after successful login.
 * <p>
 * This view serves as the application's landing page for authenticated users.
 * </p>
 *
 * @author Joar Eliasson
 * @version 1.0
 * @since 2025-02-13
 */
@Route("main")
@PageTitle("Home")
public class MainView extends VerticalLayout {

    /**
     * Constructs the main view.
     */
    public MainView() {
        add(new H1("Welcome to Flourish!"));
    }
}
