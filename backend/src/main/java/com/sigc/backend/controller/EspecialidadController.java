package com.sigc.backend.controller;

import com.sigc.backend.model.Especialidad;
import com.sigc.backend.repository.EspecialidadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/especialidades")
@CrossOrigin(origins = "*")
public class EspecialidadController {

    @Autowired
    private EspecialidadRepository especialidadRepository;

    @GetMapping
    public List<Especialidad> listar() {
        return especialidadRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Especialidad> crear(@Valid @RequestBody Especialidad especialidad) {
        return ResponseEntity.ok(especialidadRepository.save(especialidad));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Especialidad> actualizar(@PathVariable Long id, @Valid @RequestBody Especialidad especialidad) {
        Especialidad existente = especialidadRepository.findById(id).orElseThrow();
        existente.setNombre(especialidad.getNombre());
        existente.setDescripcion(especialidad.getDescripcion());
        existente.setImagen(especialidad.getImagen());
        return ResponseEntity.ok(especialidadRepository.save(existente));
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        especialidadRepository.deleteById(id);
    }
}
