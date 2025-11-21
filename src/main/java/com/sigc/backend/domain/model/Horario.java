package com.sigc.backend.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Modelo de dominio para Horario
 * Representa un horario de atención sin dependencias de infraestructura
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Horario {
    
    private Long idHorario;
    private LocalDate fecha;
    private String turno;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private boolean disponible;
    private Long idDoctor;
    
    /**
     * Valida que el horario tenga datos mínimos requeridos
     */
    public boolean isValid() {
        return fecha != null && !fecha.isBefore(LocalDate.now())
            && turno != null && turno.matches("^(Mañana|Tarde|Noche)$")
            && horaInicio != null && horaFin != null
            && horaInicio.isBefore(horaFin)
            && idDoctor != null;
    }
    
    /**
     * Verifica si el horario es futuro
     */
    public boolean esFuturo() {
        return fecha.isAfter(LocalDate.now());
    }
}
