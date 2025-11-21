package com.sigc.backend.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Modelo de dominio para Especialidad
 * Representa una especialidad médica sin dependencias de infraestructura
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Especialidad {
    
    private Long idEspecialidad;
    private String nombre;
    private String descripcion;
    private String imagen;
    
    /**
     * Valida que la especialidad tenga datos mínimos requeridos
     */
    public boolean isValid() {
        return nombre != null && !nombre.trim().isEmpty();
    }
}
