package com.sigc.backend.domain.port;

import com.sigc.backend.domain.model.Usuario;
import java.util.Optional;

/**
 * Puerto de salida: Contrato para persistencia de Usuario.
 * 
 * Define qué operaciones se necesitan sin especificar cómo se implementan.
 * Aplica DIP: El dominio depende de abstracciones, no de detalles de persistencia.
 * 
 * Las implementaciones pueden ser:
 * - Base de datos (JPA)
 * - Memoria (caché)
 * - API remota
 * - Sistema de archivos
 */
public interface IUsuarioRepository {
    
    /**
     * Busca un usuario por su ID.
     * 
     * @param id ID del usuario
     * @return Usuario si existe, Optional.empty() si no
     */
    Optional<Usuario> findById(Long id);
    
    /**
     * Busca un usuario por su email.
     * 
     * @param email Email del usuario
     * @return Usuario si existe, Optional.empty() si no
     */
    Optional<Usuario> findByEmail(String email);
    
    /**
     * Verifica si existe un usuario con un email específico.
     * 
     * @param email Email a verificar
     * @return true si existe, false si no
     */
    boolean existsByEmail(String email);
    
    /**
     * Guarda (crea o actualiza) un usuario.
     * 
     * @param usuario Usuario a guardar
     * @return Usuario guardado con ID asignado
     */
    Usuario save(Usuario usuario);
    
    /**
     * Obtiene el total de usuarios.
     * 
     * @return Total de usuarios
     */
    long count();
}
