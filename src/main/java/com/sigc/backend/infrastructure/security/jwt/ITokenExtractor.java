package com.sigc.backend.infrastructure.security.jwt;

/**
 * Puerto de salida: Responsable únicamente de extraer información de tokens JWT.
 * 
 * Aplica ISP (Interface Segregation Principle):
 * - Solo tiene 1 responsabilidad: extraer claims del token
 * - Los clientes no dependen de métodos de generación o validación
 * 
 * Aplica DIP (Dependency Inversion Principle):
 * - Los servicios dependen de esta abstracción para obtener información del token
 */
public interface ITokenExtractor {
    
    /**
     * Extrae el ID del usuario del token.
     * 
     * @param token Token JWT
     * @return ID del usuario
     */
    Long getUserIdFromToken(String token);
    
    /**
     * Extrae el email del usuario del token.
     * 
     * @param token Token JWT
     * @return Email del usuario
     */
    String getEmailFromToken(String token);
    
    /**
     * Extrae el rol del usuario del token.
     * 
     * @param token Token JWT
     * @return Rol del usuario
     */
    String getRoleFromToken(String token);
}
