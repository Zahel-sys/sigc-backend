package com.sigc.backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Propiedades de configuración CORS
 * Mapea las propiedades desde application.properties
 */
@Component
@ConfigurationProperties(prefix = "cors")
@Getter
@Setter
public class CorsProperties {
    
    /**
     * Orígenes permitidos para CORS
     */
    private String allowedOrigins = "http://localhost:5173,http://localhost:5174,http://localhost:5175";
}
