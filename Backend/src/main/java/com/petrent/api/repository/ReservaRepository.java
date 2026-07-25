package com.petrent.api.repository;

import com.petrent.api.entity.Reserva;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;


public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    Page<Reserva> findByUsuarioId(Long idUsuario, Pageable pageable);

    Page<Reserva> findByPerroId(Long idPerro, Pageable pageable);

    long countByEstado(String estado);

    @Query("SELECT COALESCE(SUM(r.precioTotal), 0) FROM Reserva r WHERE r.estado = 'COMPLETADA'")
    BigDecimal sumIngresosTotales();
}
