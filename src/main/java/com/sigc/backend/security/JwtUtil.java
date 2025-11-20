package com.sigc.backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private static final String SECRET_KEY = "supersecreta123456supersecreta123456"; // clave privada
    private static final long EXPIRATION_TIME = 86400000; // 1 día

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    /**
     * Genera un token JWT con el ID del usuario como subject
     * @param idUsuario ID numérico del usuario
     * @param email Email del usuario
     * @param rol Rol del usuario
     * @return Token JWT
     */
    public String generateToken(Long idUsuario, String email, String rol) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("rol", rol);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(String.valueOf(idUsuario)) // ID como subject
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Obtiene el ID del usuario desde el token (campo "sub")
     * @param token Token JWT
     * @return ID del usuario como Long
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong(claims.getSubject());
    }

    /**
     * Obtiene el email del usuario desde el token
     * @param token Token JWT
     * @return Email del usuario
     */
    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("email", String.class);
    }

    /**
     * Obtiene el rol del usuario desde el token
     * @param token Token JWT
     * @return Rol del usuario
     */
    public String getRolFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("rol", String.class);
    }

    /**
     * Método legacy para compatibilidad
     * @deprecated Usar getUserIdFromToken en su lugar
     */
    @Deprecated
    public String getUsernameFromToken(String token) {
        return getEmailFromToken(token);
    }

    /**
     * Alias para obtener el ID del usuario desde el token
     * @param token Token JWT
     * @return ID del usuario como Long
     */
    public Long getIdUsuarioFromToken(String token) {
        return getUserIdFromToken(token);
    }
}
