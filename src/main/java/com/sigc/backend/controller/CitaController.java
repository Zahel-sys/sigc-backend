package com.sigc.backend.controller;

import com.sigc.backend.security.JwtUtil;
import com.sigc.backend.application.service.AppointmentApplicationService;
import com.sigc.backend.domain.service.usecase.appointment.CreateAppointmentRequest;
import com.sigc.backend.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/citas")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175"})
@Tag(name = "Citas", description = "Gestión de citas médicas")
@SecurityRequirement(name = "JWT")
public class CitaController {

    @Autowired private JwtUtil jwtUtil;
    @Autowired private AppointmentApplicationService appointmentApplicationService;
    @Autowired private NotificationService notificationService;

    @GetMapping
    @Operation(summary = "Listar todas las citas", description = "Obtiene la lista completa de citas registradas")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de citas obtenida exitosamente"),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public List<com.sigc.backend.application.mapper.CitaMapper.CitaDTO> listar() {
        try {
            log.info("Listando todas las citas");
            var dtos = appointmentApplicationService.getAllAppointments();
            log.info("Se encontraron {} citas", dtos.size());
            return dtos;
        } catch (Exception e) {
            log.error("Error al listar citas: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @GetMapping("/usuario/{idUsuario}")
    @Operation(summary = "Listar citas por usuario", description = "Obtiene todas las citas de un usuario específico")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Citas del usuario obtenidas exitosamente"),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public List<com.sigc.backend.application.mapper.CitaMapper.CitaDTO> listarPorUsuario(@PathVariable Long idUsuario) {
        try {
            log.info("Listando citas del usuario ID: {}", idUsuario);
            var dtos = appointmentApplicationService.getAppointmentsByUsuario(idUsuario);
            log.info("Usuario {} tiene {} citas", idUsuario, dtos.size());
            return dtos;
        } catch (Exception e) {
            log.error("Error al listar citas del usuario {}: {}", idUsuario, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * Endpoint POST para crear/reservar citas médicas
     * 
     * Headers requeridos:
     *   Authorization: Bearer {token_jwt}
     * 
     * Body:
     * {
     *   "usuario": { "idUsuario": 1 },
     *   "horario": { "idHorario": 1 }
     * }
     * 
     * O alternativamente:
     * {
     *   "paciente": { "idUsuario": 1 },
     *   "horario": { "idHorario": 1 }
     * }
     */
    @PostMapping
        public ResponseEntity<?> crear(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody CreateByHorarioRequest requestBody) {
        try {
            log.info("📝 Recibiendo petición para crear nueva cita");
            log.info("Datos recibidos: {}", requestBody);
            
            // ✅ VALIDACION 1: Verificar autenticación (token JWT)
            if (authHeader == null || authHeader.isEmpty()) {
                log.warn("⚠️ Falta header Authorization");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(crearError("Token JWT requerido en header Authorization"));
            }
            
            String token = authHeader.startsWith("Bearer ") 
                    ? authHeader.substring(7) 
                    : authHeader;
            
            if (!jwtUtil.validateToken(token)) {
                log.warn("❌ Token JWT inválido o expirado");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(crearError("Token JWT inválido o expirado"));
            }
            
            // Extraer idUsuario autenticado
            Long idUsuario = jwtUtil.getIdUsuarioFromToken(token);
            if (idUsuario == null) {
                log.warn("❌ No se pudo extraer el ID del usuario del token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(crearError("Token inválido"));
            }

            try {
                // Construir request para el caso de uso
                var createReq = new CreateAppointmentRequest(
                        requestBody.getDate(),
                        requestBody.getDescription(),
                        requestBody.getDoctorId(),
                        idUsuario
                );
                var resp = appointmentApplicationService.createAppointment(createReq);
                
                // 🔔 Enviar notificaciones en tiempo real
                try {
                    notificationService.notifyCitaCreada(
                        resp.getAppointmentId(),
                        String.valueOf(resp.getDoctorId()),
                        String.valueOf(resp.getUsuarioId()),
                        resp
                    );
                    log.info("✅ Notificaciones WebSocket enviadas para cita ID: {}", resp.getAppointmentId());
                } catch (Exception notifEx) {
                    log.warn("⚠️ Error enviando notificación WebSocket (cita creada): {}", notifEx.getMessage());
                    // No fallar la request si falla la notificación
                }
                
                return ResponseEntity.status(HttpStatus.CREATED).body(resp);
            } catch (RuntimeException ex) {
                String m = ex.getMessage();
                if ("PACIENTE_NO_ENCONTRADO".equals(m) || "HORARIO_NO_ENCONTRADO".equals(m)) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(crearError(m));
                } else if ("HORARIO_NO_DISPONIBLE".equals(m) || "CITA_DUPLICADA".equals(m)) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(crearError(m));
                } else if ("HORARIO_EN_EL_PASADO".equals(m)) {
                    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(crearError(m));
                } else {
                    log.error("❌ Error de validación al crear cita: {}", ex.getMessage());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(crearError("Error: " + ex.getMessage()));
                }
            }
            
        } catch (RuntimeException e) {
            String mensaje = e.getMessage();
            
            if (mensaje.equals("PACIENTE_NO_ENCONTRADO")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(crearError("Paciente no encontrado"));
            } else if (mensaje.equals("HORARIO_NO_ENCONTRADO")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(crearError("Horario no encontrado"));
            } else {
                log.error("❌ Error de validación al crear cita: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(crearError("Error: " + e.getMessage()));
            }
        } catch (Exception e) {
            log.error("❌ Error inesperado al crear cita: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearError("Error interno al crear la cita"));
        }
    }
    
    /**
     * Método auxiliar para crear respuestas de error estándar
     */
    private Map<String, Object> crearError(String mensaje) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", mensaje);
        error.put("timestamp", LocalDateTime.now());
        return error;
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<String> cancelar(@PathVariable Long id) {
        try {
            log.info("Cancelando cita ID: {}", id);
            if (id == null) {
                return ResponseEntity.badRequest().body("ID inválido");
            }
            
            // Obtener datos de la cita antes de cancelar
            var cita = appointmentApplicationService.getAllAppointments().stream()
                    .filter(c -> c.getId().equals(id))
                    .findFirst()
                    .orElse(null);
            
            appointmentApplicationService.cancel(id);
            log.info("Cita {} cancelada correctamente", id);
            
            // 🔔 Enviar notificaciones de cancelación
            if (cita != null) {
                try {
                    notificationService.notifyCitaActualizada(
                        id,
                        String.valueOf(cita.getDoctorId()),
                        String.valueOf(cita.getUsuarioId()),
                        "CANCELADA",
                        cita
                    );
                    log.info("✅ Notificaciones de cancelación enviadas para cita ID: {}", id);
                } catch (Exception notifEx) {
                    log.warn("⚠️ Error enviando notificación WebSocket (cita cancelada): {}", notifEx.getMessage());
                }
            }
            
            return ResponseEntity.ok("Cita cancelada correctamente");
        } catch (Exception e) {
            log.error("Error al cancelar cita {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al cancelar la cita");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            log.info("Eliminando cita ID: {}", id);
            if (id != null) {
                appointmentApplicationService.delete(id);
                log.info("Cita {} eliminada exitosamente", id);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.badRequest().body("ID inválido");
            }
        } catch (Exception e) {
            log.error("Error al eliminar cita {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar la cita");
        }
    }

    /**
     * DTO para crear cita via payload minimal: date, doctorId y description.
     */
    public static class CreateByHorarioRequest {
        private java.time.LocalDateTime date;
        private String description;
        private Long doctorId;

        public CreateByHorarioRequest() {}

        public java.time.LocalDateTime getDate() { return date; }
        public String getDescription() { return description; }
        public Long getDoctorId() { return doctorId; }

        public void setDate(java.time.LocalDateTime date) { this.date = date; }
        public void setDescription(String description) { this.description = description; }
        public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }
    }
}
