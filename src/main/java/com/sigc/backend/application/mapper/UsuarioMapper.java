package com.sigc.backend.application.mapper;

import com.sigc.backend.domain.model.Usuario;
import com.sigc.backend.domain.service.usecase.auth.LoginResponse;

/**
 * Mapper: Usuario (modelo de dominio) -> DTOs de salida
 * 
 * Responsabilidad única: Convertir entre Usuario y DTOs
 * 
 * Principios aplicados:
 * - SRP: Solo mapea
 * - DIP: No depende de Spring
 */
public class UsuarioMapper {
    
    /**
     * Convierte Usuario a LoginResponse.
     * 
     * @param usuario Usuario del dominio
     * @param token Token JWT generado
     * @return LoginResponse con datos del usuario
     */
    public static LoginResponse toLoginResponse(Usuario usuario, String token) {
        return new LoginResponse(
            usuario.getId(),
            usuario.getEmail(),
            token,
            usuario.getRole()
        );
    }
    
    /**
     * Convierte Usuario a un DTO simple de usuario (solo datos públicos).
     * 
     * @param usuario Usuario del dominio
     * @return UsuarioDTO con datos públicos
     */
    public static UsuarioDTO toDTO(Usuario usuario) {
        return new UsuarioDTO(
            usuario.getId(),
            usuario.getEmail(),
            usuario.getNombre(),
            usuario.getApellido(),
            usuario.getTelefono(),
            usuario.getDni(),
            usuario.getRole(),
            usuario.getActivo(),
            usuario.getCreatedAt(),
            usuario.getUpdatedAt()
        );
    }
    
    /**
     * DTO simple de usuario (solo datos públicos, sin password).
     */
    public static class UsuarioDTO {
        private final Long id;
        private final String email;
        private final String nombre;
        private final String apellido;
        private final String telefono;
        private final String dni;
        private final String role;
        private final Boolean activo;
        private final java.time.LocalDateTime createdAt;
        private final java.time.LocalDateTime updatedAt;
        
        public UsuarioDTO(Long id, String email, String nombre, String apellido,
                         String telefono, String dni, String role, Boolean activo,
                         java.time.LocalDateTime createdAt, java.time.LocalDateTime updatedAt) {
            this.id = id;
            this.email = email;
            this.nombre = nombre;
            this.apellido = apellido;
            this.telefono = telefono;
            this.dni = dni;
            this.role = role;
            this.activo = activo;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }
        
        public Long getId() { return id; }
        public String getEmail() { return email; }
        public String getNombre() { return nombre; }
        public String getApellido() { return apellido; }
        public String getTelefono() { return telefono; }
        public String getDni() { return dni; }
        public String getRole() { return role; }
        public Boolean getActivo() { return activo; }
        public java.time.LocalDateTime getCreatedAt() { return createdAt; }
        public java.time.LocalDateTime getUpdatedAt() { return updatedAt; }
    }
}
