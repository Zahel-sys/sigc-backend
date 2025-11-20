package com.sigc.backend.domain.model;

import java.time.LocalDateTime;

/**
 * Entidad de dominio: Cita
 * 
 * Responsabilidad única: Representar una cita médica.
 * 
 * Atributos de la cita:
 * - id: Identificador único
 * - usuarioId: ID del usuario que solicita la cita
 * - doctorId: ID del doctor que atiende
 * - fecha: Fecha y hora de la cita
 * - descripcion: Motivo/descripción de la consulta
 * - estado: Estado de la cita (PROGRAMADA, CONFIRMADA, CANCELADA, REALIZADA)
 * - observaciones: Notas del doctor
 * - createdAt: Fecha de creación
 * - updatedAt: Fecha de última actualización
 * 
 * Principio DDD: Entidad que representa una cita médica
 * Principio SRP: Solo representa una cita
 */
public class Cita {
    
    private Long id;
    private Long usuarioId;
    private Long doctorId;
    private LocalDateTime fecha;
    private String descripcion;
    private String estado;
    private String observaciones;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructor por defecto
    public Cita() {}
    
    // Constructor completo
    public Cita(Long id, Long usuarioId, Long doctorId, LocalDateTime fecha,
                String descripcion, String estado, String observaciones,
                LocalDateTime createdAt, LocalDateTime updatedAt) {
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
    
    // Constructor simplificado (para creación rápida)
    public Cita(Long usuarioId, Long doctorId, LocalDateTime fecha, String descripcion) {
        this.usuarioId = usuarioId;
        this.doctorId = doctorId;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.estado = "PROGRAMADA";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters
    public Long getId() { return id; }
    public Long getUsuarioId() { return usuarioId; }
    public Long getDoctorId() { return doctorId; }
    public LocalDateTime getFecha() { return fecha; }
    public String getDescripcion() { return descripcion; }
    public String getEstado() { return estado; }
    public String getObservaciones() { return observaciones; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    
    // Setters
    public void setId(Long id) { this.id = id; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    @Override
    public String toString() {
        return "Cita{" +
                "id=" + id +
                ", usuarioId=" + usuarioId +
                ", doctorId=" + doctorId +
                ", fecha=" + fecha +
                ", descripcion='" + descripcion + '\'' +
                ", estado='" + estado + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
