package com.flourish.views;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

/**
 * The main content view for authenticated users.
 */
@PageTitle("Home")
@Route(value = "", layout = MainLayout.class) // Changed from "main" to "" to set as root route
@RolesAllowed("USER")
public class HomeView extends VerticalLayout {

    public HomeView() {
        H1 title = new H1("Welcome to Flourish!");
        title.getStyle().set("color", "#388e3c").set("font-size", "28px");
        add(title);
    }
}