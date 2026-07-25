package com.petrent.api.controller;

import com.petrent.api.dto.ReservaEstadoRequest;
import com.petrent.api.dto.ReservaRequest;
import com.petrent.api.dto.ReservaResponse;
import com.petrent.api.service.ReservaService;
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
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
@Tag(name = "Reservas", description = "Proceso de reserva: crea reserva + pago y actualiza disponibilidad del perro")
public class ReservaController {

    private final ReservaService reservaService;

   
    @PostMapping
    @Operation(summary = "Registrar una reserva (crea reserva + pago y bloquea disponibilidad del perro)")
    public ResponseEntity<ReservaResponse> registrar(@Valid @RequestBody ReservaRequest request,
                                                     Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reservaService.registrarReserva(request, authentication));
    }

   
    @GetMapping
    @Operation(summary = "Listar todas las reservas del sistema (solo ADMIN, paginado)")
    public ResponseEntity<Page<ReservaResponse>> listarTodas(Pageable pageable) {
        return ResponseEntity.ok(reservaService.listarTodas(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una reserva por su ID")
    public ResponseEntity<ReservaResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.obtenerPorId(id));
    }

    
    @GetMapping("/usuario/{idUsuario}")
    @Operation(summary = "Listar las reservas de un usuario (historial)")
    public ResponseEntity<Page<ReservaResponse>> listarPorUsuario(@PathVariable Long idUsuario,
                                                                   Pageable pageable) {
        return ResponseEntity.ok(reservaService.listarPorUsuario(idUsuario, pageable));
    }

   
    @PatchMapping("/{id}/estado")
    @Operation(summary = "Cambiar el estado de una reserva (cancelar o completar)")
    public ResponseEntity<ReservaResponse> actualizarEstado(@PathVariable Long id,
                                                             @Valid @RequestBody ReservaEstadoRequest request,
                                                             Authentication authentication) {
        return ResponseEntity.ok(reservaService.actualizarEstado(id, request, authentication));
    }
}
