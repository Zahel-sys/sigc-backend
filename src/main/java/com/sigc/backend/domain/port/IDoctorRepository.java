package com.sigc.backend.domain.port;

import com.sigc.backend.domain.model.Doctor;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para Doctor
 * Define el contrato para persistencia de doctores (DIP)
 */
public interface IDoctorRepository {
    
    /**
     * Encuentra todos los doctores
     */
    List<Doctor> findAll();
    
    /**
     * Encuentra un doctor por ID
     */
    Optional<Doctor> findById(Long id);
    
    /**
     * Encuentra doctores por especialidad
     */
    List<Doctor> findByEspecialidad(String especialidad);
    
    /**
     * Guarda un doctor
     */
    Doctor save(Doctor doctor);
    
    /**
     * Elimina un doctor por ID
     */
    void deleteById(Long id);
    
    /**
     * Verifica si existe un doctor por ID
     */
    boolean existsById(Long id);
}
