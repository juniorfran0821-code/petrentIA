package com.petrent.api.controller;

import com.petrent.api.dto.*;
import com.petrent.api.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Administración de cuentas de usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;

   
    @GetMapping
    @Operation(summary = "Listar todos los usuarios (solo ADMIN, paginado)")
    public ResponseEntity<Page<UsuarioResponse>> listar(Pageable pageable) {
        return ResponseEntity.ok(usuarioService.listar(pageable));
    }

    
    @GetMapping("/buscar")
    @Operation(summary = "Buscar usuarios por nombre o correo (solo ADMIN)")
    public ResponseEntity<Page<UsuarioResponse>> buscar(@RequestParam String texto, Pageable pageable) {
        return ResponseEntity.ok(usuarioService.buscarPorTexto(texto, pageable));
    }

    
    @GetMapping("/{id}")
    @Operation(summary = "Obtener un usuario por su ID")
    public ResponseEntity<UsuarioResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar nombre y correo de un usuario")
    public ResponseEntity<UsuarioResponse> actualizar(@PathVariable Long id,
                                                      @Valid @RequestBody UsuarioUpdateRequest request,
                                                      Authentication authentication) {
        return ResponseEntity.ok(usuarioService.actualizar(id, request, authentication));
    }

    
    @PatchMapping("/{id}/password")
    @Operation(summary = "Cambiar la contraseña del usuario autenticado")
    public ResponseEntity<Void> cambiarPassword(@PathVariable Long id,
                                                @Valid @RequestBody CambiarPasswordRequest request,
                                                Authentication authentication) {
        usuarioService.cambiarPassword(id, request, authentication);
        return ResponseEntity.noContent().build();
    }

   
    @PatchMapping("/{id}/rol")
    @Operation(summary = "Cambiar el rol de un usuario (solo ADMIN)")
    public ResponseEntity<UsuarioResponse> actualizarRol(@PathVariable Long id,
                                                         @Valid @RequestBody UsuarioRolRequest request) {
        return ResponseEntity.ok(usuarioService.actualizarRol(id, request));
    }

    
    @DeleteMapping("/{id}")
    @Operation(summary = "Dar de baja un usuario (eliminación lógica: activo=false)")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    
    @PatchMapping("/{id}/reactivar")
    @Operation(summary = "Reactivar un usuario dado de baja (solo ADMIN)")
    public ResponseEntity<Void> reactivar(@PathVariable Long id) {
        usuarioService.reactivar(id);
        return ResponseEntity.noContent().build();
    }
}
