package com.petrent.api.service;

import com.petrent.api.dto.PagoEstadoRequest;
import com.petrent.api.dto.PagoResponse;
import com.petrent.api.entity.Pago;
import com.petrent.api.exception.ResourceNotFoundException;
import com.petrent.api.repository.PagoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class PagoService {

    private final PagoRepository pagoRepository;

   
    @Transactional(readOnly = true)
    public Page<PagoResponse> listar(Pageable pageable) {
        log.debug("Listando todos los pagos — página {}", pageable.getPageNumber());
        return pagoRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<PagoResponse> listarPorEstado(String estadoPago, Pageable pageable) {
        log.debug("Listando pagos con estado='{}' — página {}", estadoPago, pageable.getPageNumber());
        return pagoRepository.findByEstadoPago(estadoPago, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public PagoResponse obtenerPorId(Long id) {
        log.debug("Consultando pago id={}", id);
        return toResponse(buscarOFallar(id));
    }

    
    @Transactional
    public PagoResponse actualizarEstado(Long id, PagoEstadoRequest request) {
        log.info("Actualizando estado del pago id={} a '{}'", id, request.getEstadoPago());
        Pago pago = buscarOFallar(id);
        pago.setEstadoPago(request.getEstadoPago());
        if (request.getReferenciaTransaccion() != null) {
            pago.setReferenciaTransaccion(request.getReferenciaTransaccion());
        }
        return toResponse(pago);
    }

    private Pago buscarOFallar(Long id) {
        return pagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un pago con id: " + id));
    }

    private PagoResponse toResponse(Pago p) {
        return PagoResponse.builder()
                .id(p.getId())
                .idReserva(p.getReserva().getId())
                .monto(p.getMonto())
                .metodoPago(p.getMetodoPago())
                .estadoPago(p.getEstadoPago())
                .referenciaTransaccion(p.getReferenciaTransaccion())
                .fechaPago(p.getFechaPago())
                .fechaActualizacion(p.getFechaActualizacion())
                .build();
    }
}
