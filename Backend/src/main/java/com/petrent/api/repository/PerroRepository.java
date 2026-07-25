package com.petrent.api.repository;

import com.petrent.api.entity.Perro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface PerroRepository extends JpaRepository<Perro, Long> {

    Page<Perro> findByActivoTrue(Pageable pageable);

    Page<Perro> findByActivoTrueAndNombreContainingIgnoreCaseOrActivoTrueAndRazaContainingIgnoreCase(
            String nombre, String raza, Pageable pageable);

    Page<Perro> findByActivoTrueAndDisponibleTrue(Pageable pageable);

    Page<Perro> findByActivoTrueAndTamano(String tamano, Pageable pageable);


    long countByActivoTrue();

    long countByActivoTrueAndDisponibleTrue();

    long countByActivoTrueAndDisponibleFalse();

    @Query("SELECT MIN(p.precio), MAX(p.precio), AVG(p.precio) FROM Perro p WHERE p.activo = true")
    List<Object[]> findEstadisticasPrecios();

    @Query("SELECT p.nombre, p.raza, p.tamano, p.precio, p.disponible FROM Perro p WHERE p.activo = true ORDER BY p.nombre")
    List<Object[]> findResumenPerrosActivos();
}
