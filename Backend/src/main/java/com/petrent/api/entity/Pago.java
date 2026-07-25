package com.petrent.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;


@Entity
@Table(name = "pagos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_reserva", nullable = false, unique = true)
    private Reserva reserva;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal monto;

    @Column(name = "metodo_pago", nullable = false, length = 20)
    private String metodoPago;

    @Column(name = "estado_pago", nullable = false, length = 20)
    @Builder.Default
    private String estadoPago = "PENDIENTE";

    @Column(name = "referencia_transaccion", length = 100)
    private String referenciaTransaccion;

    @Column(name = "fecha_pago", nullable = false, updatable = false)
    private OffsetDateTime fechaPago;

    @Column(name = "fecha_actualizacion", nullable = false)
    private OffsetDateTime fechaActualizacion;

    @PrePersist
    protected void alCrear() {
        if (fechaPago == null) {
            fechaPago = OffsetDateTime.now(ZoneOffset.UTC);
        }
        fechaActualizacion = OffsetDateTime.now(ZoneOffset.UTC);
    }

    @PreUpdate
    protected void alActualizar() {
        fechaActualizacion = OffsetDateTime.now(ZoneOffset.UTC);
    }
}
