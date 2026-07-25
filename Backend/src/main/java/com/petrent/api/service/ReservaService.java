package com.petrent.api.service;

import com.petrent.api.dto.ReservaEstadoRequest;
import com.petrent.api.dto.ReservaRequest;
import com.petrent.api.dto.ReservaResponse;
import com.petrent.api.entity.Pago;
import com.petrent.api.entity.Perro;
import com.petrent.api.entity.Reserva;
import com.petrent.api.entity.Usuario;
import com.petrent.api.exception.BusinessRuleException;
import com.petrent.api.exception.ResourceNotFoundException;
import com.petrent.api.repository.PagoRepository;
import com.petrent.api.repository.PerroRepository;
import com.petrent.api.repository.ResenaRepository;
import com.petrent.api.repository.ReservaRepository;
import com.petrent.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;


@Slf4j
@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final PerroRepository perroRepository;
    private final PagoRepository pagoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ResenaRepository resenaRepository;

    
    @Transactional
    public ReservaResponse registrarReserva(ReservaRequest request, Authentication authentication) {
        log.info("Registrando reserva: usuario='{}', perroId={}, horas={}",
                authentication.getName(), request.getIdPerro(), request.getHoras());

        Usuario usuario = usuarioRepository.findByCorreo(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado"));

        Perro perro = perroRepository.findById(request.getIdPerro())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe un perro con id: " + request.getIdPerro()));

        if (!Boolean.TRUE.equals(perro.getActivo())) {
            throw new ResourceNotFoundException("No existe un perro con id: " + request.getIdPerro());
        }

        if (!Boolean.TRUE.equals(perro.getDisponible())) {
            throw new BusinessRuleException(
                    "El perro '" + perro.getNombre() + "' no está disponible para reservar");
        }

        BigDecimal precioTotal = perro.getPrecio().multiply(BigDecimal.valueOf(request.getHoras()));
        OffsetDateTime inicio = OffsetDateTime.now(ZoneOffset.UTC);

        Reserva reserva = Reserva.builder()
                .usuario(usuario)
                .perro(perro)
                .fechaInicio(inicio)
                .fechaFin(inicio.plusHours(request.getHoras()))
                .horas(request.getHoras())
                .precioTotal(precioTotal)
                .estado("CONFIRMADA")
                .build();
        reserva = reservaRepository.save(reserva);

        Pago pago = Pago.builder()
                .reserva(reserva)
                .monto(precioTotal)
                .metodoPago(request.getMetodoPago())
                .estadoPago("PENDIENTE")
                .build();
        pago = pagoRepository.save(pago);

        perro.setDisponible(false);

        log.info("Reserva registrada id={}, pago id={}", reserva.getId(), pago.getId());
        return toResponse(reserva, pago);
    }

  
    @Transactional(readOnly = true)
    public Page<ReservaResponse> listarPorUsuario(Long idUsuario, Pageable pageable) {
        log.debug("Listando reservas del usuario id={} — página {}", idUsuario, pageable.getPageNumber());
        return reservaRepository.findByUsuarioId(idUsuario, pageable)
                .map(r -> toResponse(r, pagoRepository.findByReservaId(r.getId()).orElse(null)));
    }

  
    @Transactional(readOnly = true)
    public ReservaResponse obtenerPorId(Long id) {
        Reserva reserva = buscarOFallar(id);
        Pago pago = pagoRepository.findByReservaId(id).orElse(null);
        return toResponse(reserva, pago);
    }


    @Transactional(readOnly = true)
    public Page<ReservaResponse> listarTodas(Pageable pageable) {
        log.debug("Listando todas las reservas — página {}", pageable.getPageNumber());
        return reservaRepository.findAll(pageable)
                .map(r -> toResponse(r, pagoRepository.findByReservaId(r.getId()).orElse(null)));
    }

    @Transactional
    public ReservaResponse actualizarEstado(Long id, ReservaEstadoRequest request, Authentication authentication) {
        log.info("Actualizando estado de reserva id={} a '{}' por '{}'",
                id, request.getEstado(), authentication.getName());

        Reserva reserva = buscarOFallar(id);
        Usuario actual = usuarioRepository.findByCorreo(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado"));

        boolean esAdmin = "admin".equalsIgnoreCase(actual.getRol());
        boolean esDueno = reserva.getUsuario().getId().equals(actual.getId());

        if (!esAdmin && !esDueno) {
            throw new BusinessRuleException("No puede modificar una reserva de otro usuario");
        }

        if ("CANCELADA".equals(request.getEstado()) && !esAdmin && !"CONFIRMADA".equals(reserva.getEstado())) {
            throw new BusinessRuleException("Solo se pueden cancelar reservas confirmadas");
        }

        if ("COMPLETADA".equals(request.getEstado()) && !esAdmin) {
            throw new BusinessRuleException("Solo un administrador puede marcar una reserva como completada");
        }

        String estadoAnterior = reserva.getEstado();
        reserva.setEstado(request.getEstado());

        if ("CANCELADA".equals(request.getEstado()) && !"CANCELADA".equals(estadoAnterior)) {
            reserva.getPerro().setDisponible(true);
            log.info("Perro id={} liberado por cancelación de reserva id={}",
                    reserva.getPerro().getId(), id);
        }

        Pago pago = pagoRepository.findByReservaId(id).orElse(null);
        return toResponse(reserva, pago);
    }


    private Reserva buscarOFallar(Long id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe una reserva con id: " + id));
    }

    private ReservaResponse toResponse(Reserva r, Pago pago) {
        return ReservaResponse.builder()
                .id(r.getId())
                .idUsuario(r.getUsuario().getId())
                .nombreUsuario(r.getUsuario().getNombre())
                .idPerro(r.getPerro().getId())
                .nombrePerro(r.getPerro().getNombre())
                .imagenPerro(r.getPerro().getImagen())
                .fechaInicio(r.getFechaInicio())
                .fechaFin(r.getFechaFin())
                .horas(r.getHoras())
                .precioTotal(r.getPrecioTotal())
                .estado(r.getEstado())
                .idPago(pago != null ? pago.getId() : null)
                .estadoPago(pago != null ? pago.getEstadoPago() : null)
                .tieneResena(resenaRepository.existsByReservaId(r.getId()))
                .fechaCreacion(r.getFechaCreacion())
                .fechaActualizacion(r.getFechaActualizacion())
                .build();
    }
}
