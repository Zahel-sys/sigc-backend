package com.sigc.backend.infrastructure.security.jwt;

/**
 * Puerto de salida: Responsable únicamente de validar tokens JWT.
 * 
 * Aplica ISP (Interface Segregation Principle):
 * - Solo tiene 1 responsabilidad: validar tokens
 * - Los clientes no dependen de métodos de generación o extracción
 * 
 * Aplica DIP (Dependency Inversion Principle):
 * - Los filtros de seguridad dependen de esta abstracción
 */
public interface ITokenValidator {
    
    /**
     * Valida si un token JWT es válido.
     * 
     * @param token Token a validar
     * @return true si el token es válido, false en caso contrario
     */
    boolean isTokenValid(String token);
    
    /**
     * Valida si un token no ha expirado.
     * 
     * @param token Token a validar
     * @return true si el token está vigente, false si expiró
     */
    boolean isTokenNotExpired(String token);
}
