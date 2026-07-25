package com.petrent.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PerroDisponibilidadRequest {

    @NotNull(message = "El campo disponible es obligatorio")
    private Boolean disponible;
}