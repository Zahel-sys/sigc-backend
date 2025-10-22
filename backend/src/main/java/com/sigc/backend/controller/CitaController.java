package com.sigc.backend.controller;

import com.sigc.backend.model.Cita;
import com.sigc.backend.repository.CitaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/citas")
@CrossOrigin(origins = "*")
public class CitaController {

    @Autowired
    private CitaRepository citaRepository;

    @GetMapping
    public List<Cita> listarCitas() {
        return citaRepository.findAll();
    }

    @PostMapping
    public Cita crearCita(@RequestBody Cita cita) {
        return citaRepository.save(cita);
    }

    @GetMapping("/{id}")
    public Cita obtenerCita(@PathVariable Long id) {
        return citaRepository.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public Cita actualizarCita(@PathVariable Long id, @RequestBody Cita cita) {
        Cita existente = citaRepository.findById(id).orElse(null);
        if (existente != null) {
            existente.setFechaCita(cita.getFechaCita());
            existente.setHoraCita(cita.getHoraCita());
            existente.setEstado(cita.getEstado());
            existente.setUsuario(cita.getUsuario());
            existente.setServicio(cita.getServicio());
            return citaRepository.save(existente);
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public void eliminarCita(@PathVariable Long id) {
        citaRepository.deleteById(id);
    }
}
