package com.petrent.api.config;

import com.petrent.api.entity.Usuario;
import com.petrent.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final org.springframework.core.env.Environment env;

    
    @Override
    public void run(String... args) {
        String correoAdmin    = env.getProperty("app.admin.correo");
        String passwordAdmin  = env.getProperty("app.admin.password");
        String nombreAdmin    = env.getProperty("app.admin.username", "admin");

        if (passwordAdmin == null || passwordAdmin.isBlank()) {
            log.warn("APP_ADMIN_PASSWORD no configurada en .env — no se creará el administrador inicial.");
            return;
        }


        if (usuarioRepository.existsByCorreo(correoAdmin)) {
            log.info("El administrador ya existe ({}) — no se vuelve a crear.", correoAdmin);
            return;
        }

       
        Usuario admin = Usuario.builder()
                .nombre(nombreAdmin)
                .correo(correoAdmin)
                .password(passwordEncoder.encode(passwordAdmin))
                .rol("admin")
                .build();

        usuarioRepository.save(admin);
        log.info("Administrador inicial creado correctamente con correo: {}", correoAdmin);
    }
}
