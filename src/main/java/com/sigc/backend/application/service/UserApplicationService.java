package com.sigc.backend.application.service;

import com.sigc.backend.application.mapper.UsuarioMapper;
import com.sigc.backend.domain.exception.DomainException;
import com.sigc.backend.domain.port.IUsuarioRepository;

/**
 * Application Service: Usuarios
 * 
 * Responsabilidad única: Orquestar operaciones sobre usuarios.
 * 
 * Operaciones:
 * - Obtener usuario por ID
 * - Obtener usuario por email
 * - Contar usuarios
 * - (Futuros: UpdateUserUseCase, DeleteUserUseCase)
 * 
 * Principios aplicados:
 * - SRP: Solo gestiona usuarios
 * - DIP: Depende de interfaces/puertos
 * - MVC: Separa lógica de negocio (domain) de HTTP (controller)
 */
public class UserApplicationService {
    
    private final IUsuarioRepository usuarioRepository;
    
    public UserApplicationService(IUsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }
    
    /**
     * Obtiene un usuario por ID.
     * 
     * @param usuarioId ID del usuario
     * @return DTO del usuario
     */
    public UsuarioMapper.UsuarioDTO getUserById(Long usuarioId) {
        var usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new DomainException("Usuario no encontrado", "USER_NOT_FOUND"));
        return UsuarioMapper.toDTO(usuario);
    }
    
    /**
     * Obtiene un usuario por email.
     * 
     * @param email Email del usuario
     * @return DTO del usuario
     */
    public UsuarioMapper.UsuarioDTO getUserByEmail(String email) {
        var usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new DomainException("Usuario no encontrado", "USER_NOT_FOUND"));
        return UsuarioMapper.toDTO(usuario);
    }
    
    /**
     * Verifica si un email ya está registrado.
     * 
     * @param email Email a verificar
     * @return true si existe, false en caso contrario
     */
    public boolean emailExists(String email) {
        return usuarioRepository.existsByEmail(email);
    }
    
    /**
     * Cuenta el total de usuarios en el sistema.
     * 
     * @return Total de usuarios
     */
    public Long countUsers() {
        return usuarioRepository.count();
    }
}
