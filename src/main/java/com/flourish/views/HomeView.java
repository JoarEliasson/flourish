package com.flourish.views;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
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

        Image plantImage = new Image("images/PlantBild.jpg", "Plant Here!");
        plantImage.setWidth("200px");
        plantImage.getStyle().set("border-radius","15px").set("box-shadow","0px 4px 10px rgba(0,0,0,0.1)");


        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setSizeFull();
        add(title,desc,plantImage);
    }
}