package com.sigc.backend.application.service;

import com.sigc.backend.domain.model.Horario;
import com.sigc.backend.domain.port.IHorarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de aplicación para Horario
 * Orquesta operaciones de negocio relacionadas con horarios
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class HorarioApplicationService {
    
    private final IHorarioRepository horarioRepository;
    
    /**
     * Obtiene todos los horarios
     */
    @Transactional(readOnly = true)
    public List<Horario> getAllHorarios() {
        log.info("Obteniendo todos los horarios");
        return horarioRepository.findAll();
    }
    
    /**
     * Obtiene un horario por ID
     */
    @Transactional(readOnly = true)
    public Optional<Horario> getHorarioById(Long id) {
        log.info("Buscando horario con ID: {}", id);
        return horarioRepository.findById(id);
    }
    
    /**
     * Obtiene horarios por doctor
     */
    @Transactional(readOnly = true)
    public List<Horario> getHorariosByDoctor(Long idDoctor) {
        log.info("Buscando horarios para doctor ID: {}", idDoctor);
        return horarioRepository.findByIdDoctor(idDoctor);
    }
    
    /**
     * Obtiene horarios disponibles
     */
    @Transactional(readOnly = true)
    public List<Horario> getHorariosDisponibles() {
        log.info("Obteniendo horarios disponibles");
        return horarioRepository.findByDisponibleTrue();
    }
    
    /**
     * Obtiene horarios por fecha
     */
    @Transactional(readOnly = true)
    public List<Horario> getHorariosByFecha(LocalDate fecha) {
        log.info("Buscando horarios para fecha: {}", fecha);
        return horarioRepository.findByFecha(fecha);
    }
    
    /**
     * Obtiene horarios disponibles por doctor y fecha
     */
    @Transactional(readOnly = true)
    public List<Horario> getHorariosDisponiblesByDoctorAndFecha(Long idDoctor, LocalDate fecha) {
        log.info("Buscando horarios disponibles para doctor {} en fecha {}", idDoctor, fecha);
        return horarioRepository.findByIdDoctorAndFechaAndDisponibleTrue(idDoctor, fecha);
    }
    
    /**
     * Crea un nuevo horario
     */
    @Transactional
    public Horario createHorario(Horario horario) {
        log.info("Creando nuevo horario para doctor ID: {}", horario.getIdDoctor());
        
        if (!horario.isValid()) {
            throw new IllegalArgumentException("Datos del horario inválidos");
        }
        
        return horarioRepository.save(horario);
    }
    
    /**
     * Actualiza un horario existente
     */
    @Transactional
    public Horario updateHorario(Long id, Horario horario) {
        log.info("Actualizando horario con ID: {}", id);
        
        if (!horarioRepository.existsById(id)) {
            throw new IllegalArgumentException("Horario no encontrado con ID: " + id);
        }
        
        if (!horario.isValid()) {
            throw new IllegalArgumentException("Datos del horario inválidos");
        }
        
        horario.setIdHorario(id);
        return horarioRepository.save(horario);
    }
    
    /**
     * Marca un horario como no disponible
     */
    @Transactional
    public void marcarHorarioNoDisponible(Long id) {
        log.info("Marcando horario {} como no disponible", id);
        
        Horario horario = horarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Horario no encontrado con ID: " + id));
        
        horario.setDisponible(false);
        horarioRepository.save(horario);
    }
    
    /**
     * Elimina un horario
     */
    @Transactional
    public void deleteHorario(Long id) {
        log.info("Eliminando horario con ID: {}", id);
        
        if (!horarioRepository.existsById(id)) {
            throw new IllegalArgumentException("Horario no encontrado con ID: " + id);
        }
        
        horarioRepository.deleteById(id);
    }
}
