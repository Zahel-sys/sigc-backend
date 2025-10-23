package com.sigc.backend.controller;

import com.sigc.backend.model.Doctor;
import com.sigc.backend.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/doctores")
@CrossOrigin(origins = "*")
public class DoctorController {

    @Autowired
    private DoctorRepository doctorRepository;

    // 🧾 Listar doctores
    @GetMapping
    public List<Doctor> listar() {
        return doctorRepository.findAll();
    }

    // ➕ Crear nuevo doctor
    @PostMapping
    public ResponseEntity<Doctor> crear(@Valid @RequestBody Doctor doctor) {
        return ResponseEntity.ok(doctorRepository.save(doctor));
    }

    // ✏️ Actualizar doctor existente
    @PutMapping("/{id}")
    public ResponseEntity<Doctor> actualizar(@PathVariable Long id, @Valid @RequestBody Doctor doctor) {
        Doctor existente = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor no encontrado con id " + id));

        existente.setNombre(doctor.getNombre());
        existente.setEspecialidad(doctor.getEspecialidad());
        existente.setCupoPacientes(doctor.getCupoPacientes());

        // 🔹 Agregamos soporte para actualizar la imagen
        if (doctor.getImagen() != null && !doctor.getImagen().isEmpty()) {
            existente.setImagen(doctor.getImagen());
        }

        return ResponseEntity.ok(doctorRepository.save(existente));
    }

    // 🗑️ Eliminar doctor
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        doctorRepository.deleteById(id);
    }
}
