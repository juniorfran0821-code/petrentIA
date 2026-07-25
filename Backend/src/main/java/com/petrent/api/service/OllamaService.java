package com.petrent.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petrent.api.dto.IaConsultaResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;


@Slf4j
@Service
@RequiredArgsConstructor
public class OllamaService {

    @Value("${ollama.url}")
    private String ollamaUrl;

    @Value("${ollama.modelo}")
    private String modelo;

    private final IaContextoService iaContextoService;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    public IaConsultaResponse consultar(String prompt) {
        log.info("Enviando consulta a Ollama [modelo={}]: '{}'", modelo,
                prompt.length() > 80 ? prompt.substring(0, 80) + "..." : prompt);

        String systemPrompt = iaContextoService.construirContexto();

        String cuerpoJson;
        try {
            cuerpoJson = objectMapper.writeValueAsString(
                    new OllamaGenerateRequest(modelo, systemPrompt, prompt, false)
            );
        } catch (Exception e) {
            log.error("Error al serializar la petición para Ollama", e);
            throw new RuntimeException("Error interno al preparar la consulta al modelo de IA");
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ollamaUrl + "/api/generate"))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(120)) 
                .POST(HttpRequest.BodyPublishers.ofString(cuerpoJson))
                .build();

        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            log.error("No se pudo conectar con Ollama en {}", ollamaUrl, e);
            throw new RuntimeException(
                    "No se pudo conectar con el servicio de IA. " +
                    "Verifica que Ollama esté corriendo en el puerto 11434."
            );
        }

        if (response.statusCode() != 200) {
            log.error("Ollama respondió con status {} — cuerpo: {}",
                    response.statusCode(), response.body());
            throw new RuntimeException(
                    "El servicio de IA respondió con un error (HTTP " + response.statusCode() + ")."
            );
        }

        try {
            JsonNode nodo = objectMapper.readTree(response.body());
            String textoRespuesta = nodo.path("response").asText();
            String modeloUsado    = nodo.path("model").asText(modelo);

            log.info("Respuesta recibida de Ollama [modelo={}] ({} chars)",
                    modeloUsado, textoRespuesta.length());

            return IaConsultaResponse.builder()
                    .respuesta(textoRespuesta)
                    .modelo(modeloUsado)
                    .build();

        } catch (Exception e) {
            log.error("Error al parsear la respuesta de Ollama: {}", response.body(), e);
            throw new RuntimeException("Error al procesar la respuesta del modelo de IA.");
        }
    }

    private record OllamaGenerateRequest(String model, String system, String prompt, boolean stream) {}
}
