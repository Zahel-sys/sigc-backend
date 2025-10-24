package com.sigc.backend.controller;

import com.sigc.backend.model.Doctor;
import com.sigc.backend.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/doctores")
@CrossOrigin(origins = "*")
public class DoctorController {

    @Autowired
    private DoctorRepository doctorRepository;

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
        }

        return ResponseEntity.ok(doctorRepository.save(existente));
    }

    // ==========================
    // ELIMINAR DOCTOR
    // ==========================
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        doctorRepository.deleteById(id);
    }

    // ==========================
    // LISTAR POR ESPECIALIDAD
    // ==========================
    @GetMapping("/especialidad/{nombre}")
    public List<Doctor> listarPorEspecialidad(@PathVariable String nombre) {
        return doctorRepository.findByEspecialidad(nombre);
    }
}
