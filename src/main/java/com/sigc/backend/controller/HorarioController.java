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
import java.util.Collections;
import java.util.List;
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
    public ResponseEntity<?> crear(@Valid @RequestBody com.sigc.backend.model.Horario horarioJpa) {
        try {
            log.info("Creando nuevo horario para doctor ID: {}", horarioJpa.getDoctor().getIdDoctor());
            Horario horario = horarioMapper.toDomain(horarioJpa);
            Horario saved = horarioApplicationService.createHorario(horario);
            log.info("Horario creado exitosamente con ID: {}", saved.getIdHorario());
            return ResponseEntity.ok(horarioMapper.toJpaEntity(saved));
        } catch (IllegalArgumentException e) {
            log.warn("Error de validación: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error al crear horario: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear el horario");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable @NonNull Long id, @Valid @RequestBody com.sigc.backend.model.Horario horarioJpa) {
        try {
            log.info("Actualizando horario ID: {}", id);
            Horario horario = horarioMapper.toDomain(horarioJpa);
            Horario actualizado = horarioApplicationService.updateHorario(id, horario);
            log.info("Horario {} actualizado exitosamente", id);
            return ResponseEntity.ok(horarioMapper.toJpaEntity(actualizado));
        } catch (IllegalArgumentException e) {
            log.warn("Error de validación: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error al actualizar horario {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar el horario");
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
}
