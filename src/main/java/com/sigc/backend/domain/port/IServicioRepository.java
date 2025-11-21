package com.sigc.backend.domain.port;

import com.sigc.backend.domain.model.Servicio;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para Servicio
 * Define el contrato para persistencia de servicios (DIP)
 */
public interface IServicioRepository {
    
    /**
     * Encuentra todos los servicios
     */
    List<Servicio> findAll();
    
    /**
     * Encuentra un servicio por ID
     */
    Optional<Servicio> findById(Long id);
    
    /**
     * Encuentra servicios por nombre
     */
    List<Servicio> findByNombreServicioContaining(String nombre);
    
    /**
     * Guarda un servicio
     */
    Servicio save(Servicio servicio);
    
    /**
     * Elimina un servicio por ID
     */
    void deleteById(Long id);
    
    /**
     * Verifica si existe un servicio por ID
     */
    boolean existsById(Long id);
}
