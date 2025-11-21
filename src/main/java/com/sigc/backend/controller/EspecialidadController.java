package com.sigc.backend.controller;

import com.sigc.backend.application.mapper.EspecialidadMapper;
import com.sigc.backend.application.service.EspecialidadApplicationService;
import com.sigc.backend.domain.model.Especialidad;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/especialidades")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175"})
@RequiredArgsConstructor
public class EspecialidadController {

    private final EspecialidadApplicationService especialidadApplicationService;
    private final EspecialidadMapper especialidadMapper;

    @GetMapping
    public List<com.sigc.backend.model.Especialidad> listar() {
        try {
            log.info("Listando todas las especialidades");
            List<Especialidad> especialidades = especialidadApplicationService.getAllEspecialidades();
            log.info("Se encontraron {} especialidades", especialidades.size());
            return especialidades.stream()
                    .map(especialidadMapper::toJpaEntity)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error al listar especialidades: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody com.sigc.backend.model.Especialidad especialidadJpa) {
        try {
            log.info("Creando nueva especialidad: {}", especialidadJpa.getNombre());
            Especialidad especialidad = especialidadMapper.toDomain(especialidadJpa);
            Especialidad saved = especialidadApplicationService.createEspecialidad(especialidad);
            log.info("Especialidad creada exitosamente con ID: {}", saved.getIdEspecialidad());
            return ResponseEntity.ok(especialidadMapper.toJpaEntity(saved));
        } catch (IllegalArgumentException e) {
            log.warn("Error de validación: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error al crear especialidad: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear la especialidad");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable @NonNull Long id, @Valid @RequestBody com.sigc.backend.model.Especialidad especialidadJpa) {
        try {
            log.info("Actualizando especialidad con ID: {}", id);
            Especialidad especialidad = especialidadMapper.toDomain(especialidadJpa);
            Especialidad actualizada = especialidadApplicationService.updateEspecialidad(id, especialidad);
            log.info("Especialidad actualizada exitosamente: {}", id);
            return ResponseEntity.ok(especialidadMapper.toJpaEntity(actualizada));
        } catch (IllegalArgumentException e) {
            log.warn("Error de validación: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error al actualizar especialidad {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar la especialidad");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            log.info("Eliminando especialidad con ID: {}", id);
            especialidadApplicationService.deleteEspecialidad(id);
            log.info("Especialidad eliminada exitosamente: {}", id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.warn("Error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error al eliminar especialidad {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar la especialidad");
        }
    }
}
