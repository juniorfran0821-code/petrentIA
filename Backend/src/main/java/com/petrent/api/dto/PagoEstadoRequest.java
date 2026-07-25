package com.petrent.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PagoEstadoRequest {

    @NotBlank(message = "El estado del pago es obligatorio")
    @Pattern(regexp = "PENDIENTE|PAGADO|REEMBOLSADO", message = "Estado de pago inválido")
    private String estadoPago;

    private String referenciaTransaccion;
}
