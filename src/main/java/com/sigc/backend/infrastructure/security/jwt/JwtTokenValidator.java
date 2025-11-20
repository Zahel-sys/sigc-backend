package com.sigc.backend.infrastructure.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;

/**
 * Implementación de ITokenValidator.
 * Responsable únicamente de validar tokens JWT.
 * 
 * Ventajas:
 * - Implementa una única responsabilidad (SRP)
 * - Fácil de testear (interfaz clara)
 * - Centraliza la lógica de validación
 */
@Component
public class JwtTokenValidator implements ITokenValidator {

    private static final String SECRET_KEY = "supersecreta123456supersecreta123456";

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    /**
     * Valida si un token JWT es válido (firmado correctamente).
     * 
     * @param token Token a validar
     * @return true si el token es válido, false en caso contrario
     */
    @Override
    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Valida si un token no ha expirado.
     * 
     * @param token Token a validar
     * @return true si el token está vigente, false si expiró
     */
    @Override
    public boolean isTokenNotExpired(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return !claims.getExpiration().before(new java.util.Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
