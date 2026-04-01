package com.clinix.clinic.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Frontend Angular
        config.setAllowedOrigins(List.of("http://localhost:4200"));

        // Méthodes autorisées
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // Headers autorisés (dont Authorization pour JWT)
        config.setAllowedHeaders(List.of("*"));

        // Exposer le header Authorization dans les réponses
        config.setExposedHeaders(List.of("Authorization"));

        // Autoriser les cookies/credentials
        config.setAllowCredentials(true);

        // Durée de cache du preflight (1h)
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
