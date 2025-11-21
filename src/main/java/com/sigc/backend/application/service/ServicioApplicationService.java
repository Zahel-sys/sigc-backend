package com.sigc.backend.application.service;

import com.sigc.backend.domain.model.Servicio;
import com.sigc.backend.domain.port.IServicioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de aplicación para Servicio
 * Orquesta operaciones de negocio relacionadas con servicios médicos
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ServicioApplicationService {
    
    private final IServicioRepository servicioRepository;
    
    /**
     * Obtiene todos los servicios
     */
    @Transactional(readOnly = true)
    public List<Servicio> getAllServicios() {
        log.info("Obteniendo todos los servicios");
        return servicioRepository.findAll();
    }
    
    /**
     * Obtiene un servicio por ID
     */
    @Transactional(readOnly = true)
    public Optional<Servicio> getServicioById(Long id) {
        log.info("Buscando servicio con ID: {}", id);
        return servicioRepository.findById(id);
    }
    
    /**
     * Busca servicios por nombre
     */
    @Transactional(readOnly = true)
    public List<Servicio> searchServiciosByNombre(String nombre) {
        log.info("Buscando servicios que contengan: {}", nombre);
        return servicioRepository.findByNombreServicioContaining(nombre);
    }
    
    /**
     * Crea un nuevo servicio
     */
    @Transactional
    public Servicio createServicio(Servicio servicio) {
        log.info("Creando nuevo servicio: {}", servicio.getNombreServicio());
        
        if (!servicio.isValid()) {
            throw new IllegalArgumentException("Datos del servicio inválidos");
        }
        
        return servicioRepository.save(servicio);
    }
    
    /**
     * Actualiza un servicio existente
     */
    @Transactional
    public Servicio updateServicio(Long id, Servicio servicio) {
        log.info("Actualizando servicio con ID: {}", id);
        
        if (!servicioRepository.existsById(id)) {
            throw new IllegalArgumentException("Servicio no encontrado con ID: " + id);
        }
        
        if (!servicio.isValid()) {
            throw new IllegalArgumentException("Datos del servicio inválidos");
        }
        
        servicio.setIdServicio(id);
        return servicioRepository.save(servicio);
    }
    
    /**
     * Elimina un servicio
     */
    @Transactional
    public void deleteServicio(Long id) {
        log.info("Eliminando servicio con ID: {}", id);
        
        if (!servicioRepository.existsById(id)) {
            throw new IllegalArgumentException("Servicio no encontrado con ID: " + id);
        }
        
        servicioRepository.deleteById(id);
    }
}
