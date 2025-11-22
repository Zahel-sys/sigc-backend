package com.sigc.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para notificaciones WebSocket
 * Usado para enviar notificaciones en tiempo real a los usuarios
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionDTO {
    
    /**
     * Tipo de notificación
     * Ejemplos: "CITA_CREADA", "CITA_ACTUALIZADA", "CITA_CANCELADA", 
     *           "HORARIO_CAMBIADO", "DOCTOR_DISPONIBLE"
     */
    private String tipo;
    
    /**
     * Título de la notificación (corto, para mostrar en toast)
     */
    private String titulo;
    
    /**
     * Mensaje descriptivo de la notificación
     */
    private String mensaje;
    
    /**
     * Datos adicionales relacionados con la notificación
     * Puede ser CitaDTO, HorarioDTO, DoctorDTO, etc.
     */
    private Object datos;
    
    /**
     * Timestamp de cuando se generó la notificación
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    
    /**
     * ID del usuario destinatario (opcional)
     */
    private String destinatarioId;
    
    /**
     * Prioridad de la notificación
     * Valores: "ALTA", "MEDIA", "BAJA"
     */
    @Builder.Default
    private String prioridad = "MEDIA";
    
    /**
     * URL a la que redirigir cuando se haga clic en la notificación
     */
    private String url;
    
    /**
     * Indica si la notificación requiere acción del usuario
     */
    @Builder.Default
    private boolean requiereAccion = false;
}
