package com.sigc.backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Propiedades personalizadas de la aplicación
 * Mapea las propiedades desde application.properties
 */
@Component
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class AppProperties {
    
    /**
     * Directorio base para uploads de archivos
     */
    private String uploadDir = "uploads/";
    
    /**
     * Nombre de la aplicación
     */
    private String name = "SIGC Backend";
    
    /**
     * Versión de la aplicación
     */
    private String version = "1.0.0";
}
