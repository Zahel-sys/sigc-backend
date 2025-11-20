package com.sigc.backend.application.mapper;

import com.sigc.backend.domain.model.Cita;
import com.sigc.backend.domain.service.usecase.appointment.CreateAppointmentRequest;
import com.sigc.backend.domain.service.usecase.appointment.CreateAppointmentResponse;

/**
 * Mapper: Cita (modelo de dominio) -> DTOs de salida
 * 
 * Responsabilidad única: Convertir entre Cita y DTOs
 * 
 * Principios aplicados:
 * - SRP: Solo mapea
 * - DIP: No depende de Spring
 */
public class CitaMapper {
    
    /**
     * Convierte CreateAppointmentRequest a Cita (modelo de dominio).
     * 
     * @param request Request de creación de cita
     * @return Cita del dominio
     */
    public static Cita toDomain(CreateAppointmentRequest request) {
        return new Cita(
            request.getUsuarioId(),
            request.getDoctorId(),
            request.getDate(),
            request.getDescription()
        );
    }
    
    /**
     * Convierte Cita a CreateAppointmentResponse.
     * 
     * @param cita Cita del dominio
     * @return CreateAppointmentResponse
     */
    public static CreateAppointmentResponse toResponse(Cita cita) {
        return new CreateAppointmentResponse(
            cita.getId(),
            cita.getFecha(),
            cita.getDescripcion(),
            cita.getDoctorId(),
            cita.getUsuarioId(),
            cita.getEstado()
        );
    }
    
    /**
     * Convierte Cita a un DTO simple de cita.
     * 
     * @param cita Cita del dominio
     * @return CitaDTO
     */
    public static CitaDTO toDTO(Cita cita) {
        return new CitaDTO(
            cita.getId(),
            cita.getUsuarioId(),
            cita.getDoctorId(),
            cita.getFecha(),
            cita.getDescripcion(),
            cita.getEstado(),
            cita.getObservaciones(),
            cita.getCreatedAt(),
            cita.getUpdatedAt()
        );
    }
    
    /**
     * DTO simple de cita.
     */
    public static class CitaDTO {
        private final Long id;
        private final Long usuarioId;
        private final Long doctorId;
        private final java.time.LocalDateTime fecha;
        private final String descripcion;
        private final String estado;
        private final String observaciones;
        private final java.time.LocalDateTime createdAt;
        private final java.time.LocalDateTime updatedAt;
        
        public CitaDTO(Long id, Long usuarioId, Long doctorId, 
                      java.time.LocalDateTime fecha, String descripcion,
                      String estado, String observaciones,
                      java.time.LocalDateTime createdAt, java.time.LocalDateTime updatedAt) {
            this.id = id;
            this.usuarioId = usuarioId;
            this.doctorId = doctorId;
            this.fecha = fecha;
            this.descripcion = descripcion;
            this.estado = estado;
            this.observaciones = observaciones;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }
        
        public Long getId() { return id; }
        public Long getUsuarioId() { return usuarioId; }
        public Long getDoctorId() { return doctorId; }
        public java.time.LocalDateTime getFecha() { return fecha; }
        public String getDescripcion() { return descripcion; }
        public String getEstado() { return estado; }
        public String getObservaciones() { return observaciones; }
        public java.time.LocalDateTime getCreatedAt() { return createdAt; }
        public java.time.LocalDateTime getUpdatedAt() { return updatedAt; }
    }
}
