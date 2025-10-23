package com.sigc.backend.controller;

import com.sigc.backend.model.Horario;
import com.sigc.backend.repository.HorarioRepository;
import com.sigc.backend.repository.DoctorRepository;
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

    @Autowired
    private DoctorRepository doctorRepository;

    // 📋 Listar todos los horarios
    @GetMapping
    public List<Horario> listar() {
        return horarioRepository.findAll();
    }

    // ➕ Crear un nuevo horario
    @PostMapping
    public ResponseEntity<Horario> crear(@Valid @RequestBody Horario horario) {
        // 🔹 Buscar el doctor por ID y asignarlo correctamente
        if (horario.getDoctor() != null && horario.getDoctor().getIdDoctor() != null) {
            var doctor = doctorRepository.findById(horario.getDoctor().getIdDoctor())
                    .orElseThrow(() -> new RuntimeException("Doctor no encontrado"));
            horario.setDoctor(doctor);
        }

        return ResponseEntity.ok(horarioRepository.save(horario));
    }

    // ✏️ Actualizar un horario existente
    @PutMapping("/{id}")
    public ResponseEntity<Horario> actualizar(@PathVariable Long id, @Valid @RequestBody Horario horario) {
        Horario existente = horarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado con id " + id));

        existente.setFecha(horario.getFecha());
        existente.setTurno(horario.getTurno());
        existente.setHoraInicio(horario.getHoraInicio());
        existente.setHoraFin(horario.getHoraFin());
        existente.setDisponible(horario.isDisponible());

        // 🔹 Actualizar también el doctor asociado
        if (horario.getDoctor() != null && horario.getDoctor().getIdDoctor() != null) {
            var doctor = doctorRepository.findById(horario.getDoctor().getIdDoctor())
                    .orElseThrow(() -> new RuntimeException("Doctor no encontrado"));
            existente.setDoctor(doctor);
        }

        return ResponseEntity.ok(horarioRepository.save(existente));
    }

    // 🗑️ Eliminar horario
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        horarioRepository.deleteById(id);
    }
}
