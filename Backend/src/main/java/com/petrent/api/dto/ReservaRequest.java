package com.petrent.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservaRequest {

    @NotNull(message = "El id del perro es obligatorio")
    private Long idPerro;

    @NotNull(message = "Las horas son obligatorias")
    @Min(value = 1, message = "El mínimo de horas es 1")
    @Max(value = 8, message = "El máximo de horas es 8")
    private Integer horas;

    @NotNull(message = "El método de pago es obligatorio")
    private String metodoPago;
}