package com.petrent.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioRolRequest {

    @NotBlank(message = "El rol es obligatorio")
    @Pattern(regexp = "admin|usuario", message = "El rol debe ser admin o usuario")
    private String rol;
}
