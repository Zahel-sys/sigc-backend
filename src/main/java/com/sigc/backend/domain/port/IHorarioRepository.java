package com.sigc.backend.domain.port;

import com.sigc.backend.domain.model.Horario;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para Horario
 * Define el contrato para persistencia de horarios (DIP)
 */
public interface IHorarioRepository {
    
    /**
     * Encuentra todos los horarios
     */
    List<Horario> findAll();
    
    /**
     * Encuentra un horario por ID
     */
    Optional<Horario> findById(Long id);
    
    /**
     * Encuentra horarios por ID de doctor
     */
    List<Horario> findByIdDoctor(Long idDoctor);
    
    /**
     * Encuentra horarios disponibles
     */
    List<Horario> findByDisponibleTrue();
    
    /**
     * Encuentra horarios por fecha
     */
    List<Horario> findByFecha(LocalDate fecha);
    
    /**
     * Encuentra horarios disponibles por doctor y fecha
     */
    List<Horario> findByIdDoctorAndFechaAndDisponibleTrue(Long idDoctor, LocalDate fecha);
    
    /**
     * Guarda un horario
     */
    Horario save(Horario horario);
    
    /**
     * Elimina un horario por ID
     */
    void deleteById(Long id);
    
    /**
     * Verifica si existe un horario por ID
     */
    boolean existsById(Long id);
}
