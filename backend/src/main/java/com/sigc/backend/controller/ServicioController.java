package com.sigc.backend.controller;

import com.sigc.backend.model.Servicio;
import com.sigc.backend.repository.ServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/servicios")
@CrossOrigin(origins = "*")
public class ServicioController {

    @Autowired
    private ServicioRepository servicioRepository;

    @GetMapping
    public List<Servicio> listarServicios() {
        return servicioRepository.findAll();
    }

    @PostMapping
    public Servicio crearServicio(@RequestBody Servicio servicio) {
        return servicioRepository.save(servicio);
    }

    @GetMapping("/{id}")
    public Servicio obtenerServicio(@PathVariable Long id) {
        return servicioRepository.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public Servicio actualizarServicio(@PathVariable Long id, @RequestBody Servicio servicio) {
        Servicio existente = servicioRepository.findById(id).orElse(null);
        if (existente != null) {
            existente.setNombreServicio(servicio.getNombreServicio());
            existente.setDescripcion(servicio.getDescripcion());
            existente.setDuracionMinutos(servicio.getDuracionMinutos());
            existente.setPrecio(servicio.getPrecio());
            return servicioRepository.save(existente);
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public void eliminarServicio(@PathVariable Long id) {
        servicioRepository.deleteById(id);
    }
}
