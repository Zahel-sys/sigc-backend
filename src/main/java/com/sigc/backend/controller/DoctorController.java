package com.sigc.backend.controller;

import com.sigc.backend.model.Doctor;
import com.sigc.backend.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

<<<<<<< HEAD
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
=======
import java.io.File;
import java.io.IOException;
import java.util.Collections;
>>>>>>> origin/feature/resoluciones-y-JWT
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/doctores")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175"})
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorRepository doctorRepository;

<<<<<<< HEAD
    private static final String UPLOAD_DIR = "src/main/resources/uploads/";

    // ==========================
    // LISTAR TODOS LOS DOCTORES
    // ==========================
    @GetMapping
    public List<Doctor> listar() {
        return doctorRepository.findAll();
    }

    // ==========================
    // CREAR DOCTOR
    // ==========================
    @PostMapping
    public ResponseEntity<Doctor> crear(
            @RequestParam("nombre") String nombre,
            @RequestParam("especialidad") String especialidad,
            @RequestParam("cupoPacientes") int cupoPacientes,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen
    ) throws IOException {

        Doctor doctor = new Doctor();
        doctor.setNombre(nombre);
        doctor.setEspecialidad(especialidad);
        doctor.setCupoPacientes(cupoPacientes);

        // Guardar imagen si se envía
        if (imagen != null && !imagen.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + imagen.getOriginalFilename();
            Path path = Paths.get(UPLOAD_DIR + fileName);
            Files.createDirectories(path.getParent());
            imagen.transferTo(path.toFile());
            doctor.setImagen("/uploads/" + fileName);
        }

        return ResponseEntity.ok(doctorRepository.save(doctor));
    }

    // ==========================
    // ACTUALIZAR DOCTOR
    // ==========================
    @PutMapping("/{id}")
    public ResponseEntity<Doctor> actualizar(
            @PathVariable Long id,
            @RequestParam("nombre") String nombre,
            @RequestParam("especialidad") String especialidad,
            @RequestParam("cupoPacientes") int cupoPacientes,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen
    ) throws IOException {

        Doctor existente = doctorRepository.findById(id).orElseThrow();

        existente.setNombre(nombre);
        existente.setEspecialidad(especialidad);
        existente.setCupoPacientes(cupoPacientes);

        // Si se sube una nueva imagen, reemplazar la anterior
        if (imagen != null && !imagen.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + imagen.getOriginalFilename();
            Path path = Paths.get(UPLOAD_DIR + fileName);
            Files.createDirectories(path.getParent());
            imagen.transferTo(path.toFile());
            existente.setImagen("/uploads/" + fileName);
=======
    @GetMapping
    public List<Doctor> listar() {
        try {
            log.info("Listando todos los doctores");
            List<Doctor> doctores = doctorRepository.findAll();
            log.info("Se encontraron {} doctores", doctores.size());
            return doctores;
        } catch (Exception e) {
            log.error("Error al listar doctores: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> crear(
            @RequestParam("nombre") String nombre,
            @RequestParam("especialidad") String especialidad,
            @RequestParam("cupoPacientes") Integer cupoPacientes,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen) {
        try {
            log.info("Registrando doctor: {} - Especialidad: {}", nombre, especialidad);
            
            Doctor doctor = new Doctor();
            doctor.setNombre(nombre);
            doctor.setEspecialidad(especialidad);
            doctor.setCupoPacientes(cupoPacientes);
            
            // Procesar imagen si existe
            if (imagen != null && !imagen.isEmpty()) {
                String imageUrl = guardarImagen(imagen, "doctores");
                doctor.setImagen(imageUrl);
                log.info("Imagen del doctor guardada: {}", imageUrl);
            } else {
                log.info("No se proporcionó imagen para el doctor");
            }
            
            Doctor saved = doctorRepository.save(doctor);
            log.info("Doctor registrado exitosamente con ID: {}", saved.getIdDoctor());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            log.error("Error al registrar doctor: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear el doctor: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable Long id,
            @RequestParam("nombre") String nombre,
            @RequestParam("especialidad") String especialidad,
            @RequestParam("cupoPacientes") Integer cupoPacientes,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen) {
        try {
            log.info("Actualizando doctor con ID: {}", id);
            Doctor existente = doctorRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Doctor no encontrado con ID: " + id));

            existente.setNombre(nombre);
            existente.setEspecialidad(especialidad);
            existente.setCupoPacientes(cupoPacientes);

            // Actualizar imagen si se proporciona
            if (imagen != null && !imagen.isEmpty()) {
                String imageUrl = guardarImagen(imagen, "doctores");
                existente.setImagen(imageUrl);
                log.info("Imagen actualizada: {}", imageUrl);
            }

            Doctor actualizado = doctorRepository.save(existente);
            log.info("Doctor actualizado exitosamente: {}", id);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            log.error("Error al actualizar doctor {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar el doctor: " + e.getMessage());
>>>>>>> origin/feature/resoluciones-y-JWT
        }
    }

<<<<<<< HEAD
    // ==========================
    // ELIMINAR DOCTOR
    // ==========================
=======
>>>>>>> origin/feature/resoluciones-y-JWT
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            log.info("Eliminando doctor con ID: {}", id);
            doctorRepository.deleteById(id);
            log.info("Doctor eliminado exitosamente: {}", id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al eliminar doctor {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar el doctor");
        }
    }

    /**
     * Método auxiliar para guardar imágenes
     */
    private String guardarImagen(MultipartFile file, String folder) throws IOException {
        String uploadDir = "src/main/resources/static/images/" + folder + "/";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
            log.info("Directorio creado: {}", uploadDir);
        }
        
        // Sanitizar nombre del archivo
        String originalFilename = file.getOriginalFilename();
        String sanitizedFilename = originalFilename != null ? 
                originalFilename.replaceAll("[^a-zA-Z0-9.-]", "_") : "image.jpg";
        
        String fileName = System.currentTimeMillis() + "_" + sanitizedFilename;
        String filePath = uploadDir + fileName;
        
        File dest = new File(filePath);
        file.transferTo(dest);
        
        log.info("Imagen guardada en: {}", filePath);
        return "/images/" + folder + "/" + fileName;
    }

    // ==========================
    // LISTAR POR ESPECIALIDAD
    // ==========================
    @GetMapping("/especialidad/{nombre}")
    public List<Doctor> listarPorEspecialidad(@PathVariable String nombre) {
        return doctorRepository.findByEspecialidad(nombre);
    }
}
