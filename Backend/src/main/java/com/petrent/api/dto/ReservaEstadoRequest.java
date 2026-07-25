package com.petrent.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservaEstadoRequest {

    @NotBlank(message = "El estado es obligatorio")
    @Pattern(regexp = "CONFIRMADA|CANCELADA|COMPLETADA", message = "Estado inválido")
    private String estado;
}
