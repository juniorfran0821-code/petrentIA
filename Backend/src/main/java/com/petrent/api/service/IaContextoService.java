package com.petrent.api.service;

import com.petrent.api.repository.PerroRepository;
import com.petrent.api.repository.ReservaRepository;
import com.petrent.api.repository.ResenaRepository;
import com.petrent.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class IaContextoService {

    private final UsuarioRepository usuarioRepository;
    private final PerroRepository   perroRepository;
    private final ReservaRepository reservaRepository;
    private final ResenaRepository  resenaRepository;

    @Transactional(readOnly = true)
    public String construirContexto() {
        log.debug("Construyendo contexto dinámico para Ollama...");

        long totalUsuarios = usuarioRepository.count();
        long totalAdmins   = usuarioRepository.countByRol("admin");
        long totalClientes = usuarioRepository.countByRol("usuario");

        
        long totalPerros       = perroRepository.count();
        long perrosDisponibles = perroRepository.countByActivoTrueAndDisponibleTrue();
        long perrosAlquilados  = perroRepository.countByActivoTrueAndDisponibleFalse();

        String precioMin = "N/D", precioMax = "N/D", precioPromedio = "N/D";
        try {
            List<Object[]> est = perroRepository.findEstadisticasPrecios();
            if (!est.isEmpty() && est.get(0)[0] != null) {
                Object[] f = est.get(0);
                precioMin      = "$" + ((BigDecimal) f[0]).setScale(2, RoundingMode.HALF_UP);
                precioMax      = "$" + ((BigDecimal) f[1]).setScale(2, RoundingMode.HALF_UP);
                precioPromedio = "$" + ((BigDecimal) f[2]).setScale(2, RoundingMode.HALF_UP);
            }
        } catch (Exception e) {
            log.warn("No se pudieron obtener estadísticas de precios: {}", e.getMessage());
        }

        StringBuilder listaPerros = new StringBuilder();
        try {
            List<Object[]> perros = perroRepository.findResumenPerrosActivos();
            int limite = Math.min(perros.size(), 20);
            for (int i = 0; i < limite; i++) {
                Object[] p = perros.get(i);
                listaPerros.append(String.format("  %s | %s | %s | %s/h | %s%n",
                        p[0],
                        p[1],
                        p[2],
                        ((BigDecimal) p[3]).setScale(2, RoundingMode.HALF_UP),
                        Boolean.TRUE.equals(p[4]) ? "disponible" : "alquilado"
                ));
            }
            if (perros.size() > 20) {
                listaPerros.append(String.format("  ... y %d más%n", perros.size() - 20));
            }
        } catch (Exception e) {
            log.warn("No se pudo obtener la lista de perros: {}", e.getMessage());
            listaPerros.append("  (no disponible)\n");
        }

        long confirmadas = reservaRepository.countByEstado("CONFIRMADA");
        long completadas = reservaRepository.countByEstado("COMPLETADA");
        long canceladas  = reservaRepository.countByEstado("CANCELADA");
        long totalReservas = confirmadas + completadas + canceladas;

        String ingresos = "$0.00";
        try {
            ingresos = "$" + reservaRepository.sumIngresosTotales()
                               .setScale(2, RoundingMode.HALF_UP);
        } catch (Exception e) {
            log.warn("No se pudieron obtener los ingresos: {}", e.getMessage());
        }

        long totalResenas = resenaRepository.count();


        return "Eres el asistente de PetRent (plataforma de alquiler de perros). "
             + "Responde SIEMPRE en español. "
             + "Cuando te pregunten por cantidades, usa EXACTAMENTE los números de abajo sin cambiarlos.\n\n"
             + "CIFRAS EXACTAS DEL SISTEMA (fecha: " + LocalDate.now() + "):\n"
             + "- Usuarios activos: " + totalUsuarios
                 + " (clientes=" + totalClientes + ", admins=" + totalAdmins + ")\n"
             + "- Perros en catálogo: " + totalPerros
                 + " (disponibles=" + perrosDisponibles + ", alquilados=" + perrosAlquilados + ")\n"
             + "- Precio/hora: min=" + precioMin + " max=" + precioMax + " promedio=" + precioPromedio + "\n"
             + "- Reservas: total=" + totalReservas
                 + " (confirmadas=" + confirmadas + ", completadas=" + completadas
                 + ", canceladas=" + canceladas + ")\n"
             + "- Ingresos (completadas): " + ingresos + "\n"
             + "- Reseñas: " + totalResenas + "\n\n"
             + "PERROS (nombre | raza | tamaño | precio/h | estado):\n"
             + listaPerros
             + "\nPuedes responder cualquier pregunta, no solo sobre PetRent. Siempre en español.";
    }
}
