package com.petrent.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig {

    private static final String SCHEME_NAME = "basicAuth";

    @Bean
    public OpenAPI petrentOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PetRent API")
                        .description("""
                                API REST para el sistema de alquiler de perros PetRent.
                                
                                Autenticación: HTTP Basic (correo:password en Base64).
                                Roles disponibles: admin, usuario.
                                
                                Para probar endpoints protegidos, usa el botón 'Authorize' e ingresa tus credenciales.
                                """)
                        .version("1.0.0"))
                .addSecurityItem(new SecurityRequirement().addList(SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SCHEME_NAME, new SecurityScheme()
                                .name(SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("basic")
                                .description("HTTP Basic: ingresa tu correo y contraseña")));
    }
}
