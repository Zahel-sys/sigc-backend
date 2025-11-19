package com.sigc.backend.controller;

import com.sigc.backend.model.Cita;
import com.sigc.backend.model.Horario;
import com.sigc.backend.model.Usuario;
import com.sigc.backend.repository.CitaRepository;
import com.sigc.backend.repository.HorarioRepository;
import com.sigc.backend.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/citas")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175"})
public class CitaController {

    @Autowired private CitaRepository citaRepository;
    @Autowired private HorarioRepository horarioRepository;
    @Autowired private UsuarioRepository usuarioRepository;

    @GetMapping
    public List<Cita> listar() {
        try {
            log.info("Listando todas las citas");
            List<Cita> citas = citaRepository.findAll();
            log.info("Se encontraron {} citas", citas.size());
            return citas;
        } catch (Exception e) {
            log.error("Error al listar citas: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @GetMapping("/usuario/{idUsuario}")
    public List<Cita> listarPorUsuario(@PathVariable Long idUsuario) {
        try {
            log.info("Listando citas del usuario ID: {}", idUsuario);
            List<Cita> citas = citaRepository.findByUsuario_IdUsuario(idUsuario);
            log.info("Usuario {} tiene {} citas", idUsuario, citas.size());
            return citas;
        } catch (Exception e) {
            log.error("Error al listar citas del usuario {}: {}", idUsuario, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Cita cita) {
        try {
            log.info("📝 Recibiendo petición para crear nueva cita");
            log.info("Datos recibidos: {}", cita);
            
            // Validar que se envió el usuario/paciente
            if (cita.getUsuario() == null || cita.getUsuario().getIdUsuario() == null) {
                log.error("❌ Error: No se proporcionó usuario/paciente en la petición");
                return ResponseEntity.badRequest()
                        .body("Debe proporcionar un usuario/paciente válido");
            }
            
            // Validar que se envió el horario
            if (cita.getHorario() == null || cita.getHorario().getIdHorario() == null) {
                log.error("❌ Error: No se proporcionó horario en la petición");
                return ResponseEntity.badRequest()
                        .body("Debe proporcionar un horario válido");
            }
            
            Long idUsuario = cita.getUsuario() != null ? cita.getUsuario().getIdUsuario() : null;
            Long idHorario = cita.getHorario() != null ? cita.getHorario().getIdHorario() : null;
            
            if (idUsuario == null) {
                log.error("❌ Error: Usuario es nulo después de validación");
                return ResponseEntity.badRequest().body("Usuario inválido");
            }
            if (idHorario == null) {
                log.error("❌ Error: Horario es nulo después de validación");
                return ResponseEntity.badRequest().body("Horario inválido");
            }
            
            log.info("Creando cita para usuario ID: {} con horario ID: {}", idUsuario, idHorario);
            
            // Buscar usuario
            Usuario usuario = usuarioRepository.findById(idUsuario)
                    .orElseThrow(() -> {
                        log.error("❌ Usuario no encontrado con ID: {}", idUsuario);
                        return new RuntimeException("Usuario no encontrado con ID: " + idUsuario);
                    });
            
            // Buscar horario
            Horario horario = horarioRepository.findById(idHorario)
                    .orElseThrow(() -> {
                        log.error("❌ Horario no encontrado con ID: {}", idHorario);
                        return new RuntimeException("Horario no encontrado con ID: " + idHorario);
                    });

            // Validar que el horario esté disponible
            if (!horario.isDisponible()) {
                log.warn("⚠️ Horario {} ya no está disponible", horario.getIdHorario());
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("El horario seleccionado ya no está disponible");
            }

            // Asignar datos a la cita
            cita.setUsuario(usuario);
            cita.setHorario(horario);
            cita.setDoctor(horario.getDoctor());
            cita.setFechaCita(horario.getFecha());
            cita.setHoraCita(horario.getHoraInicio());
            cita.setTurno(horario.getTurno());
            cita.setEstado("ACTIVA");

            // Marcar horario como no disponible
            horario.setDisponible(false);
            horarioRepository.save(horario);

            // Guardar cita
            Cita saved = citaRepository.save(cita);
            log.info("✅ Cita creada exitosamente con ID: {}", saved.getIdCita());
            log.info("   - Paciente: {} (ID: {})", usuario.getNombre(), usuario.getIdUsuario());
            log.info("   - Doctor: {} (ID: {})", horario.getDoctor().getNombre(), horario.getDoctor().getIdDoctor());
            log.info("   - Fecha: {} Hora: {}", saved.getFechaCita(), saved.getHoraCita());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
            
        } catch (RuntimeException e) {
            log.error("❌ Error de validación al crear cita: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: " + e.getMessage());
        } catch (Exception e) {
            log.error("❌ Error inesperado al crear cita: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno al crear la cita: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<String> cancelar(@PathVariable Long id) {
        try {
            log.info("Cancelando cita ID: {}", id);
            if (id == null) {
                return ResponseEntity.badRequest().body("ID inválido");
            }
            Cita cita = citaRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

            long diasRestantes = java.time.temporal.ChronoUnit.DAYS.between(
                    LocalDate.now(), cita.getFechaCita()
            );

            if (diasRestantes < 2) {
                log.warn("Intento de cancelar cita {} con menos de 2 días de anticipación", id);
                return ResponseEntity.badRequest()
                        .body("No se puede cancelar la cita con menos de 2 días de anticipación");
            }

            cita.setEstado("CANCELADA");
            Horario horarioAsociado = cita.getHorario();
            if (horarioAsociado != null) {
                horarioAsociado.setDisponible(true);
                horarioRepository.save(horarioAsociado);
            }
            citaRepository.save(cita);

            log.info("Cita {} cancelada correctamente", id);
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
                citaRepository.deleteById(id);
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
}
