package com.petrent.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerroResponse {
    private Long id;
    private Long idPropietario;
    private String nombrePropietario;
    private String nombre;
    private String raza;
    private String tamano;
    private BigDecimal precio;
    private String descripcion;
    private String etiquetas;
    private String imagen;
    private Boolean disponible;
    private Boolean activo;
    private BigDecimal calificacion;
    private Integer resenasTotales;
    private OffsetDateTime fechaCreacion;
    private OffsetDateTime fechaActualizacion;
}