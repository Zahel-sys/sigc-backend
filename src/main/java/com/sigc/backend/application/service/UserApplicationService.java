package com.sigc.backend.application.service;

import com.sigc.backend.application.mapper.UsuarioMapper;
import com.sigc.backend.domain.exception.DomainException;
import com.sigc.backend.domain.model.Usuario;
import com.sigc.backend.domain.port.IUsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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
@Service
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
    public com.sigc.backend.domain.model.Usuario getUserById(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new DomainException("Usuario no encontrado", "USER_NOT_FOUND"));
    }
    
    /**
     * Obtiene un usuario por email.
     * 
     * @param email Email del usuario
     * @return DTO del usuario
     */
    public com.sigc.backend.domain.model.Usuario getUserByEmail(String email) {
        return usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new DomainException("Usuario no encontrado", "USER_NOT_FOUND"));
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

    // ----- Additional convenience methods used by controllers -----
    public List<Usuario> listAllUsers() {
        return usuarioRepository.findAll();
    }

    public Usuario createUser(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public Usuario updateUser(Long id, Usuario usuario) {
        var existente = usuarioRepository.findById(id)
                .orElseThrow(() -> new DomainException("Usuario no encontrado", "USER_NOT_FOUND"));
        existente.setNombre(usuario.getNombre());
        existente.setEmail(usuario.getEmail());
        existente.setRole(usuario.getRole());
        existente.setActivo(usuario.getActivo());
        return usuarioRepository.save(existente);
    }

    public void deleteUser(Long id) {
        usuarioRepository.deleteById(id);
    }
}
