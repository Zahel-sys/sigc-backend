package com.sigc.backend.infrastructure.security.jwt;

/**
 * Puerto de salida: Responsable únicamente de generar tokens JWT.
 * 
 * Aplica ISP (Interface Segregation Principle):
 * - Solo tiene 1 responsabilidad: generar tokens
 * - Los clientes no dependen de métodos innecesarios
 * 
 * Aplica DIP (Dependency Inversion Principle):
 * - Los controladores dependen de esta abstracción, no de JwtUtil directamente
 */
public interface ITokenProvider {
    
    /**
     * Genera un token JWT con los claims especificados.
     * 
     * @param userId ID del usuario
     * @param email Email del usuario
     * @param role Rol del usuario
     * @return Token JWT generado
     */
    String generateToken(Long userId, String email, String role);
}
