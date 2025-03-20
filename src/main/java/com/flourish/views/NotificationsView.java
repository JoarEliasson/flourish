package com.flourish.views;

import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import com.flourish.domain.User;
import com.flourish.service.PlantNotificationService;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import jakarta.annotation.security.RolesAllowed;
import java.util.List;

/**
 * A Vaadin view that displays notifications for the logged-in user.
 * <p>
 * This view retrieves the current user's notifications via the {@link PlantNotificationService}
 * and displays them in a list format. If notifications are present, a red badge with the count is shown;
 * otherwise, a message indicating that no notifications are available is displayed.
 * Access to this view is restricted to users with the "USER" role.
 * </p>
 * @author Zahraa Alqassab
 * @since 2025-03-11
 */
@PageTitle("Notifications")
@Route(value = "notifications", layout = MainLayout.class)
@RolesAllowed("USER")
public class NotificationsView extends VerticalLayout {

    private final PlantNotificationService notificationService;

    /**
     * Constructs a new {@code NotificationsView} instance.
     *
     * @param notificationService the service used to generate notifications for the logged-in user.
     */
    public NotificationsView(PlantNotificationService notificationService) {
        this.notificationService = notificationService;

        // Retrieve the logged-in user from the current Vaadin session.
        User user = (User) VaadinSession.getCurrent().getAttribute("user");
        if (user == null) {
            Notification.show("You must be logged in to view notifications.", 3000, Notification.Position.TOP_CENTER);
            return;
        }
        Long userId = user.getId();

        // Retrieve notifications for the user.
        List<String> notifications = notificationService.generateNotificationsForUser(userId);

        // If notifications exist, display a red badge with the notification count.
        if (!notifications.isEmpty()) {
            Div notificationBadge = new Div(new Paragraph(notifications.size() + " notifications"));
            notificationBadge.getStyle()
                    .set("background-color", "red")
                    .set("color", "white")
                    .set("padding", "5px 10px")
                    .set("border-radius", "10px")
                    .set("font-weight", "bold")
                    .set("display", "inline-block")
                    .set("margin-bottom", "10px");
            add(notificationBadge);
        }

        // Display each notification message or a default message if there are none.
        if (notifications.isEmpty()) {
            add(new Paragraph("No notifications at this time."));
        } else {
            for (String message : notifications) {
                Div messageDiv = new Div(new Paragraph(message));
                messageDiv.getStyle()
                        .set("padding", "10px")
                        .set("border", "1px solid #ccc")
                        .set("margin", "5px");
                add(messageDiv);
            }
        }
    }

}
