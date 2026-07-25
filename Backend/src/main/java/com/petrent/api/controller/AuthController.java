package com.petrent.api.controller;

import com.petrent.api.dto.UsuarioRegisterRequest;
import com.petrent.api.dto.UsuarioResponse;
import com.petrent.api.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Registro, login y verificación de sesión")
public class AuthController {

    private final UsuarioService usuarioService;

   
    @PostMapping("/registro")
    @Operation(summary = "Registrar un nuevo usuario (rol 'usuario' por defecto)")
    public ResponseEntity<UsuarioResponse> registrar(@Valid @RequestBody UsuarioRegisterRequest request) {
        UsuarioResponse response = usuarioService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    
    @PostMapping("/login")
    @Operation(summary = "Inicia sesión validando credenciales por HTTP Basic")
    public ResponseEntity<UsuarioResponse> login(Authentication authentication) {
        return ResponseEntity.ok(usuarioService.obtenerActual(authentication));
    }

    
    @GetMapping("/me")
    @Operation(summary = "Devuelve los datos del usuario autenticado (verifica sesión activa)")
    public ResponseEntity<UsuarioResponse> quienSoy(Authentication authentication) {
        return ResponseEntity.ok(usuarioService.obtenerActual(authentication));
    }
}
