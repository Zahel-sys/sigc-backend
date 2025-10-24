package com.sigc.backend.controller;

import com.sigc.backend.model.Especialidad;
import com.sigc.backend.repository.EspecialidadRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/especialidades")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175"})
public class EspecialidadController {

    @Autowired
    private EspecialidadRepository especialidadRepository;

    @GetMapping
    public List<Especialidad> listar() {
        try {
            log.info("Listando todas las especialidades");
            List<Especialidad> especialidades = especialidadRepository.findAll();
            log.info("Se encontraron {} especialidades", especialidades.size());
            return especialidades;
        } catch (Exception e) {
            log.error("Error al listar especialidades: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody Especialidad especialidad) {
        try {
            log.info("Creando nueva especialidad: {}", especialidad.getNombre());
            Especialidad saved = especialidadRepository.save(especialidad);
            log.info("Especialidad creada exitosamente con ID: {}", saved.getIdEspecialidad());
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            log.error("Error al crear especialidad: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear la especialidad");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody Especialidad especialidad) {
        try {
            log.info("Actualizando especialidad con ID: {}", id);
            Especialidad existente = especialidadRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Especialidad no encontrada con ID: " + id));
            
            existente.setNombre(especialidad.getNombre());
            existente.setDescripcion(especialidad.getDescripcion());
            existente.setImagen(especialidad.getImagen());
            
            Especialidad actualizada = especialidadRepository.save(existente);
            log.info("Especialidad actualizada exitosamente: {}", id);
            return ResponseEntity.ok(actualizada);
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
            especialidadRepository.deleteById(id);
            log.info("Especialidad eliminada exitosamente: {}", id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al eliminar especialidad {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar la especialidad");
        }
    }
}
