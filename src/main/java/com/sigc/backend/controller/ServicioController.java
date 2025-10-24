package com.sigc.backend.controller;

import com.sigc.backend.model.Servicio;
import com.sigc.backend.repository.ServicioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/servicios")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175"})
public class ServicioController {

    @Autowired
    private ServicioRepository servicioRepository;

    @GetMapping
    public List<Servicio> listarServicios() {
        try {
            log.info("Listando todos los servicios");
            List<Servicio> servicios = servicioRepository.findAll();
            log.info("Se encontraron {} servicios", servicios.size());
            return servicios;
        } catch (Exception e) {
            log.error("Error al listar servicios: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @PostMapping
    public ResponseEntity<?> crearServicio(@RequestBody Servicio servicio) {
        try {
            log.info("Creando nuevo servicio: {}", servicio.getNombreServicio());
            Servicio saved = servicioRepository.save(servicio);
            log.info("Servicio creado exitosamente con ID: {}", saved.getIdServicio());
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            log.error("Error al crear servicio: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear el servicio");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerServicio(@PathVariable Long id) {
        try {
            log.info("Obteniendo servicio ID: {}", id);
            Servicio servicio = servicioRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Servicio no encontrado con ID: " + id));
            return ResponseEntity.ok(servicio);
        } catch (Exception e) {
            log.error("Error al obtener servicio {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Servicio no encontrado");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarServicio(@PathVariable Long id, @RequestBody Servicio servicio) {
        try {
            log.info("Actualizando servicio ID: {}", id);
            Servicio existente = servicioRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Servicio no encontrado con ID: " + id));
            
            existente.setNombreServicio(servicio.getNombreServicio());
            existente.setDescripcion(servicio.getDescripcion());
            existente.setDuracionMinutos(servicio.getDuracionMinutos());
            existente.setPrecio(servicio.getPrecio());
            
            Servicio actualizado = servicioRepository.save(existente);
            log.info("Servicio {} actualizado exitosamente", id);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            log.error("Error al actualizar servicio {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar el servicio");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarServicio(@PathVariable Long id) {
        try {
            log.info("Eliminando servicio ID: {}", id);
            servicioRepository.deleteById(id);
            log.info("Servicio {} eliminado exitosamente", id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al eliminar servicio {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar el servicio");
        }
    }
}
