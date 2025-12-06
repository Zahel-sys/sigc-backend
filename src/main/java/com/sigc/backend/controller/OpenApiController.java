package com.sigc.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yaml.snakeyaml.Yaml;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

@Controller
public class OpenApiController {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Yaml yaml = new Yaml();
    private String cachedJsonSpec;

    /**
     * Endpoint que sirve la especificación OpenAPI en formato JSON
     * Requerido por Swagger UI
     */
    @GetMapping("/v3/api-docs")
    @ResponseBody
    public ResponseEntity<String> getApiDocs() {
        try {
            // Usar caché para mejorar rendimiento
            if (cachedJsonSpec != null) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(cachedJsonSpec);
            }

            // Leer el archivo openapi.yml
            String yamlContent = Files.readString(Paths.get("openapi.yml"), StandardCharsets.UTF_8);
            
            // Convertir YAML a JSON
            @SuppressWarnings("unchecked")
            Map<String, Object> yamlMap = (Map<String, Object>) yaml.load(yamlContent);
            cachedJsonSpec = objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(yamlMap);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(cachedJsonSpec);
        } catch (IOException e) {
            return ResponseEntity.status(500)
                    .body("{\"error\": \"No se pudo leer el archivo OpenAPI: " + e.getMessage() + "\"}");
        }
    }

    /**
     * Endpoint para redirigir a Swagger UI
     */
    @GetMapping("/swagger-ui.html")
    public String swaggerUi() {
        return "redirect:/swagger-ui/index.html";
    }
}
