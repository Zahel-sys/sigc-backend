package com.sigc.backend.infrastructure.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;

/**
 * Implementación de ITokenExtractor.
 * Responsable únicamente de extraer información de tokens JWT.
 * 
 * Ventajas:
 * - Implementa una única responsabilidad (SRP)
 * - Fácil de testear (interfaz clara)
 * - Centraliza la extracción de claims
 */
@Component
public class JwtTokenClaimsExtractor implements ITokenExtractor {

    private static final String SECRET_KEY = "supersecreta123456supersecreta123456";

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Extrae el ID del usuario del token.
     * 
     * @param token Token JWT
     * @return ID del usuario
     */
    @Override
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return Long.parseLong(claims.getSubject());
    }

    /**
     * Extrae el email del usuario del token.
     * 
     * @param token Token JWT
     * @return Email del usuario
     */
    @Override
    public String getEmailFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("email", String.class);
    }

    /**
     * Extrae el rol del usuario del token.
     * 
     * @param token Token JWT
     * @return Rol del usuario
     */
    @Override
    public String getRoleFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("rol", String.class);
    }
}
