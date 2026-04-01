package com.clinix.clinic.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME_NAME = "BearerAuth";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Clinix Manager API")
                        .description("API REST - Système de Gestion de Clinique Médicale\n\n" +
                                "**Authentification :** Utiliser POST /api/auth/login pour obtenir un token JWT, " +
                                "puis cliquer sur **Authorize** et saisir : `Bearer <votre_token>`")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Clinix Manager")
                                .email("contact@clinix.com")))
                // Schéma de sécurité JWT Bearer
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Saisir le token JWT obtenu via /api/auth/login")));
    }
}
