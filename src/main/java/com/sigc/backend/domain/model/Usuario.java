package com.sigc.backend.domain.model;

import java.time.LocalDateTime;

/**
 * Entidad de dominio: Usuario
 * 
 * Responsabilidad única: Representar un usuario del sistema.
 * 
 * Atributos del usuario:
 * - id: Identificador único
 * - email: Correo electrónico (único)
 * - password: Contraseña encriptada
 * - nombre: Nombre del usuario
 * - apellido: Apellido del usuario
 * - telefono: Teléfono de contacto
 * - dni: Documento de identidad (único)
 * - role: Rol del usuario (USER, DOCTOR, ADMIN)
 * - activo: Si está habilitado o no
 * - createdAt: Fecha de creación
 * - updatedAt: Fecha de última actualización
 * 
 * Principo DDD: Entidad que representa la identidad del usuario
 * Principio SRP: Solo representa un usuario
 */
public class Usuario {
    
    private Long id;
    private String email;
    private String password;
    private String nombre;
    private String apellido;
    private String telefono;
    private String dni;
    private String role;
    private Boolean activo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructor por defecto
    public Usuario() {}
    
    // Constructor completo
    public Usuario(Long id, String email, String password, String nombre, String apellido,
                   String telefono, String dni, String role, Boolean activo,
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.dni = dni;
        this.role = role;
        this.activo = activo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getTelefono() { return telefono; }
    public String getDni() { return dni; }
    public String getRole() { return role; }
    public Boolean getActivo() { return activo; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    
    // Setters
    public void setId(Long id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setDni(String dni) { this.dni = dni; }
    public void setRole(String role) { this.role = role; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", role='" + role + '\'' +
                ", activo=" + activo +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
