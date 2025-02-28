package com.flourish.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
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
        title.addClassNames(LumoUtility.TextColor.SUCCESS,LumoUtility.FontSize.XXXLARGE);
        Paragraph desc = new Paragraph("Your personal plant library!");
        desc.addClassNames(LumoUtility.TextColor.SECONDARY,LumoUtility.FontSize.LARGE);

        Button myPlantsNavigation = new Button("My Plants!", e -> getUI().ifPresent(ui -> ui.navigate("my-plants")));
        Button allPlantsNavigation = new Button("All Plants!", e -> getUI().ifPresent(ui -> ui.navigate("all-plants")));

        myPlantsNavigation.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        allPlantsNavigation.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

        HorizontalLayout buttonLayout = new HorizontalLayout(myPlantsNavigation,allPlantsNavigation);
        buttonLayout.setPadding(true);
        buttonLayout.setSpacing(true);

        Image plantImage = new Image("https://plus.unsplash.com/premium_photo-1675864663002-c330710c6ba0?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MXx8cGxhbnR8ZW58MHx8MHx8fDA%3D", "Plant Here!");

        plantImage.setWidth("200px");
        plantImage.getStyle().set("border-radius","15px").set("box-shadow","0px 4px 10px rgba(0,0,0,0.1)");



        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setSizeFull();
        add(title,desc,buttonLayout,plantImage);
    }
}