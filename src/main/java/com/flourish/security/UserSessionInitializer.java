package com.flourish.security;

import com.flourish.domain.User;
import com.flourish.repository.UserRepository;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class UserSessionInitializer implements VaadinServiceInitListener {

    private final UserRepository userRepository;

    public UserSessionInitializer(UserRepository userRepository) {
        System.out.println("UserSessionInitializer constructor invoked!");
        this.userRepository = userRepository;
    }

    @Override
    public void serviceInit(ServiceInitEvent event) {
        System.out.println("UserSessionInitializer serviceInit invoked!");

        event.getSource().addUIInitListener(uiEvent -> {
            UI ui = uiEvent.getUI();
            ui.addBeforeEnterListener(beforeEnterEvent -> {
                if (VaadinSession.getCurrent().getAttribute("user") == null) {
                    var auth = SecurityContextHolder.getContext().getAuthentication();
                    if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof UserDetails) {
                        String username = ((UserDetails) auth.getPrincipal()).getUsername();
                        User user = userRepository.findByEmail(username);
                        if (user != null) {
                            VaadinSession.getCurrent().setAttribute("user", user);
                            VaadinSession.getCurrent().setAttribute("userId", user.getId());
                        }
                    }
                }
            });
        });
    }

}
