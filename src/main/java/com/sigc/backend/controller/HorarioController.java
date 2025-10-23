package com.sigc.backend.controller;

import com.sigc.backend.model.Horario;
import com.sigc.backend.repository.HorarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/horarios")
@CrossOrigin(origins = "*")
public class HorarioController {

    @Autowired
    private HorarioRepository horarioRepository;

    // 🔹 Listar todos los horarios
    @GetMapping
    public List<Horario> listar() {
        return horarioRepository.findAll();
    }

    // 🔹 Listar horarios por doctor (solo disponibles)
    @GetMapping("/doctor/{idDoctor}")
    public List<Horario> listarPorDoctor(@PathVariable Long idDoctor) {
        return horarioRepository.findByDoctor_IdDoctorAndDisponibleTrue(idDoctor);
    }

    // 🔹 Crear nuevo horario
    @PostMapping
    public ResponseEntity<Horario> crear(@Valid @RequestBody Horario horario) {
        return ResponseEntity.ok(horarioRepository.save(horario));
    }

    // 🔹 Actualizar horario existente
    @PutMapping("/{id}")
    public ResponseEntity<Horario> actualizar(@PathVariable Long id, @Valid @RequestBody Horario horario) {
        Horario existente = horarioRepository.findById(id).orElseThrow();
        existente.setFecha(horario.getFecha());
        existente.setTurno(horario.getTurno());
        existente.setHoraInicio(horario.getHoraInicio());
        existente.setHoraFin(horario.getHoraFin());
        existente.setDisponible(horario.isDisponible());
        existente.setDoctor(horario.getDoctor());
        return ResponseEntity.ok(horarioRepository.save(existente));
    }

    // 🔹 Reservar horario (marcar como no disponible)
    @PutMapping("/{id}/reservar")
    public ResponseEntity<Horario> reservar(@PathVariable Long id) {
        Horario horario = horarioRepository.findById(id).orElseThrow();
        horario.setDisponible(false);
        return ResponseEntity.ok(horarioRepository.save(horario));
    }

    // 🔹 Eliminar horario
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        horarioRepository.deleteById(id);
    }
}
