package com.petrent.api.repository;

import com.petrent.api.entity.Pago;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PagoRepository extends JpaRepository<Pago, Long> {

    
    Optional<Pago> findByReservaId(Long idReserva);

   
    Page<Pago> findByEstadoPago(String estadoPago, Pageable pageable);
}
