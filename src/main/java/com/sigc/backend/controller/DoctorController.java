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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/doctores")
@CrossOrigin(origins = {"http://localhost:5173"})
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorRepository doctorRepository;
   private final String BASE_UPLOAD_DIR = "C:/sigc/uploads/doctores/";

    private final List<String> EXTENSIONES_PERMITIDAS = Arrays.asList("jpg", "jpeg", "png", "webp");
    private final long MAX_SIZE = 5 * 1024 * 1024;

    @GetMapping
    public List<Doctor> listar() {
        try {
            return doctorRepository.findAll();
        } catch (Exception e) {
            log.error("Error al listar doctores: {}", e.getMessage());
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
            Doctor doctor = new Doctor();
            doctor.setNombre(nombre);
            doctor.setEspecialidad(especialidad);
            doctor.setCupoPacientes(cupoPacientes);

            if (imagen != null && !imagen.isEmpty()) {
                doctor.setImagen(guardarImagen(imagen));
            }

            Doctor saved = doctorRepository.save(doctor);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);

        } catch (Exception e) {
            log.error("Error al crear doctor: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear doctor: " + e.getMessage());
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
            Doctor existente = doctorRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Doctor no encontrado"));

            existente.setNombre(nombre);
            existente.setEspecialidad(especialidad);
            existente.setCupoPacientes(cupoPacientes);

            if (imagen != null && !imagen.isEmpty()) {
                existente.setImagen(guardarImagen(imagen));
            }

            return ResponseEntity.ok(doctorRepository.save(existente));

        } catch (Exception e) {
            log.error("Error al actualizar doctor: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar doctor: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            doctorRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar doctor");
        }
    }

    private String guardarImagen(MultipartFile file) throws IOException {
        if (file.getSize() > MAX_SIZE) throw new IOException("El archivo excede los 5MB permitidos.");

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        }
        if (!EXTENSIONES_PERMITIDAS.contains(extension)) {
            throw new IOException("Formato no permitido. Solo: " + EXTENSIONES_PERMITIDAS);
        }

        File uploadDir = new File(BASE_UPLOAD_DIR);
        if (!uploadDir.exists()) uploadDir.mkdirs();

        String sanitized = originalFilename.replaceAll("[^a-zA-Z0-9.-]", "_");
        String fileName = System.currentTimeMillis() + "_" + sanitized;

        File destino = new File(uploadDir, fileName);
        file.transferTo(destino);
        System.out.println("Archivo guardado en: " + destino.getAbsolutePath());

        // Retornar solo el nombre del archivo
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
            MediaType mediaType;
            if (filename.endsWith(".png")) mediaType = MediaType.IMAGE_PNG;
            else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) mediaType = MediaType.IMAGE_JPEG;
            else if (filename.endsWith(".webp")) mediaType = MediaType.valueOf("image/webp");
            else mediaType = MediaType.APPLICATION_OCTET_STREAM;
            return ResponseEntity.ok().contentType(mediaType).body(bytes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
