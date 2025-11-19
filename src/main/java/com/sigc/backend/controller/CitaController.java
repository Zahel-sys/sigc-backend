package com.sigc.backend.controller;

import com.sigc.backend.model.Cita;
import com.sigc.backend.model.Horario;
import com.sigc.backend.model.Usuario;
import com.sigc.backend.repository.CitaRepository;
import com.sigc.backend.repository.HorarioRepository;
import com.sigc.backend.repository.UsuarioRepository;
import com.sigc.backend.security.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/citas")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175"})
public class CitaController {

    @Autowired private CitaRepository citaRepository;
    @Autowired private HorarioRepository horarioRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private JwtUtil jwtUtil;

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
            @RequestBody Cita cita) {
        try {
            log.info("📝 Recibiendo petición para crear nueva cita");
            log.info("Datos recibidos: {}", cita);
            
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
            
            // ✅ VALIDACION 2: Verificar que se proporcionó idPaciente
            if (cita.getUsuario() == null || cita.getUsuario().getIdUsuario() == null) {
                log.error("❌ Error: No se proporcionó idPaciente en la petición");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(crearError("Debe proporcionar idPaciente"));
            }
            
            // ✅ VALIDACION 3: Verificar que se proporcionó idHorario
            if (cita.getHorario() == null || cita.getHorario().getIdHorario() == null) {
                log.error("❌ Error: No se proporcionó idHorario en la petición");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(crearError("Debe proporcionar idHorario"));
            }
            
            Long idPaciente = cita.getUsuario().getIdUsuario();
            Long idHorario = cita.getHorario().getIdHorario();
            
            // ✅ VALIDACION 4: Verificar que el paciente existe
            Usuario usuario = usuarioRepository.findById(idPaciente)
                    .orElseThrow(() -> {
                        log.error("❌ Paciente no encontrado con ID: {}", idPaciente);
                        return new RuntimeException("PACIENTE_NO_ENCONTRADO");
                    });
            log.info("✓ Paciente validado: {} (ID: {})", usuario.getNombre(), idPaciente);
            
            // ✅ VALIDACION 5: Verificar que el horario existe
            Horario horario = horarioRepository.findById(idHorario)
                    .orElseThrow(() -> {
                        log.error("❌ Horario no encontrado con ID: {}", idHorario);
                        throw new RuntimeException("HORARIO_NO_ENCONTRADO");
                    });
            log.info("✓ Horario validado: ID {}", idHorario);
            
            // ✅ VALIDACION 6: Verificar que el horario esté disponible
            if (!horario.isDisponible()) {
                log.warn("⚠️ Horario {} ya no está disponible", idHorario);
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(crearError("El horario ya no está disponible"));
            }
            log.info("✓ Horario disponible: SÍ");
            
            // ✅ VALIDACION 7: Verificar que no exista ya una cita para ese horario
            List<Cita> citasExistentes = citaRepository.findByHorario_IdHorario(idHorario);
            if (!citasExistentes.isEmpty()) {
                log.warn("⚠️ Ya existe una cita para el horario {}", idHorario);
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(crearError("Ya existe una cita para este horario"));
            }
            log.info("✓ No hay citas duplicadas para este horario");
            
            // ✅ VALIDACION 8: Verificar que el horario no esté en el pasado
            LocalDateTime ahora = LocalDateTime.now();
            LocalDateTime horarioDateTime = LocalDateTime.of(horario.getFecha(), horario.getHoraInicio());
            
            if (horarioDateTime.isBefore(ahora)) {
                log.warn("⚠️ Intento de reservar horario en el pasado: {}", horarioDateTime);
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                        .body(crearError("No se puede reservar un horario en el pasado"));
            }
            log.info("✓ Horario no está en el pasado");
            
            // ✅ Crear la cita con todas las validaciones pasadas
            cita.setUsuario(usuario);
            cita.setHorario(horario);
            cita.setDoctor(horario.getDoctor());
            cita.setFechaCita(horario.getFecha());
            cita.setHoraCita(horario.getHoraInicio());
            cita.setTurno(horario.getTurno());
            cita.setEstado("confirmada");
            
            // Guardar la fecha de reserva (fechaReserva)
            // Esto se puede agregar al modelo si es necesario
            
            // Cambiar estado del horario a no disponible
            horario.setDisponible(false);
            horarioRepository.save(horario);
            log.info("✓ Horario {} marcado como no disponible", idHorario);
            
            // Guardar la cita
            Cita saved = citaRepository.save(cita);
            log.info("✅ Cita creada exitosamente con ID: {}", saved.getIdCita());
            log.info("   - Paciente: {} (ID: {})", usuario.getNombre(), usuario.getIdUsuario());
            log.info("   - Doctor: {} (ID: {})", horario.getDoctor().getNombre(), horario.getDoctor().getIdDoctor());
            log.info("   - Fecha: {} Hora: {}", saved.getFechaCita(), saved.getHoraCita());
            log.info("   - Estado: confirmada");
            
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
            
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
