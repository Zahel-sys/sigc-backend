package com.sigc.backend.domain.service.usecase.auth;

/**
 * DTO: Respuesta de Login.
 * Transporta datos desde el use case hacia la salida (HTTP).
 */
public class LoginResponse {
    private final Long userId;
    private final String email;
    private final String token;
    private final String role;
    
    public LoginResponse(Long userId, String email, String token, String role) {
        this.userId = userId;
        this.email = email;
        this.token = token;
        this.role = role;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getToken() {
        return token;
    }
    
    public String getRole() {
        return role;
    }
}
