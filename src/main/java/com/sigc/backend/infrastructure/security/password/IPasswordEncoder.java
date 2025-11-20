package com.sigc.backend.infrastructure.security.password;

/**
 * Puerto de salida: Responsable únicamente de operaciones de contraseña.
 * 
 * Aplica ISP (Interface Segregation Principle):
 * - Solo tiene 2 responsabilidades relacionadas: codificar y validar
 * - Los clientes dependen de una interfaz clara y simple
 * 
 * Aplica DIP (Dependency Inversion Principle):
 * - Los controladores dependen de esta abstracción, no de BCryptPasswordEncoder directamente
 */
public interface IPasswordEncoder {
    
    /**
     * Codifica una contraseña en texto plano.
     * 
     * @param rawPassword Contraseña en texto plano
     * @return Contraseña codificada
     */
    String encodePassword(String rawPassword);
    
    /**
     * Valida si una contraseña en texto plano coincide con la codificada.
     * 
     * @param rawPassword Contraseña en texto plano
     * @param encodedPassword Contraseña codificada
     * @return true si coinciden, false en caso contrario
     */
    boolean matchesPassword(String rawPassword, String encodedPassword);
}
