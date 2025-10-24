package com.sigc.backend.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    private static final String SECRET_KEY = "sigc_secret_key";
    private static final long EXPIRATION_TIME = 86400000; // 1 día

<<<<<<< HEAD
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
=======
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
>>>>>>> origin/feature/resoluciones-y-JWT
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }
<<<<<<< HEAD
=======

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
>>>>>>> origin/feature/resoluciones-y-JWT
}
