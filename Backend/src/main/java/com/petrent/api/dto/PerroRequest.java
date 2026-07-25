package com.petrent.api.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PerroRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 60)
    private String nombre;

    @NotBlank(message = "La raza es obligatoria")
    @Size(max = 80)
    private String raza;

    @NotBlank(message = "El tamaño es obligatorio")
    @Pattern(regexp = "Pequeño|Mediano|Grande", message = "El tamaño debe ser Pequeño, Mediano o Grande")
    private String tamano;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    private BigDecimal precio;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 500)
    private String descripcion;

    private String etiquetas;

    private String imagen;
}
