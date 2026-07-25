package com.petrent.api.controller;

import com.petrent.api.dto.PerroDisponibilidadRequest;
import com.petrent.api.dto.PerroRequest;
import com.petrent.api.dto.PerroResponse;
import com.petrent.api.service.PerroService;
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
@RequestMapping("/api/perros")
@RequiredArgsConstructor
@Tag(name = "Perros", description = "CRUD del catálogo de perros disponibles para alquilar")
public class PerroController {

    private final PerroService perroService;

    
    @PostMapping
    @Operation(summary = "Registrar un nuevo perro en el catálogo (solo ADMIN)")
    public ResponseEntity<PerroResponse> registrar(@Valid @RequestBody PerroRequest request,
                                                   Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(perroService.registrar(request, authentication));
    }

    
    @GetMapping
    @Operation(summary = "Listar todos los perros activos del catálogo (paginado)")
    public ResponseEntity<Page<PerroResponse>> listar(Pageable pageable) {
        return ResponseEntity.ok(perroService.listar(pageable));
    }

   
    @GetMapping("/{id}")
    @Operation(summary = "Obtener un perro por su ID")
    public ResponseEntity<PerroResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(perroService.obtenerPorId(id));
    }

   
    @GetMapping("/buscar")
    @Operation(summary = "Buscar perros por nombre o raza (texto parcial, paginado)")
    public ResponseEntity<Page<PerroResponse>> buscar(@RequestParam String texto, Pageable pageable) {
        return ResponseEntity.ok(perroService.buscarPorTexto(texto, pageable));
    }

    
    @GetMapping("/disponibles")
    @Operation(summary = "Listar solo los perros actualmente disponibles")
    public ResponseEntity<Page<PerroResponse>> listarDisponibles(Pageable pageable) {
        return ResponseEntity.ok(perroService.listarDisponibles(pageable));
    }

    
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar los datos de un perro (solo ADMIN)")
    public ResponseEntity<PerroResponse> actualizar(@PathVariable Long id,
                                                    @Valid @RequestBody PerroRequest request) {
        return ResponseEntity.ok(perroService.actualizar(id, request));
    }

    
    @PatchMapping("/{id}/disponibilidad")
    @Operation(summary = "Actualizar la disponibilidad de un perro (solo ADMIN)")
    public ResponseEntity<PerroResponse> actualizarDisponibilidad(
            @PathVariable Long id, @Valid @RequestBody PerroDisponibilidadRequest request) {
        return ResponseEntity.ok(perroService.actualizarDisponibilidad(id, request));
    }

    
    @DeleteMapping("/{id}")
    @Operation(summary = "Dar de baja un perro del catálogo (eliminación lógica: activo=false)")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        perroService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

   
    @PatchMapping("/{id}/reactivar")
    @Operation(summary = "Reactivar un perro dado de baja (solo ADMIN)")
    public ResponseEntity<Void> reactivar(@PathVariable Long id) {
        perroService.reactivar(id);
        return ResponseEntity.noContent().build();
    }
}
