package com.sigc.backend.controller;

import com.sigc.backend.model.Horario;
import com.sigc.backend.repository.HorarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.lang.NonNull;
import jakarta.validation.Valid;
import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/horarios")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175"})
public class HorarioController {

    @Autowired
    private HorarioRepository horarioRepository;

    @GetMapping
    public List<Horario> listar() {
        try {
            log.info("Listando todos los horarios");
            List<Horario> horarios = horarioRepository.findAll();
            log.info("Se encontraron {} horarios", horarios.size());
            return horarios;
        } catch (Exception e) {
            log.error("Error al listar horarios: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @GetMapping("/doctor/{idDoctor}")
    public List<Horario> listarPorDoctor(@PathVariable Long idDoctor) {
        try {
            log.info("Listando horarios disponibles del doctor ID: {}", idDoctor);
            List<Horario> horarios = horarioRepository.findByDoctor_IdDoctorAndDisponibleTrue(idDoctor);
            log.info("Doctor {} tiene {} horarios disponibles", idDoctor, horarios.size());
            return horarios;
        } catch (Exception e) {
            log.error("Error al listar horarios del doctor {}: {}", idDoctor, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody Horario horario) {
        try {
            log.info("Creando nuevo horario para doctor ID: {}", horario.getDoctor().getIdDoctor());
            Horario saved = horarioRepository.save(horario);
            log.info("Horario creado exitosamente con ID: {}", saved.getIdHorario());
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            log.error("Error al crear horario: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear el horario");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable @NonNull Long id, @Valid @RequestBody Horario horario) {
        try {
            log.info("Actualizando horario ID: {}", id);
            Horario existente = horarioRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Horario no encontrado con ID: " + id));
            
            if (horario.getFecha() != null) existente.setFecha(horario.getFecha());
            if (horario.getTurno() != null) existente.setTurno(horario.getTurno());
            if (horario.getHoraInicio() != null) existente.setHoraInicio(horario.getHoraInicio());
            if (horario.getHoraFin() != null) existente.setHoraFin(horario.getHoraFin());
            existente.setDisponible(horario.isDisponible());
            if (horario.getDoctor() != null) existente.setDoctor(horario.getDoctor());
            
            Horario actualizado = horarioRepository.save(existente);
            log.info("Horario {} actualizado exitosamente", id);
            return ResponseEntity.ok(actualizado);
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
            Horario horario = horarioRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Horario no encontrado con ID: " + id));
            
            horario.setDisponible(false);
            Horario reservado = horarioRepository.save(horario);
            log.info("Horario {} reservado exitosamente", id);
            return ResponseEntity.ok(reservado);
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
            horarioRepository.deleteById(id);
            log.info("Horario {} eliminado exitosamente", id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al eliminar horario {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar el horario");
        }
    }
}
