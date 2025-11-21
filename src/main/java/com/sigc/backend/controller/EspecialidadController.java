package com.sigc.backend.controller;

import com.sigc.backend.application.mapper.EspecialidadMapper;
import com.sigc.backend.application.service.EspecialidadApplicationService;
import com.sigc.backend.domain.model.Especialidad;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/especialidades")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175"})
@RequiredArgsConstructor
public class EspecialidadController {

    private final EspecialidadApplicationService especialidadApplicationService;
    private final EspecialidadMapper especialidadMapper;
    
    private final String BASE_UPLOAD_DIR = "C:/sigc/uploads/especialidades/";
    private final List<String> EXTENSIONES_PERMITIDAS = Arrays.asList("jpg", "jpeg", "png", "webp");
    private final long MAX_SIZE = 5 * 1024 * 1024; // 5MB

    @GetMapping
    public List<com.sigc.backend.model.Especialidad> listar() {
        try {
            log.info("Listando todas las especialidades");
            List<Especialidad> especialidades = especialidadApplicationService.getAllEspecialidades();
            log.info("Se encontraron {} especialidades", especialidades.size());
            return especialidades.stream()
                    .map(especialidadMapper::toJpaEntity)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error al listar especialidades: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody com.sigc.backend.model.Especialidad especialidadJpa) {
        try {
            log.info("Creando nueva especialidad: {}", especialidadJpa.getNombre());
            Especialidad especialidad = especialidadMapper.toDomain(especialidadJpa);
            Especialidad saved = especialidadApplicationService.createEspecialidad(especialidad);
            log.info("Especialidad creada exitosamente con ID: {}", saved.getIdEspecialidad());
            return ResponseEntity.ok(especialidadMapper.toJpaEntity(saved));
        } catch (IllegalArgumentException e) {
            log.warn("Error de validación: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error al crear especialidad: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear la especialidad");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable @NonNull Long id,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String descripcion,
            @RequestParam(value = "imagen", required = false) MultipartFile imagenFile) {
        try {
            log.info("📥 PUT /especialidades/{}", id);
            log.info("  - nombre: {}", nombre);
            log.info("  - descripcion: {}", descripcion);
            log.info("  - imagenFile: {}", imagenFile != null ? imagenFile.getOriginalFilename() : "null");
            
            // Obtener especialidad existente
            Especialidad existente = especialidadApplicationService.getEspecialidadById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Especialidad no encontrada"));
            
            // Actualizar campos si vienen
            if (nombre != null && !nombre.trim().isEmpty()) {
                existente.setNombre(nombre.trim());
            }
            if (descripcion != null) {
                existente.setDescripcion(descripcion.trim());
            }
            
            // Guardar archivo si viene
            if (imagenFile != null && !imagenFile.isEmpty()) {
                String imagenGuardada = guardarImagen(imagenFile);
                if (imagenGuardada != null) {
                    existente.setImagen(imagenGuardada);
                }
            }
            
            Especialidad actualizada = especialidadApplicationService.updateEspecialidad(id, existente);
            log.info("✅ Especialidad actualizada: {}", id);
            
            return ResponseEntity.ok(especialidadMapper.toJpaEntity(actualizada));
        } catch (IllegalArgumentException e) {
            log.warn("⚠️ Error de validación: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        } catch (IOException e) {
            log.error("❌ Error al guardar imagen: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error al guardar imagen: " + e.getMessage()));
        } catch (Exception e) {
            log.error("❌ Error al actualizar especialidad {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error al actualizar la especialidad"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            log.info("Eliminando especialidad con ID: {}", id);
            especialidadApplicationService.deleteEspecialidad(id);
            log.info("Especialidad eliminada exitosamente: {}", id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.warn("Error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error al eliminar especialidad {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar la especialidad");
        }
    }
    
    private String guardarImagen(MultipartFile file) throws IOException {
        if (file.getSize() > MAX_SIZE) {
            throw new IOException("El archivo excede los 5MB permitidos.");
        }
        
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new IOException("El nombre del archivo es invalido");
        }
        
        String extension = "";
        if (originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        }
        
        if (!EXTENSIONES_PERMITIDAS.contains(extension)) {
            throw new IOException("Formato no permitido. Solo: " + EXTENSIONES_PERMITIDAS);
        }
        
        File uploadDir = new File(BASE_UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        
        String sanitized = originalFilename.replaceAll("[^a-zA-Z0-9.-]", "_");
        String fileName = System.currentTimeMillis() + "_" + sanitized;
        
        File destino = new File(uploadDir, fileName);
        file.transferTo(destino);
        
        log.info("Archivo guardado en: {}", destino.getAbsolutePath());
        return fileName;
    }
    
    @GetMapping("/imagen/{filename:.+}")
    public ResponseEntity<byte[]> servirImagen(@PathVariable String filename) {
        try {
            File imgFile = new File(BASE_UPLOAD_DIR + filename);
            if (!imgFile.exists()) {
                return ResponseEntity.notFound().build();
            }
            
            byte[] bytes = java.nio.file.Files.readAllBytes(imgFile.toPath());
            MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
            
            if (filename.endsWith(".png")) {
                mediaType = MediaType.IMAGE_PNG;
            } else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
                mediaType = MediaType.IMAGE_JPEG;
            } else if (filename.endsWith(".webp")) {
                mediaType = MediaType.valueOf("image/webp");
            }
            
            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .body(bytes);
        } catch (Exception e) {
            log.error("Error al servir imagen: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
