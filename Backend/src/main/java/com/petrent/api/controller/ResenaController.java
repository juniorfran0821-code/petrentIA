package com.petrent.api.controller;

import com.petrent.api.dto.ResenaRequest;
import com.petrent.api.dto.ResenaResponse;
import com.petrent.api.service.ResenaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/resenas")
@RequiredArgsConstructor
@Tag(name = "Reseñas", description = "Reseñas de perros asociadas a reservas completadas")
public class ResenaController {

    private final ResenaService resenaService;

   
    @PostMapping
    @Operation(summary = "Registrar una reseña sobre una reserva completada")
    public ResponseEntity<ResenaResponse> registrar(@Valid @RequestBody ResenaRequest request,
                                                    Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(resenaService.registrar(request, authentication));
    }

   
    @GetMapping("/perro/{idPerro}")
    @Operation(summary = "Listar las reseñas activas de un perro (paginado)")
    public ResponseEntity<Page<ResenaResponse>> listarPorPerro(@PathVariable Long idPerro, Pageable pageable) {
        return ResponseEntity.ok(resenaService.listarPorPerro(idPerro, pageable));
    }

    
    @DeleteMapping("/{id}")
    @Operation(summary = "Ocultar una reseña (eliminación lógica activo=false, solo ADMIN)")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        resenaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
