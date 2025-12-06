package com.sigc.backend.service;

import com.sigc.backend.dto.NotificacionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Servicio para enviar notificaciones en tiempo real vía WebSocket
 * 
 * Utiliza SimpMessagingTemplate para enviar mensajes a canales específicos
 * Los canales están segregados por usuario y rol para seguridad
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Envía una notificación a un usuario específico
     * Canal: /topic/user/{userId}
     * 
     * @param userId ID del usuario destinatario
     * @param notification Datos de la notificación
     */
    public void notifyUser(String userId, NotificacionDTO notification) {
        try {
            // Asegurar que el timestamp esté presente
            if (notification.getTimestamp() == null) {
                notification.setTimestamp(LocalDateTime.now());
            }
            
            // Asegurar que el destinatarioId esté presente
            notification.setDestinatarioId(userId);
            
            // Enviar al canal específico del usuario
            String destination = "/topic/user/" + userId;
            messagingTemplate.convertAndSend(destination, notification);
            
            log.info("📤 Notificación enviada a usuario {}: {} - {}", 
                userId, notification.getTipo(), notification.getTitulo());
        } catch (Exception e) {
            log.error("❌ Error enviando notificación a usuario {}: {}", userId, e.getMessage(), e);
        }
    }

    /**
     * Envía una notificación a todos los usuarios con rol ADMIN
     * Canal: /topic/admin
     * 
     * @param notification Datos de la notificación
     */
    public void notifyAdmins(NotificacionDTO notification) {
        try {
            if (notification.getTimestamp() == null) {
                notification.setTimestamp(LocalDateTime.now());
            }
            
            messagingTemplate.convertAndSend("/topic/admin", notification);
            
            log.info("📤 Notificación enviada a admins: {} - {}", 
                notification.getTipo(), notification.getTitulo());
        } catch (Exception e) {
            log.error("❌ Error enviando notificación a admins: {}", e.getMessage(), e);
        }
    }

    /**
     * Envía una notificación broadcast a todos los usuarios conectados
     * Canal: /topic/global
     * 
     * @param notification Datos de la notificación
     */
    public void notifyAll(NotificacionDTO notification) {
        try {
            if (notification.getTimestamp() == null) {
                notification.setTimestamp(LocalDateTime.now());
            }
            
            messagingTemplate.convertAndSend("/topic/global", notification);
            
            log.info("📤 Notificación broadcast enviada: {} - {}", 
                notification.getTipo(), notification.getTitulo());
        } catch (Exception e) {
            log.error("❌ Error enviando notificación broadcast: {}", e.getMessage(), e);
        }
    }

    /**
     * Notifica la creación de una nueva cita
     * Envía notificación al doctor y al paciente
     * 
     * @param citaId ID de la cita creada
     * @param doctorId ID del doctor asignado
     * @param pacienteId ID del paciente que solicitó
     * @param citaData Datos de la cita (CitaDTO)
     */
    public void notifyCitaCreada(Long citaId, String doctorId, String pacienteId, Object citaData) {
        // Notificación al doctor
        NotificacionDTO doctorNotif = NotificacionDTO.builder()
                .tipo("CITA_CREADA")
                .titulo("Nueva Cita Agendada")
                .mensaje("Un paciente ha agendado una cita contigo")
                .datos(citaData)
                .destinatarioId(doctorId)
                .prioridad("ALTA")
                .url("/citas/" + citaId)
                .requiereAccion(true)
                .timestamp(LocalDateTime.now())
                .build();
        
        notifyUser(doctorId, doctorNotif);
        
        // Notificación al paciente
        NotificacionDTO pacienteNotif = NotificacionDTO.builder()
                .tipo("CITA_CONFIRMADA")
                .titulo("Cita Confirmada")
                .mensaje("Tu cita ha sido confirmada exitosamente")
                .datos(citaData)
                .destinatarioId(pacienteId)
                .prioridad("MEDIA")
                .url("/mis-citas/" + citaId)
                .requiereAccion(false)
                .timestamp(LocalDateTime.now())
                .build();
        
        notifyUser(pacienteId, pacienteNotif);
        
        // Notificar a admins para monitoreo
        NotificacionDTO adminNotif = NotificacionDTO.builder()
                .tipo("CITA_CREADA")
                .titulo("Nueva Cita en el Sistema")
                .mensaje("Se ha creado una nueva cita")
                .datos(citaData)
                .prioridad("BAJA")
                .url("/admin/citas/" + citaId)
                .requiereAccion(false)
                .timestamp(LocalDateTime.now())
                .build();
        
        notifyAdmins(adminNotif);
        
        log.info("📅 Notificaciones de cita creada enviadas (ID: {})", citaId);
    }

    /**
     * Notifica la actualización de una cita
     * 
     * @param citaId ID de la cita actualizada
     * @param doctorId ID del doctor
     * @param pacienteId ID del paciente
     * @param estado Nuevo estado de la cita
     * @param citaData Datos actualizados de la cita
     */
    public void notifyCitaActualizada(Long citaId, String doctorId, String pacienteId, String estado, Object citaData) {
        String titulo;
        String mensaje;
        String prioridad = "MEDIA";
        
        // Determinar mensaje según el estado
        switch (estado.toUpperCase()) {
            case "CONFIRMADA":
                titulo = "Cita Confirmada";
                mensaje = "Tu cita ha sido confirmada";
                break;
            case "CANCELADA":
                titulo = "Cita Cancelada";
                mensaje = "Tu cita ha sido cancelada";
                prioridad = "ALTA";
                break;
            case "COMPLETADA":
                titulo = "Cita Completada";
                mensaje = "Tu cita ha sido completada";
                break;
            default:
                titulo = "Cita Actualizada";
                mensaje = "El estado de tu cita ha cambiado a " + estado;
        }
        
        // Notificación al doctor
        NotificacionDTO doctorNotif = NotificacionDTO.builder()
                .tipo("CITA_ACTUALIZADA")
                .titulo(titulo)
                .mensaje(mensaje)
                .datos(citaData)
                .destinatarioId(doctorId)
                .prioridad(prioridad)
                .url("/citas/" + citaId)
                .requiereAccion(false)
                .timestamp(LocalDateTime.now())
                .build();
        
        notifyUser(doctorId, doctorNotif);
        
        // Notificación al paciente
        NotificacionDTO pacienteNotif = NotificacionDTO.builder()
                .tipo("CITA_ACTUALIZADA")
                .titulo(titulo)
                .mensaje(mensaje)
                .datos(citaData)
                .destinatarioId(pacienteId)
                .prioridad(prioridad)
                .url("/mis-citas/" + citaId)
                .requiereAccion(estado.equalsIgnoreCase("CANCELADA"))
                .timestamp(LocalDateTime.now())
                .build();
        
        notifyUser(pacienteId, pacienteNotif);
        
        log.info("📅 Notificaciones de cita actualizada enviadas (ID: {}, Estado: {})", citaId, estado);
    }

    /**
     * Notifica cambios en horarios de doctores
     * Afecta a pacientes con citas agendadas
     * 
     * @param doctorId ID del doctor
     * @param mensaje Descripción del cambio
     * @param data Datos del horario
     */
    public void notifyHorarioChange(String doctorId, String mensaje, Object data) {
        NotificacionDTO notification = NotificacionDTO.builder()
                .tipo("HORARIO_ACTUALIZADO")
                .titulo("Cambio en Disponibilidad")
                .mensaje(mensaje)
                .datos(data)
                .prioridad("MEDIA")
                .url("/horarios")
                .requiereAccion(false)
                .timestamp(LocalDateTime.now())
                .build();
        
        // Enviar a canal de horarios para que frontend notifique a afectados
        Object notificationObj = Objects.requireNonNull(notification, "Notification cannot be null");
        messagingTemplate.convertAndSend("/topic/horarios", notificationObj);
        
        log.info("🕒 Notificación de cambio de horario enviada (Doctor: {})", doctorId);
    }

    /**
     * Notifica cambios en información de doctores
     * 
     * @param doctorId ID del doctor
     * @param mensaje Descripción del cambio
     * @param data Datos del doctor
     */
    public void notifyDoctorUpdate(String doctorId, String mensaje, Object data) {
        NotificacionDTO notification = NotificacionDTO.builder()
                .tipo("DOCTOR_ACTUALIZADO")
                .titulo("Información de Doctor Actualizada")
                .mensaje(mensaje)
                .datos(data)
                .prioridad("BAJA")
                .url("/doctores/" + doctorId)
                .requiereAccion(false)
                .timestamp(LocalDateTime.now())
                .build();
        
        // Notificar a admins
        notifyAdmins(notification);
        
        log.info("👨‍⚕️ Notificación de actualización de doctor enviada (ID: {})", doctorId);
    }
}
