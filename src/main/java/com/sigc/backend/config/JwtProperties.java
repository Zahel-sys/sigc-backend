package com.sigc.backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Propiedades de configuración JWT
 * Mapea las propiedades desde application.properties
 */
@Component
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtProperties {
    
    /**
     * Clave secreta para firmar tokens JWT
     */
    private String secret = "sigc-secret-key-2025-development-only-change-in-production";
    
    /**
     * Tiempo de expiración del token en milisegundos (por defecto 24 horas)
     */
    private Long expiration = 86400000L;
}
