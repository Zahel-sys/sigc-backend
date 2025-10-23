package com.sigc.backend.controller;

import com.sigc.backend.model.Cita;
import com.sigc.backend.model.Horario;
import com.sigc.backend.model.Usuario;
import com.sigc.backend.repository.CitaRepository;
import com.sigc.backend.repository.HorarioRepository;
import com.sigc.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/citas")
@CrossOrigin(origins = "*")
public class CitaController {

    @Autowired private CitaRepository citaRepository;
    @Autowired private HorarioRepository horarioRepository;
    @Autowired private UsuarioRepository usuarioRepository;

    // 📋 Listar todas las citas
    @GetMapping
    public List<Cita> listar() {
        return citaRepository.findAll();
    }

    // 📋 Listar citas de un usuario
    @GetMapping("/usuario/{idUsuario}")
    public List<Cita> listarPorUsuario(@PathVariable Long idUsuario) {
        return citaRepository.findByUsuario_IdUsuario(idUsuario);
    }

    // ➕ Crear una nueva cita
    @PostMapping
    public ResponseEntity<Cita> crear(@RequestBody Cita cita) {
        Usuario usuario = usuarioRepository.findById(cita.getUsuario().getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Horario horario = horarioRepository.findById(cita.getHorario().getIdHorario())
                .orElseThrow(() -> new RuntimeException("Horario no encontrado"));

        // Validar si el horario sigue disponible
        if (!horario.isDisponible()) {
            throw new RuntimeException("El horario ya no está disponible.");
        }

        // Asignar info
        cita.setUsuario(usuario);
        cita.setHorario(horario);
        cita.setDoctor(horario.getDoctor());
        cita.setFechaCita(horario.getFecha());
        cita.setHoraCita(horario.getHoraInicio());
        cita.setTurno(horario.getTurno());
        cita.setEstado("ACTIVA");

        // Marcar horario como reservado
        horario.setDisponible(false);
        horarioRepository.save(horario);

        return ResponseEntity.ok(citaRepository.save(cita));
    }

    // ❌ Cancelar cita (solo si faltan >= 2 días)
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<String> cancelar(@PathVariable Long id) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        long diasRestantes = java.time.temporal.ChronoUnit.DAYS.between(
                LocalDate.now(), cita.getFechaCita()
        );

        if (diasRestantes < 2) {
            return ResponseEntity.badRequest()
                    .body("No se puede cancelar la cita con menos de 2 días de anticipación.");
        }

        cita.setEstado("CANCELADA");
        cita.getHorario().setDisponible(true);
        horarioRepository.save(cita.getHorario());
        citaRepository.save(cita);

        return ResponseEntity.ok("Cita cancelada correctamente.");
    }

    // 🗑️ Eliminar (opcional)
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        citaRepository.deleteById(id);
    }
}
