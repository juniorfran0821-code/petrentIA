package com.petrent.api.repository;

import com.petrent.api.entity.Resena;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface ResenaRepository extends JpaRepository<Resena, Long> {

  
    Optional<Resena> findByReservaId(Long idReserva);

  
    boolean existsByReservaId(Long idReserva);

 
    Page<Resena> findByActivoTrueAndReserva_Perro_Id(Long idPerro, Pageable pageable);
}
