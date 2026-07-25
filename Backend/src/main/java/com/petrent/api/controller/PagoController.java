package com.petrent.api.controller;

import com.petrent.api.dto.PagoEstadoRequest;
import com.petrent.api.dto.PagoResponse;
import com.petrent.api.service.PagoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
@Tag(name = "Pagos", description = "Consulta y gestión del estado de los pagos (solo ADMIN)")
public class PagoController {

    private final PagoService pagoService;

    
    @GetMapping
    @Operation(summary = "Listar todos los pagos, opcionalmente filtrados por estado")
    public ResponseEntity<Page<PagoResponse>> listar(@RequestParam(required = false) String estadoPago,
                                                     Pageable pageable) {
        if (estadoPago != null && !estadoPago.isBlank()) {
            return ResponseEntity.ok(pagoService.listarPorEstado(estadoPago, pageable));
        }
        return ResponseEntity.ok(pagoService.listar(pageable));
    }

    
    @GetMapping("/{id}")
    @Operation(summary = "Buscar un pago por su ID")
    public ResponseEntity<PagoResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pagoService.obtenerPorId(id));
    }

    
    @PatchMapping("/{id}/estado")
    @Operation(summary = "Actualizar el estado de un pago (PENDIENTE, PAGADO, REEMBOLSADO)")
    public ResponseEntity<PagoResponse> actualizarEstado(@PathVariable Long id,
                                                         @Valid @RequestBody PagoEstadoRequest request) {
        return ResponseEntity.ok(pagoService.actualizarEstado(id, request));
    }
}
