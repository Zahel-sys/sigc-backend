package com.sigc.backend.domain.port;

import com.sigc.backend.domain.model.Especialidad;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para Especialidad
 * Define el contrato para persistencia de especialidades (DIP)
 */
public interface IEspecialidadRepository {
    
    /**
     * Encuentra todas las especialidades
     */
    List<Especialidad> findAll();
    
    /**
     * Encuentra una especialidad por ID
     */
    Optional<Especialidad> findById(Long id);
    
    /**
     * Encuentra una especialidad por nombre
     */
    Optional<Especialidad> findByNombre(String nombre);
    
    /**
     * Guarda una especialidad
     */
    Especialidad save(Especialidad especialidad);
    
    /**
     * Elimina una especialidad por ID
     */
    void deleteById(Long id);
    
    /**
     * Verifica si existe una especialidad por ID
     */
    boolean existsById(Long id);
}
