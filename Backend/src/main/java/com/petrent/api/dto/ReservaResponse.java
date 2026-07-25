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
public class ReservaResponse {
    private Long id;
    private Long idUsuario;
    private String nombreUsuario;
    private Long idPerro;
    private String nombrePerro;
    private String imagenPerro;
    private OffsetDateTime fechaInicio;
    private OffsetDateTime fechaFin;
    private Integer horas;
    private BigDecimal precioTotal;
    private String estado;
    private Long idPago;
    private String estadoPago;
    private Boolean tieneResena;
    private OffsetDateTime fechaCreacion;
    private OffsetDateTime fechaActualizacion;
}