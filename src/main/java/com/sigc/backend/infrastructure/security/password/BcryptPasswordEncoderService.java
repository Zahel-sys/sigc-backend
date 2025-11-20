package com.sigc.backend.infrastructure.security.password;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Implementación de IPasswordEncoder usando BCrypt.
 * 
 * Ventajas:
 * - Encapsula BCryptPasswordEncoder (no lo expone directamente)
 * - Fácil de testear
 * - Fácil de reemplazar (ej. cambiar a otro algoritmo)
 * - Sigue ISP: solo tiene 2 métodos relacionados
 * - Sigue DIP: depende de abstracción, no de BCryptPasswordEncoder
 */
@Component
public class BcryptPasswordEncoderService implements IPasswordEncoder {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * Codifica una contraseña usando BCrypt.
     * 
     * @param rawPassword Contraseña en texto plano
     * @return Contraseña codificada
     */
    @Override
    public String encodePassword(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    /**
     * Valida si una contraseña en texto plano coincide con la codificada.
     * 
     * @param rawPassword Contraseña en texto plano
     * @param encodedPassword Contraseña codificada
     * @return true si coinciden, false en caso contrario
     */
    @Override
    public boolean matchesPassword(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}
