package com.petrent.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResenaResponse {
    private Long id;
    private Long idReserva;
    private Long idPerro;
    private String nombrePerro;
    private String nombreUsuario;
    private Integer calificacion;
    private String comentario;
    private Boolean activo;
    private OffsetDateTime fechaCreacion;
}