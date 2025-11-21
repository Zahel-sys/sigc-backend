package com.sigc.backend.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Modelo de dominio para Servicio
 * Representa un servicio médico sin dependencias de infraestructura
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Servicio {
    
    private Long idServicio;
    private String nombreServicio;
    private String descripcion;
    private int duracionMinutos;
    private double precio;
    
    /**
     * Valida que el servicio tenga datos mínimos requeridos
     */
    public boolean isValid() {
        return nombreServicio != null && !nombreServicio.trim().isEmpty()
            && duracionMinutos > 0
            && precio >= 0;
    }
}
