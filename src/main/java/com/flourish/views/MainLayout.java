package com.flourish.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.server.VaadinSession;
import jakarta.annotation.security.RolesAllowed;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Provides a top-level layout with a header, logo, tab-based navigation, and user avatar.
 *
 * <p>Applies a dark-inspired theme through custom properties. The selected tab is
 * determined based on the current route.</p>
 *
 * @author
 *   Joar Eliasson, Kenan Al Tal, Emil Åqvist
 * @version
 *   1.2.0
 * @since
 *   2025-03-14
 */
@RolesAllowed("USER")
public class MainLayout extends AppLayout implements BeforeEnterObserver {

    private final Map<Tab, String> tabToRoute = new LinkedHashMap<>();
    private final Tabs navTabs = new Tabs();
    private final Avatar profileAvatar = new Avatar("USER");

    /**
     * Constructs a MainLayout with a logo image, tabbed navigation, and user profile avatar.
     */
    public MainLayout() {
        createHeader();
        setPrimarySection(Section.NAVBAR);
        getElement().getStyle().set("background-color", "var(--flourish-bg-color)");
    }

    /**
     * Creates the header with a logo, tab bar, and context menu attached to the user avatar.
     */
    private void createHeader() {
        Image logoImage = new Image("themes/flourish/images/flourish-logo.png", "Flourish Logo");
        logoImage.setWidth("160px");
        logoImage.setHeight("auto");

        Tab homeTab = new Tab("Home");
        Tab myPlantsTab = new Tab("My Plants");
        Tab allPlantsTab = new Tab("All Plants");
        Tab settingsTab = new Tab("Settings");
        Tab notificationsTab = new Tab("Notifications");

        tabToRoute.put(homeTab, "");
        tabToRoute.put(myPlantsTab, "my-plants");
        tabToRoute.put(allPlantsTab, "all-plants");
        tabToRoute.put(settingsTab, "settings");
        tabToRoute.put(notificationsTab, "notifications");

        navTabs.add(homeTab, myPlantsTab, allPlantsTab, settingsTab, notificationsTab);
        navTabs.addSelectedChangeListener(event -> {
            Tab selectedTab = event.getSelectedTab();
            String route = tabToRoute.get(selectedTab);
            if (route != null) {
                getUI().ifPresent(ui -> ui.navigate(route));
            }
        });

        Avatar profileAvatar = new Avatar("USER");
        profileAvatar.setImage("images/DefualtImage.png");

        ContextMenu menu = new ContextMenu(profileAvatar);
        menu.setOpenOnClick(true);
        menu.addItem("Account Settings", e -> getUI().ifPresent(ui -> ui.navigate("profile")));
        menu.addItem("Toggle Email Notifications", e -> {});
        menu.addItem("Log Out", e -> logout());

        HorizontalLayout navBar = new HorizontalLayout(logoImage, navTabs, profileAvatar);
        navBar.setWidthFull();
        navBar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        navBar.setAlignItems(FlexComponent.Alignment.CENTER);
        navBar.getStyle()
                .set("background-color", "var(--flourish-surface-color)")
                .set("padding", "10px");

        addToNavbar(navBar);
    }

    /**
     * Ensures the correct tab is selected based on the route the user navigates to.
     *
     * @param event the event carrying route details
     */
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String targetRoute = event.getNavigationTarget().getSimpleName();
        tabToRoute.forEach((tab, route) -> {
            if (matchesTargetRoute(route, targetRoute)) {
                navTabs.setSelectedTab(tab);
            }
        });
    }

    /**
     * Determines whether the route string corresponds to the target view class name.
     *
     * @param route the route path mapped to a Tab
     * @param targetRouteClassName the simple name of the view class
     * @return true if the mapped route matches the target route class, false otherwise
     */
    private boolean matchesTargetRoute(String route, String targetRouteClassName) {
        if (route.isEmpty() && "HomeView".equalsIgnoreCase(targetRouteClassName)) {
            return true;
        }
        String[] parts = route.split("-");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) {
                sb.append(part.substring(0, 1).toUpperCase()).append(part.substring(1));
            }
        }
        sb.append("View");
        String guessedViewName = sb.toString();
        return guessedViewName.equalsIgnoreCase(targetRouteClassName);
    }

    /**
     * Logs out the user by invalidating the session and redirecting to the login page.
     */
    private void logout() {
        VaadinSession.getCurrent().getSession().invalidate();
        VaadinSession.getCurrent().close();
        getUI().ifPresent(ui -> ui.getPage().setLocation("login"));
    }
}
