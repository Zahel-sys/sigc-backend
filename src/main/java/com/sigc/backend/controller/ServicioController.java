package com.sigc.backend.controller;

import com.sigc.backend.application.mapper.ServicioMapper;
import com.sigc.backend.application.service.ServicioApplicationService;
import com.sigc.backend.domain.model.Servicio;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/servicios")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175"})
@RequiredArgsConstructor
public class ServicioController {

    private final ServicioApplicationService servicioApplicationService;
    private final ServicioMapper servicioMapper;

    @GetMapping
    public List<com.sigc.backend.model.Servicio> listarServicios() {
        try {
            log.info("Listando todos los servicios");
            List<Servicio> servicios = servicioApplicationService.getAllServicios();
            log.info("Se encontraron {} servicios", servicios.size());
            return servicios.stream()
                    .map(servicioMapper::toJpaEntity)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error al listar servicios: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @PostMapping
    public ResponseEntity<?> crearServicio(@RequestBody com.sigc.backend.model.Servicio servicioJpa) {
        try {
            log.info("Creando nuevo servicio: {}", servicioJpa.getNombreServicio());
            Servicio servicio = servicioMapper.toDomain(servicioJpa);
            Servicio saved = servicioApplicationService.createServicio(servicio);
            log.info("Servicio creado exitosamente con ID: {}", saved.getIdServicio());
            return ResponseEntity.ok(servicioMapper.toJpaEntity(saved));
        } catch (IllegalArgumentException e) {
            log.warn("Error de validación: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
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
            Servicio servicio = servicioApplicationService.getServicioById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Servicio no encontrado con ID: " + id));
            return ResponseEntity.ok(servicioMapper.toJpaEntity(servicio));
        } catch (IllegalArgumentException e) {
            log.warn("Error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error al obtener servicio {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Servicio no encontrado");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarServicio(@PathVariable Long id, @RequestBody com.sigc.backend.model.Servicio servicioJpa) {
        try {
            log.info("Actualizando servicio ID: {}", id);
            Servicio servicio = servicioMapper.toDomain(servicioJpa);
            Servicio actualizado = servicioApplicationService.updateServicio(id, servicio);
            log.info("Servicio {} actualizado exitosamente", id);
            return ResponseEntity.ok(servicioMapper.toJpaEntity(actualizado));
        } catch (IllegalArgumentException e) {
            log.warn("Error de validación: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
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
            servicioApplicationService.deleteServicio(id);
            log.info("Servicio {} eliminado exitosamente", id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.warn("Error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error al eliminar servicio {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar el servicio");
        }
    }
}
