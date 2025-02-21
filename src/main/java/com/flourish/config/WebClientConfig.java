package com.flourish.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration class to provide a WebClient bean for API calls.
 *
 * <p>This allows other services to autowire a configured WebClient.</p>
 *
 * @author
 *   Joar Eliasson
 * @version
 *   1.1.0
 * @since
 *   2025-02-18
 */
@Configuration
public class WebClientConfig {

    /**
     * Creates and configures a default WebClient bean.
     *
     * @return a WebClient instance.
     */
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("https://perenual.com")
                .defaultHeader("Accept", "application/json")
                .build();
    }
}
