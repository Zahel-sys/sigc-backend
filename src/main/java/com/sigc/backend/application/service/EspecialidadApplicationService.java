package com.sigc.backend.application.service;

import com.sigc.backend.domain.model.Especialidad;
import com.sigc.backend.domain.port.IEspecialidadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de aplicación para Especialidad
 * Orquesta operaciones de negocio relacionadas con especialidades
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EspecialidadApplicationService {
    
    private final IEspecialidadRepository especialidadRepository;
    
    /**
     * Obtiene todas las especialidades
     */
    @Transactional(readOnly = true)
    public List<Especialidad> getAllEspecialidades() {
        log.info("Obteniendo todas las especialidades");
        return especialidadRepository.findAll();
    }
    
    /**
     * Obtiene una especialidad por ID
     */
    @Transactional(readOnly = true)
    public Optional<Especialidad> getEspecialidadById(Long id) {
        log.info("Buscando especialidad con ID: {}", id);
        return especialidadRepository.findById(id);
    }
    
    /**
     * Obtiene una especialidad por nombre
     */
    @Transactional(readOnly = true)
    public Optional<Especialidad> getEspecialidadByNombre(String nombre) {
        log.info("Buscando especialidad con nombre: {}", nombre);
        return especialidadRepository.findByNombre(nombre);
    }
    
    /**
     * Crea una nueva especialidad
     */
    @Transactional
    public Especialidad createEspecialidad(Especialidad especialidad) {
        log.info("Creando nueva especialidad: {}", especialidad.getNombre());
        
        if (!especialidad.isValid()) {
            throw new IllegalArgumentException("Datos de la especialidad inválidos");
        }
        
        // Verificar que no exista otra especialidad con el mismo nombre
        if (especialidadRepository.findByNombre(especialidad.getNombre()).isPresent()) {
            throw new IllegalArgumentException("Ya existe una especialidad con ese nombre");
        }
        
        return especialidadRepository.save(especialidad);
    }
    
    /**
     * Actualiza una especialidad existente
     */
    @Transactional
    public Especialidad updateEspecialidad(Long id, Especialidad especialidad) {
        log.info("Actualizando especialidad con ID: {}", id);
        
        if (!especialidadRepository.existsById(id)) {
            throw new IllegalArgumentException("Especialidad no encontrada con ID: " + id);
        }
        
        if (!especialidad.isValid()) {
            throw new IllegalArgumentException("Datos de la especialidad inválidos");
        }
        
        especialidad.setIdEspecialidad(id);
        return especialidadRepository.save(especialidad);
    }
    
    /**
     * Elimina una especialidad
     */
    @Transactional
    public void deleteEspecialidad(Long id) {
        log.info("Eliminando especialidad con ID: {}", id);
        
        if (!especialidadRepository.existsById(id)) {
            throw new IllegalArgumentException("Especialidad no encontrada con ID: " + id);
        }
        
        especialidadRepository.deleteById(id);
    }
}
