package com.sigc.backend.infrastructure.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementación de ITokenProvider.
 * Responsable únicamente de generar tokens JWT.
 * 
 * Ventajas:
 * - Implementa una única responsabilidad (SRP)
 * - Fácil de testear (interfaz clara)
 * - Fácil de reemplazar (ej. cambiar a otro proveedor de tokens)
 */
@Component
public class JwtTokenProvider implements ITokenProvider {

    private static final String SECRET_KEY = "supersecreta123456supersecreta123456";
    private static final long EXPIRATION_TIME = 86400000; // 1 día

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    /**
     * Genera un token JWT con los claims del usuario.
     * 
     * @param userId ID del usuario
     * @param email Email del usuario
     * @param role Rol del usuario
     * @return Token JWT generado
     */
    @Override
    public String generateToken(Long userId, String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("rol", role);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}
