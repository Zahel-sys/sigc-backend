package com.sigc.backend.application.service;

import com.sigc.backend.domain.model.Doctor;
import com.sigc.backend.domain.port.IDoctorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de aplicación para Doctor
 * Orquesta operaciones de negocio relacionadas con doctores
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorApplicationService {
    
    private final IDoctorRepository doctorRepository;
    
    /**
     * Obtiene todos los doctores
     */
    @Transactional(readOnly = true)
    public List<Doctor> getAllDoctors() {
        log.info("Obteniendo todos los doctores");
        return doctorRepository.findAll();
    }
    
    /**
     * Obtiene un doctor por ID
     */
    @Transactional(readOnly = true)
    public Optional<Doctor> getDoctorById(Long id) {
        log.info("Buscando doctor con ID: {}", id);
        return doctorRepository.findById(id);
    }
    
    /**
     * Obtiene doctores por especialidad
     */
    @Transactional(readOnly = true)
    public List<Doctor> getDoctorsByEspecialidad(String especialidad) {
        log.info("Buscando doctores con especialidad: {}", especialidad);
        return doctorRepository.findByEspecialidad(especialidad);
    }
    
    /**
     * Crea un nuevo doctor
     */
    @Transactional
    public Doctor createDoctor(Doctor doctor) {
        log.info("Creando nuevo doctor: {}", doctor.getNombre());
        
        if (!doctor.isValid()) {
            throw new IllegalArgumentException("Datos del doctor inválidos");
        }
        
        return doctorRepository.save(doctor);
    }
    
    /**
     * Actualiza un doctor existente
     */
    @Transactional
    public Doctor updateDoctor(Long id, Doctor doctor) {
        log.info("Actualizando doctor con ID: {}", id);
        
        if (!doctorRepository.existsById(id)) {
            throw new IllegalArgumentException("Doctor no encontrado con ID: " + id);
        }
        
        if (!doctor.isValid()) {
            throw new IllegalArgumentException("Datos del doctor inválidos");
        }
        
        doctor.setIdDoctor(id);
        return doctorRepository.save(doctor);
    }
    
    /**
     * Elimina un doctor
     */
    @Transactional
    public void deleteDoctor(Long id) {
        log.info("Eliminando doctor con ID: {}", id);
        
        if (!doctorRepository.existsById(id)) {
            throw new IllegalArgumentException("Doctor no encontrado con ID: " + id);
        }
        
        doctorRepository.deleteById(id);
    }
}
