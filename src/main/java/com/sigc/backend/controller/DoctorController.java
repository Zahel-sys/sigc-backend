package com.sigc.backend.controller;

import com.sigc.backend.application.mapper.DoctorMapper;
import com.sigc.backend.application.service.DoctorApplicationService;
import com.sigc.backend.domain.model.Doctor;
import com.sigc.backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/doctores")
@CrossOrigin(origins = {"http://localhost:5173"})
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorApplicationService doctorApplicationService;
    private final DoctorMapper doctorMapper;
    private final NotificationService notificationService;

    @org.springframework.beans.factory.annotation.Value("${app.upload.dir:uploads/}")
    private String appUploadDir; // base upload dir configurable

    private final List<String> EXTENSIONES_PERMITIDAS = Arrays.asList("jpg", "jpeg", "png", "webp");
    private final long MAX_SIZE = 5 * 1024 * 1024;

    @GetMapping
    public List<com.sigc.backend.model.Doctor> listar() {
        try {
            List<Doctor> doctors = doctorApplicationService.getAllDoctors();
            return doctors.stream()
                    .map(doctorMapper::toJpaEntity)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error al listar doctores: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> crear(
            @RequestParam("nombre") String nombre,
            @RequestParam("apellido") String apellido,
            @RequestParam("telefono") String telefono,
            @RequestParam("correo") String correo,
            @RequestParam("especialidadId") Long especialidadId,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen) {

        try {
            log.info("📥 POST /doctores - Crear doctor");
            log.info("  - nombre: {}, apellido: {}", nombre, apellido);
            log.info("  - correo: {}, telefono: {}", correo, telefono);
            log.info("  - especialidadId: {}", especialidadId);
            
            Doctor doctor = Doctor.builder()
                    .nombre(nombre)
                    .apellido(apellido)
                    .telefono(telefono)
                    .correo(correo)
                    .especialidadId(especialidadId)
                    .build();

            if (imagen != null && !imagen.isEmpty()) {
                String rutaImagen = guardarImagen(imagen);
                doctor.setImagen(rutaImagen);
                log.info("  - imagen guardada: {}", rutaImagen);
            }

            Doctor saved = doctorApplicationService.createDoctor(doctor);
            log.info("✅ Doctor creado exitosamente con ID: {}", saved.getIdDoctor());
            
            // 🔔 Enviar notificación de nuevo doctor
            try {
                String mensaje = String.format("Nuevo doctor registrado: Dr. %s %s", 
                    saved.getNombre(), saved.getApellido());
                notificationService.notifyDoctorUpdate(
                    String.valueOf(saved.getIdDoctor()), 
                    mensaje, 
                    doctorMapper.toJpaEntity(saved)
                );
                log.info("✅ Notificación de nuevo doctor enviada");
            } catch (Exception notifEx) {
                log.warn("⚠️ Error enviando notificación de nuevo doctor: {}", notifEx.getMessage());
            }
            
            return ResponseEntity.status(HttpStatus.CREATED).body(doctorMapper.toJpaEntity(saved));

        } catch (IllegalArgumentException e) {
            log.warn("⚠️ Validación fallida al crear doctor: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("❌ Error al crear doctor: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear doctor: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable Long id,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String apellido,
            @RequestParam(required = false) String telefono,
            @RequestParam(required = false) String correo,
            @RequestParam(required = false) Long especialidadId,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen) {

        try {
            log.info("📥 PUT /doctores/{}", id);
            log.info("  - nombre: {}, apellido: {}", nombre, apellido);
            log.info("  - correo: {}, telefono: {}", correo, telefono);
            log.info("  - especialidadId: {}", especialidadId);
            log.info("  - imagenFile: {}", imagen != null ? imagen.getOriginalFilename() : "null");
            
            Doctor existente = doctorApplicationService.getDoctorById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Doctor no encontrado"));

            // Actualizar solo los campos que vienen
            if (nombre != null && !nombre.trim().isEmpty()) {
                existente.setNombre(nombre.trim());
            }
            if (apellido != null && !apellido.trim().isEmpty()) {
                existente.setApellido(apellido.trim());
            }
            if (telefono != null && !telefono.trim().isEmpty()) {
                existente.setTelefono(telefono.trim());
            }
            if (correo != null && !correo.trim().isEmpty()) {
                existente.setCorreo(correo.trim());
            }
            if (especialidadId != null) {
                existente.setEspecialidadId(especialidadId);
            }

            // Guardar imagen si viene
            if (imagen != null && !imagen.isEmpty()) {
                String imagenGuardada = guardarImagen(imagen);
                if (imagenGuardada != null) {
                    existente.setImagen(imagenGuardada);
                    log.info("  - nueva imagen guardada: {}", imagenGuardada);
                }
            }

            Doctor doctorGuardado = doctorApplicationService.updateDoctor(id, existente);
            log.info("✅ Doctor actualizado exitosamente: {}", id);
            
            // 🔔 Enviar notificación de actualización de doctor
            try {
                String mensaje = String.format("Información actualizada: Dr. %s %s", 
                    doctorGuardado.getNombre(), doctorGuardado.getApellido());
                notificationService.notifyDoctorUpdate(
                    String.valueOf(id), 
                    mensaje, 
                    doctorMapper.toJpaEntity(doctorGuardado)
                );
                log.info("✅ Notificación de actualización de doctor enviada");
            } catch (Exception notifEx) {
                log.warn("⚠️ Error enviando notificación de actualización de doctor: {}", notifEx.getMessage());
            }
            
            return ResponseEntity.ok(doctorMapper.toJpaEntity(doctorGuardado));

        } catch (IllegalArgumentException e) {
            log.warn("⚠️ Error de validación al actualizar doctor: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IOException e) {
            log.error("❌ Error al guardar imagen: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al guardar imagen: " + e.getMessage());
        } catch (Exception e) {
            log.error("❌ Error al actualizar doctor: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar doctor: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            doctorApplicationService.deleteDoctor(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.warn("Error al eliminar doctor: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error al eliminar doctor: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar doctor");
        }
    }

    private String guardarImagen(MultipartFile file) throws IOException {
        if (file.getSize() > MAX_SIZE) throw new IOException("El archivo excede los 5MB permitidos.");

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new IOException("El nombre del archivo es inválido");
        }
        String extension = "";
        if (originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        }
        if (!EXTENSIONES_PERMITIDAS.contains(extension)) {
            throw new IOException("Formato no permitido. Solo: " + EXTENSIONES_PERMITIDAS);
        }

        String base = appUploadDir == null ? "uploads/" : appUploadDir;
        if (!base.endsWith("/")) base = base + "/";
        File uploadDir = new File(base + "doctores/");
        if (!uploadDir.exists()) uploadDir.mkdirs();

        String sanitized = originalFilename.replaceAll("[^a-zA-Z0-9.-]", "_");
        String fileName = System.currentTimeMillis() + "_" + sanitized;

        File destino = new File(uploadDir, fileName);
        file.transferTo(destino);
        
        // Retornar la ruta accesible desde el frontend
        String rutaAccesible = "/uploads/doctores/" + fileName;
        log.info("✅ Imagen de doctor guardada en: {}", destino.getAbsolutePath());
        log.info("📍 Ruta accesible: {}", rutaAccesible);
        return rutaAccesible;
    }

    @GetMapping("/imagen/{filename:.+}")
    public ResponseEntity<byte[]> servirImagen(@PathVariable String filename) {
        try {
            String base = appUploadDir == null ? "uploads/" : appUploadDir;
            if (!base.endsWith("/")) base = base + "/";
            File imgFile = new File(base + "doctores/" + filename);
            if (!imgFile.exists()) {
                return ResponseEntity.notFound().build();
            }
            byte[] bytes = java.nio.file.Files.readAllBytes(imgFile.toPath());
            MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
            if (filename != null) {
                if (filename.endsWith(".png")) mediaType = MediaType.IMAGE_PNG;
                else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) mediaType = MediaType.IMAGE_JPEG;
                else if (filename.endsWith(".webp")) {
                    MediaType webpType = MediaType.valueOf("image/webp");
                    if (webpType != null) mediaType = webpType;
                }
            }
            if (mediaType != null) {
                return ResponseEntity.ok().contentType(mediaType).body(bytes);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
