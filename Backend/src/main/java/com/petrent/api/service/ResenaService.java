package com.petrent.api.service;

import com.petrent.api.dto.ResenaRequest;
import com.petrent.api.dto.ResenaResponse;
import com.petrent.api.entity.Perro;
import com.petrent.api.entity.Resena;
import com.petrent.api.entity.Reserva;
import com.petrent.api.entity.Usuario;
import com.petrent.api.exception.BusinessRuleException;
import com.petrent.api.exception.DuplicateResourceException;
import com.petrent.api.exception.ResourceNotFoundException;
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
import java.math.RoundingMode;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class ResenaService {

    private final ResenaRepository resenaRepository;
    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;

  
    @Transactional
    public ResenaResponse registrar(ResenaRequest request, Authentication authentication) {
        log.info("Registrando reseña para reserva id={} por '{}'",
                request.getIdReserva(), authentication.getName());

        Usuario usuario = usuarioRepository.findByCorreo(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado"));

        Reserva reserva = reservaRepository.findById(request.getIdReserva())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe una reserva con id: " + request.getIdReserva()));

        if (!reserva.getUsuario().getId().equals(usuario.getId())) {
            throw new BusinessRuleException("Solo puede reseñar sus propias reservas");
        }

        if (!"COMPLETADA".equals(reserva.getEstado())) {
            throw new BusinessRuleException("Solo se pueden reseñar reservas completadas");
        }

        if (resenaRepository.existsByReservaId(reserva.getId())) {
            throw new DuplicateResourceException("Esta reserva ya tiene una reseña registrada");
        }

        Resena resena = Resena.builder()
                .reserva(reserva)
                .calificacion(request.getCalificacion())
                .comentario(request.getComentario())
                .activo(true)
                .build();
        resena = resenaRepository.save(resena);

        recalcularCalificacion(reserva.getPerro());

        log.info("Reseña registrada id={} para perro id={}",
                resena.getId(), reserva.getPerro().getId());
        return toResponse(resena);
    }

    @Transactional(readOnly = true)
    public Page<ResenaResponse> listarPorPerro(Long idPerro, Pageable pageable) {
        log.debug("Listando reseñas del perro id={}", idPerro);
        return resenaRepository.findByActivoTrueAndReserva_Perro_Id(idPerro, pageable)
                .map(this::toResponse);
    }


    @Transactional
    public void eliminar(Long id) {
        log.info("Ocultando reseña id={} (eliminación lógica)", id);
        Resena resena = resenaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe una reseña con id: " + id));
        resena.setActivo(false);

        recalcularCalificacion(resena.getReserva().getPerro());
    }

 
    private void recalcularCalificacion(Perro perro) {
        List<Resena> activas = resenaRepository
                .findByActivoTrueAndReserva_Perro_Id(
                        perro.getId(), org.springframework.data.domain.Pageable.unpaged())
                .getContent();

        if (activas.isEmpty()) {
            perro.setCalificacion(BigDecimal.ZERO);
            perro.setResenasTotales(0);
            return;
        }

        double promedio = activas.stream()
                .mapToInt(Resena::getCalificacion)
                .average()
                .orElse(0.0);

        perro.setCalificacion(BigDecimal.valueOf(promedio).setScale(1, RoundingMode.HALF_UP));
        perro.setResenasTotales(activas.size());
        log.debug("Calificación del perro id={} actualizada a {}", perro.getId(), perro.getCalificacion());
    }

    private ResenaResponse toResponse(Resena r) {
        return ResenaResponse.builder()
                .id(r.getId())
                .idReserva(r.getReserva().getId())
                .idPerro(r.getReserva().getPerro().getId())
                .nombrePerro(r.getReserva().getPerro().getNombre())
                .nombreUsuario(r.getReserva().getUsuario().getNombre())
                .calificacion(r.getCalificacion())
                .comentario(r.getComentario())
                .activo(r.getActivo())
                .fechaCreacion(r.getFechaCreacion())
                .build();
    }
}
