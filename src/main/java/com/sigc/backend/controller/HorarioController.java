package com.sigc.backend.controller;

import com.sigc.backend.application.mapper.HorarioMapper;
import com.sigc.backend.application.service.HorarioApplicationService;
import com.sigc.backend.domain.model.Horario;
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
    public ResponseEntity<?> crear(@Valid @RequestBody HorarioRequest request) {
        try {
            log.info("📥 Creando nuevo horario para doctor ID: {}", request.getIdDoctor());
            
            // Validar que venga el doctor
            if (request.getIdDoctor() == null) {
                log.warn("⚠️ No se proporcionó ID de doctor");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Debe seleccionar un doctor"));
            }
            
            // Construir horario de dominio
            Horario horario = Horario.builder()
                    .fecha(request.getFecha())
                    .turno(request.getTurno())
                    .horaInicio(request.getHoraInicio())
                    .horaFin(request.getHoraFin())
                    .disponible(true)
                    .idDoctor(request.getIdDoctor())
                    .build();
            
            Horario saved = horarioApplicationService.createHorario(horario);
            log.info("✅ Horario creado exitosamente con ID: {}", saved.getIdHorario());
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
    public ResponseEntity<?> actualizar(@PathVariable @NonNull Long id, @Valid @RequestBody HorarioRequest request) {
        try {
            log.info("📝 Actualizando horario ID: {}", id);
            
            // Validar que venga el doctor
            if (request.getIdDoctor() == null) {
                log.warn("⚠️ No se proporcionó ID de doctor");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Debe seleccionar un doctor"));
            }
            
            // Construir horario de dominio
            Horario horario = Horario.builder()
                    .fecha(request.getFecha())
                    .turno(request.getTurno())
                    .horaInicio(request.getHoraInicio())
                    .horaFin(request.getHoraFin())
                    .disponible(request.isDisponible())
                    .idDoctor(request.getIdDoctor())
                    .build();
            
            Horario actualizado = horarioApplicationService.updateHorario(id, horario);
            log.info("✅ Horario {} actualizado exitosamente", id);
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
