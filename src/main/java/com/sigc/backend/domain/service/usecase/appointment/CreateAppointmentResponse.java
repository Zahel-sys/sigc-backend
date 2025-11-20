package com.sigc.backend.domain.service.usecase.appointment;

import java.time.LocalDateTime;

/**
 * DTO: Respuesta de Crear Cita.
 * Transporta datos desde el use case hacia la salida (HTTP).
 */
public class CreateAppointmentResponse {
    private final Long appointmentId;
    private final LocalDateTime date;
    private final String description;
    private final Long doctorId;
    private final Long usuarioId;
    private final String status;
    
    public CreateAppointmentResponse(Long appointmentId, LocalDateTime date, String description, 
                                     Long doctorId, Long usuarioId, String status) {
        this.appointmentId = appointmentId;
        this.date = date;
        this.description = description;
        this.doctorId = doctorId;
        this.usuarioId = usuarioId;
        this.status = status;
    }
    
    public Long getAppointmentId() {
        return appointmentId;
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
    
    public String getStatus() {
        return status;
    }
}
