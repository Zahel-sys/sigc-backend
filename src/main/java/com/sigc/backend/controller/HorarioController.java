package com.sigc.backend.controller;

import com.sigc.backend.application.mapper.HorarioMapper;
import com.sigc.backend.application.service.HorarioApplicationService;
import com.sigc.backend.domain.model.Horario;
import com.sigc.backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.lang.NonNull;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/horarios")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175"})
@RequiredArgsConstructor
public class HorarioController {

    private final HorarioApplicationService horarioApplicationService;
    private final HorarioMapper horarioMapper;
    private final NotificationService notificationService;

    @GetMapping
    public List<com.sigc.backend.model.Horario> listar() {
        try {
            log.info("Listando todos los horarios");
            List<Horario> horarios = horarioApplicationService.getAllHorarios();
            log.info("Se encontraron {} horarios", horarios.size());
            return horarios.stream()
                    .map(horarioMapper::toJpaEntity)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error al listar horarios: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @GetMapping("/doctor/{idDoctor}")
    public List<com.sigc.backend.model.Horario> listarPorDoctor(@PathVariable Long idDoctor) {
        try {
            log.info("Listando horarios disponibles del doctor ID: {}", idDoctor);
            List<Horario> horarios = horarioApplicationService.getHorariosDisponiblesByDoctorAndFecha(idDoctor, java.time.LocalDate.now());
            log.info("Doctor {} tiene {} horarios disponibles", idDoctor, horarios.size());
            return horarios.stream()
                    .map(horarioMapper::toJpaEntity)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error al listar horarios del doctor {}: {}", idDoctor, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Map<String, Object> rawRequest) {
        try {
            log.info("📥 Request recibido completo: {}", rawRequest);
            log.info("📥 Claves del request: {}", rawRequest.keySet());
            
            // Extraer idDoctor del request (puede venir como Long o dentro de objeto doctor)
            Long idDoctor = null;
            
            if (rawRequest.containsKey("idDoctor")) {
                Object idDoctorObj = rawRequest.get("idDoctor");
                log.info("🔍 Tipo de idDoctor: {}", idDoctorObj != null ? idDoctorObj.getClass().getName() : "null");
                log.info("🔍 Valor de idDoctor: {}", idDoctorObj);
                
                if (idDoctorObj instanceof Number) {
                    idDoctor = ((Number) idDoctorObj).longValue();
                } else if (idDoctorObj instanceof String) {
                    try {
                        idDoctor = Long.parseLong((String) idDoctorObj);
                    } catch (NumberFormatException e) {
                        log.error("❌ No se pudo parsear idDoctor como Long: {}", idDoctorObj);
                    }
                } else if (idDoctorObj instanceof Map) {
                    // El frontend podría estar enviando {idDoctor: {value: 1}}
                    @SuppressWarnings("unchecked")
                    Map<String, Object> idMap = (Map<String, Object>) idDoctorObj;
                    log.info("🔍 idDoctor es un Map: {}", idMap);
                    Object innerValue = idMap.get("idDoctor");
                    if (innerValue == null) innerValue = idMap.get("value");
                    if (innerValue instanceof Number) {
                        idDoctor = ((Number) innerValue).longValue();
                    }
                }
            } else if (rawRequest.containsKey("doctor")) {
                Object doctorObj = rawRequest.get("doctor");
                log.info("🔍 Tipo de doctor: {}", doctorObj != null ? doctorObj.getClass().getName() : "null");
                log.info("🔍 Valor de doctor: {}", doctorObj);
                
                if (doctorObj instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> doctorMap = (Map<String, Object>) doctorObj;
                    Object idDoctorObj = doctorMap.get("idDoctor");
                    if (idDoctorObj instanceof Number) {
                        idDoctor = ((Number) idDoctorObj).longValue();
                    }
                } else if (doctorObj instanceof Number) {
                    idDoctor = ((Number) doctorObj).longValue();
                }
            }
            
            log.info("📋 ID Doctor extraído final: {}", idDoctor);
            
            // Validar que venga el doctor
            if (idDoctor == null) {
                log.warn("⚠️ No se proporcionó ID de doctor válido");
                log.warn("⚠️ Request completo era: {}", rawRequest);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Debe seleccionar un doctor", "requestRecibido", rawRequest));
            }
            
            // Extraer datos del request con manejo de errores
            log.info("🔍 Extrayendo campos del request...");
            LocalDate fecha = null;
            String turno = null;
            LocalTime horaInicio = null;
            LocalTime horaFin = null;
            
            try {
                if (rawRequest.get("fecha") != null) {
                    fecha = LocalDate.parse(rawRequest.get("fecha").toString());
                    log.info("✅ Fecha parseada: {}", fecha);
                }
            } catch (Exception e) {
                log.error("❌ Error parseando fecha: {}", e.getMessage());
            }
            
            try {
                turno = (String) rawRequest.get("turno");
                log.info("✅ Turno: {}", turno);
            } catch (Exception e) {
                log.error("❌ Error obteniendo turno: {}", e.getMessage());
            }
            
            try {
                if (rawRequest.get("horaInicio") != null) {
                    horaInicio = LocalTime.parse(rawRequest.get("horaInicio").toString());
                    log.info("✅ HoraInicio parseada: {}", horaInicio);
                }
            } catch (Exception e) {
                log.error("❌ Error parseando horaInicio: {}", e.getMessage());
            }
            
            try {
                if (rawRequest.get("horaFin") != null) {
                    horaFin = LocalTime.parse(rawRequest.get("horaFin").toString());
                    log.info("✅ HoraFin parseada: {}", horaFin);
                }
            } catch (Exception e) {
                log.error("❌ Error parseando horaFin: {}", e.getMessage());
            }
            
            // Construir horario de dominio
            log.info("🏗️ Construyendo horario con: fecha={}, turno={}, horaInicio={}, horaFin={}, idDoctor={}", 
                fecha, turno, horaInicio, horaFin, idDoctor);
            
            Horario horario = Horario.builder()
                    .fecha(fecha)
                    .turno(turno)
                    .horaInicio(horaInicio)
                    .horaFin(horaFin)
                    .disponible(true)
                    .idDoctor(idDoctor)
                    .build();
            
            Horario saved = horarioApplicationService.createHorario(horario);
            log.info("✅ Horario creado exitosamente con ID: {}", saved.getIdHorario());
            
            // 🔔 Enviar notificación de nuevo horario disponible
            try {
                String mensaje = String.format("Nuevo horario disponible: %s - %s a %s", 
                    saved.getFecha(), saved.getHoraInicio(), saved.getHoraFin());
                notificationService.notifyHorarioChange(
                    String.valueOf(idDoctor), 
                    mensaje, 
                    horarioMapper.toJpaEntity(saved)
                );
                log.info("✅ Notificación de horario enviada");
            } catch (Exception notifEx) {
                log.warn("⚠️ Error enviando notificación de horario: {}", notifEx.getMessage());
            }
            
            return ResponseEntity.ok(horarioMapper.toJpaEntity(saved));
        } catch (IllegalArgumentException e) {
            log.warn("⚠️ Error de validación: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("❌ Error al crear horario: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al crear el horario"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable @NonNull Long id, @RequestBody Map<String, Object> rawRequest) {
        try {
            log.info("📝 Actualizando horario ID: {}", id);
            log.info("📥 Request recibido: {}", rawRequest);
            
            // Extraer idDoctor del request (puede venir como Long o dentro de objeto doctor)
            Long idDoctor = null;
            if (rawRequest.containsKey("idDoctor")) {
                Object idDoctorObj = rawRequest.get("idDoctor");
                idDoctor = idDoctorObj instanceof Number ? ((Number) idDoctorObj).longValue() : null;
            } else if (rawRequest.containsKey("doctor")) {
                Object doctorObj = rawRequest.get("doctor");
                if (doctorObj instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> doctorMap = (Map<String, Object>) doctorObj;
                    Object idDoctorObj = doctorMap.get("idDoctor");
                    idDoctor = idDoctorObj instanceof Number ? ((Number) idDoctorObj).longValue() : null;
                }
            }
            
            log.info("📋 ID Doctor extraído: {}", idDoctor);
            
            // Validar que venga el doctor
            if (idDoctor == null) {
                log.warn("⚠️ No se proporcionó ID de doctor");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Debe seleccionar un doctor"));
            }
            
            // Extraer datos del request
            LocalDate fecha = rawRequest.get("fecha") != null 
                ? LocalDate.parse(rawRequest.get("fecha").toString()) 
                : null;
            String turno = (String) rawRequest.get("turno");
            LocalTime horaInicio = rawRequest.get("horaInicio") != null 
                ? LocalTime.parse(rawRequest.get("horaInicio").toString()) 
                : null;
            LocalTime horaFin = rawRequest.get("horaFin") != null 
                ? LocalTime.parse(rawRequest.get("horaFin").toString()) 
                : null;
            Boolean disponible = rawRequest.get("disponible") != null 
                ? (Boolean) rawRequest.get("disponible") 
                : true;
            
            // Construir horario de dominio
            Horario horario = Horario.builder()
                    .fecha(fecha)
                    .turno(turno)
                    .horaInicio(horaInicio)
                    .horaFin(horaFin)
                    .disponible(disponible)
                    .idDoctor(idDoctor)
                    .build();
            
            Horario actualizado = horarioApplicationService.updateHorario(id, horario);
            log.info("✅ Horario {} actualizado exitosamente", id);
            
            // 🔔 Enviar notificación de cambio de horario
            try {
                String mensaje = String.format("Horario actualizado: %s - %s a %s (Disponible: %s)", 
                    actualizado.getFecha(), actualizado.getHoraInicio(), actualizado.getHoraFin(), 
                    actualizado.isDisponible() ? "Sí" : "No");
                notificationService.notifyHorarioChange(
                    String.valueOf(idDoctor), 
                    mensaje, 
                    horarioMapper.toJpaEntity(actualizado)
                );
                log.info("✅ Notificación de cambio de horario enviada");
            } catch (Exception notifEx) {
                log.warn("⚠️ Error enviando notificación de cambio de horario: {}", notifEx.getMessage());
            }
            
            return ResponseEntity.ok(horarioMapper.toJpaEntity(actualizado));
        } catch (IllegalArgumentException e) {
            log.warn("⚠️ Error de validación: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("❌ Error al actualizar horario {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al actualizar el horario"));
        }
    }

    @PostMapping("/{id}/reservar")
    public ResponseEntity<?> reservar(@PathVariable Long id) {
        try {
            log.info("Reservando horario ID: {}", id);
            horarioApplicationService.marcarHorarioNoDisponible(id);
            Horario horario = horarioApplicationService.getHorarioById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Horario no encontrado"));
            log.info("Horario {} reservado exitosamente", id);
            return ResponseEntity.ok(horarioMapper.toJpaEntity(horario));
        } catch (IllegalArgumentException e) {
            log.warn("Error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error al reservar horario {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al reservar el horario");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            log.info("Eliminando horario ID: {}", id);
            horarioApplicationService.deleteHorario(id);
            log.info("Horario {} eliminado exitosamente", id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.warn("Error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error al eliminar horario {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar el horario");
        }
    }
    
    /**
     * DTO para crear/actualizar horario.
     * Sigue principio de responsabilidad única: transportar datos HTTP -> Domain.
     */
    public static class HorarioRequest {
        private LocalDate fecha;
        private String turno;
        private LocalTime horaInicio;
        private LocalTime horaFin;
        private boolean disponible = true;
        private Long idDoctor;
        
        public HorarioRequest() {}
        
        public LocalDate getFecha() { return fecha; }
        public String getTurno() { return turno; }
        public LocalTime getHoraInicio() { return horaInicio; }
        public LocalTime getHoraFin() { return horaFin; }
        public boolean isDisponible() { return disponible; }
        public Long getIdDoctor() { return idDoctor; }
        
        public void setFecha(LocalDate fecha) { this.fecha = fecha; }
        public void setTurno(String turno) { this.turno = turno; }
        public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }
        public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }
        public void setDisponible(boolean disponible) { this.disponible = disponible; }
        public void setIdDoctor(Long idDoctor) { this.idDoctor = idDoctor; }
    }
}
