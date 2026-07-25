package com.petrent.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;


@Entity
@Table(name = "perros")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Perro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_propietario", nullable = false)
    private Usuario propietario;

    @Column(nullable = false, length = 60)
    private String nombre;

    @Column(nullable = false, length = 80)
    private String raza;

    @Column(nullable = false, length = 20)
    private String tamano;

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal precio;

    @Column(nullable = false, length = 500)
    private String descripcion;

    @Column(length = 255)
    private String etiquetas;

    @Column(length = 255)
    private String imagen;

    @Column(nullable = false)
    @Builder.Default
    private Boolean disponible = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @Column(precision = 2, scale = 1)
    @Builder.Default
    private BigDecimal calificacion = BigDecimal.ZERO;

    @Column(name = "resenas_totales")
    @Builder.Default
    private Integer resenasTotales = 0;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private OffsetDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion", nullable = false)
    private OffsetDateTime fechaActualizacion;

    @PrePersist
    protected void alCrear() {
        if (fechaCreacion == null) {
            fechaCreacion = OffsetDateTime.now(ZoneOffset.UTC);
        }
        fechaActualizacion = OffsetDateTime.now(ZoneOffset.UTC);
    }

    @PreUpdate
    protected void alActualizar() {
        fechaActualizacion = OffsetDateTime.now(ZoneOffset.UTC);
    }
}
