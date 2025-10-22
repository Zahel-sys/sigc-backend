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

    @GetMapping
    public List<Horario> listar() {
        return horarioRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Horario> crear(@Valid @RequestBody Horario horario) {
        return ResponseEntity.ok(horarioRepository.save(horario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Horario> actualizar(@PathVariable Long id, @Valid @RequestBody Horario horario) {
        Horario existente = horarioRepository.findById(id).orElseThrow();
        existente.setFecha(horario.getFecha());
        existente.setTurno(horario.getTurno());
        existente.setHoraInicio(horario.getHoraInicio());
        existente.setHoraFin(horario.getHoraFin());
        existente.setDisponible(horario.isDisponible());
        return ResponseEntity.ok(horarioRepository.save(existente));
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        horarioRepository.deleteById(id);
    }
}
