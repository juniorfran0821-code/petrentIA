package com.petrent.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IaConsultaRequest {

    @NotBlank(message = "La consulta no puede estar vacía")
    @Size(max = 1000, message = "La consulta no puede superar los 1000 caracteres")
    private String consulta;
}
