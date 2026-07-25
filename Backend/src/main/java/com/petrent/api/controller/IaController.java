package com.petrent.api.controller;

import com.petrent.api.dto.IaConsultaRequest;
import com.petrent.api.dto.IaConsultaResponse;
import com.petrent.api.service.OllamaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ia")
@RequiredArgsConstructor
@Tag(name = "IA – Asistente", description = "Consultas al modelo de lenguaje local (Ollama)")
public class IaController {

    private final OllamaService ollamaService;
    
    @PostMapping("/consulta")
    @Operation(
        summary = "Consultar al asistente de IA",
        description = "Envía una pregunta al modelo de lenguaje local (Ollama) y devuelve la respuesta generada. "
                    + "Útil para recomendar razas de perros, resolver dudas sobre el alquiler, "
                    + "calcular costos o cualquier consulta relacionada con la aplicación."
    )
    public ResponseEntity<IaConsultaResponse> consultar(
            @Valid @RequestBody IaConsultaRequest request) {

        IaConsultaResponse respuesta = ollamaService.consultar(request.getConsulta());
        return ResponseEntity.ok(respuesta);
    }
}
