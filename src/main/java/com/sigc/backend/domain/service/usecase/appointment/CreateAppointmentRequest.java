package com.sigc.backend.domain.service.usecase.appointment;

import java.time.LocalDateTime;

/**
 * DTO: Solicitud de Crear Cita.
 * Transporta datos desde la entrada (HTTP) hacia el use case.
 */
public class CreateAppointmentRequest {
    private final LocalDateTime date;
    private final String description;
    private final Long doctorId;
    private final Long usuarioId;
    
    public CreateAppointmentRequest(LocalDateTime date, String description, Long doctorId, Long usuarioId) {
        this.date = date;
        this.description = description;
        this.doctorId = doctorId;
        this.usuarioId = usuarioId;
    }
    
    public LocalDateTime getDate() {
        return date;
    }
    
    public String getDescription() {
        return description;
    }
    
    public Long getDoctorId() {
        return doctorId;
    }
    
    public Long getUsuarioId() {
        return usuarioId;
    }
}
