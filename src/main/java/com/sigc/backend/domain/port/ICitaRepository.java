package com.sigc.backend.domain.port;

import com.sigc.backend.domain.model.Cita;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida: Contrato para persistencia de Cita.
 * 
 * Define qué operaciones se necesitan sin especificar cómo se implementan.
 * Aplica DIP: El dominio depende de abstracciones, no de detalles de persistencia.
 */
public interface ICitaRepository {
    
    /**
     * Busca una cita por su ID.
     * 
     * @param id ID de la cita
     * @return Cita si existe, Optional.empty() si no
     */
    Optional<Cita> findById(Long id);
    
    /**
     * Obtiene todas las citas de un usuario.
     * 
     * @param usuarioId ID del usuario
     * @return Lista de citas del usuario
     */
    List<Cita> findByUsuarioId(Long usuarioId);
    
    /**
     * Obtiene todas las citas de un doctor.
     * 
     * @param doctorId ID del doctor
     * @return Lista de citas del doctor
     */
    List<Cita> findByDoctorId(Long doctorId);
    
    /**
     * Guarda (crea o actualiza) una cita.
     * 
     * @param cita Cita a guardar
     * @return Cita guardada con ID asignado
     */
    Cita save(Cita cita);
    
    /**
     * Elimina una cita.
     * 
     * @param id ID de la cita a eliminar
     */
    void deleteById(Long id);
    
    /**
     * Obtiene el total de citas.
     * 
     * @return Total de citas
     */
    long count();

    /**
     * Obtiene todas las citas.
     *
     * @return Lista de todas las citas
     */
    java.util.List<Cita> findAll();
}
