package com.flourish;

import com.flourish.config.SecurityConfig;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The main entry point of the Flourish application.
 * <p>
 * The initial route of the application is the {@link SecurityConfig} class.
 *
 * @see SecurityConfig
 *
 * @author
 *   Joar Eliasson
 * @version
 *   1.1.0
 * @since
 *   2025-02-15
 */
@SpringBootApplication
@Theme(value="flourish", variant= Lumo.DARK)
@CssImport(value = "./themes/flourish/charts-transparent.css", themeFor = "vaadin-chart")
public class FlourishApplication implements AppShellConfigurator {
    public static void main(String[] args) {
        SpringApplication.run(FlourishApplication.class, args);
    }
}
