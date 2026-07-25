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
public class PagoResponse {
    private Long id;
    private Long idReserva;
    private BigDecimal monto;
    private String metodoPago;
    private String estadoPago;
    private String referenciaTransaccion;
    private OffsetDateTime fechaPago;
    private OffsetDateTime fechaActualizacion;
}