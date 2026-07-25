package com.petrent.api.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.LocalDateTime;
import java.util.List;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${app.cors.origenes:http://localhost:5173}")
    private String origenesPermitidos;

    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

   
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuracion = new CorsConfiguration();
        configuracion.setAllowedOrigins(List.of(origenesPermitidos.split(",")));
        configuracion.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuracion.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuracion.setAllowCredentials(true);
        configuracion.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource fuente = new UrlBasedCorsConfigurationSource();
        fuente.registerCorsConfiguration("/**", configuracion);
        return fuente;
    }

    private String buildErrorJson(int status, String error, String message, String path) {
        String safeMessage = message == null ? "" : message.replace("\"", "'");
        return "{"
                + "\"timestamp\":\"" + LocalDateTime.now() + "\","
                + "\"status\":" + status + ","
                + "\"error\":\"" + error + "\","
                + "\"message\":\"" + safeMessage + "\","
                + "\"path\":\"" + path + "\""
                + "}";
    }

    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/swagger-ui/**",       
                        "/v3/api-docs/**",      
                        "/swagger-ui.html",
                        "/api/auth/registro",  
                        "/api/ia/**"           
                ).permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() 
                .requestMatchers(HttpMethod.GET, "/api/perros/**").permitAll()   
                .requestMatchers(HttpMethod.GET, "/api/resenas/**").permitAll()  

                .requestMatchers(HttpMethod.POST, "/api/perros/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,  "/api/perros/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH,"/api/perros/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE,"/api/perros/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/usuarios", "/api/usuarios/buscar").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/usuarios/*/rol").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/usuarios/*/reactivar").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/usuarios/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/reservas").hasRole("ADMIN")
                .requestMatchers("/api/pagos/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/resenas/**").hasRole("ADMIN")

                .requestMatchers("/api/usuarios/**").hasAnyRole("ADMIN", "USUARIO")
                .requestMatchers("/api/reservas/**").hasAnyRole("ADMIN", "USUARIO")
                .requestMatchers("/api/resenas/**").hasAnyRole("ADMIN", "USUARIO")

                .requestMatchers("/api/auth/me").authenticated()
                .anyRequest().authenticated()
            )
            .httpBasic(basic -> basic
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.getWriter().write(buildErrorJson(
                            401, "Unauthorized",
                            "Credenciales inválidas o no proporcionadas",
                            request.getRequestURI()));
                })
            )
            .exceptionHandling(ex -> ex
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.getWriter().write(buildErrorJson(
                            403, "Forbidden",
                            "No tiene permisos para realizar esta operación",
                            request.getRequestURI()));
                })
            );

        return http.build();
    }
}
