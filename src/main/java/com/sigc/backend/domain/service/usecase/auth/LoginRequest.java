package com.sigc.backend.domain.service.usecase.auth;

/**
 * DTO: Solicitud de Login.
 * Transporta datos desde la entrada (HTTP) hacia el use case.
 */
public class LoginRequest {
    private final String email;
    private final String password;
    
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getPassword() {
        return password;
    }
}
