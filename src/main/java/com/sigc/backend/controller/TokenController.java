package com.sigc.backend.controller;

import com.sigc.backend.model.Usuario;
import com.sigc.backend.repository.UsuarioRepository;
import com.sigc.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para validación y renovación de tokens JWT
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175"})
@RequiredArgsConstructor
public class TokenController {

    private final JwtUtil jwtUtil;
    private final UsuarioRepository usuarioRepository;

    /**
     * POST /auth/validate-token
     * Valida si un token es válido y retorna información del usuario
     */
    @PostMapping("/validate-token")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        Map<String, Object> response = new HashMap<>();

        try {
            if (token == null || token.trim().isEmpty()) {
                response.put("valid", false);
                response.put("error", "Token no proporcionado");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Validar token
            boolean isValid = jwtUtil.validateToken(token);
            
            if (!isValid) {
                response.put("valid", false);
                response.put("error", "Token inválido o expirado");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Intentar extraer el subject (puede ser ID o email en tokens antiguos)
            String subject = jwtUtil.getUserIdFromToken(token).toString();
            
            // Verificar si el subject es numérico (token nuevo) o email (token antiguo)
            boolean isOldToken = false;
            Long userId = null;
            
            try {
                userId = Long.parseLong(subject);
            } catch (NumberFormatException e) {
                // El subject no es numérico, es un token antiguo con email
                isOldToken = true;
                log.warn("Token antiguo detectado con email en subject: {}", subject);
            }

            response.put("valid", true);
            response.put("isOldToken", isOldToken);
            
            if (isOldToken) {
                response.put("message", "Token antiguo detectado. Se recomienda renovar.");
                response.put("emailFromToken", subject);
            } else {
                response.put("userId", userId);
                response.put("message", "Token válido");
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error al validar token: {}", e.getMessage(), e);
            response.put("valid", false);
            response.put("error", "Error al validar el token");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * POST /auth/refresh-token
     * Renueva un token antiguo (con email en sub) a un token nuevo (con ID en sub)
     * Requiere el token antiguo en el header Authorization
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String, Object>> refreshToken(@RequestHeader("Authorization") String authHeader) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Extraer token del header (quitar "Bearer ")
            String oldToken = authHeader.substring(7);

            // Validar que el token sea válido
            if (!jwtUtil.validateToken(oldToken)) {
                response.put("error", "Token inválido o expirado");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Intentar extraer información del token antiguo
            String subject;
            try {
                // Intentar obtener como ID
                subject = jwtUtil.getUserIdFromToken(oldToken).toString();
            } catch (Exception e) {
                // Si falla, el subject es probablemente un email
                subject = jwtUtil.getEmailFromToken(oldToken);
            }

            log.info("Intentando renovar token para subject: {}", subject);

            Usuario usuario;
            
            // Determinar si el subject es ID o email
            try {
                Long userId = Long.parseLong(subject);
                usuario = usuarioRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            } catch (NumberFormatException e) {
                // El subject es un email (token antiguo)
                usuario = usuarioRepository.findByEmail(subject);
                if (usuario == null) {
                    response.put("error", "Usuario no encontrado con email: " + subject);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                }
            }

            // Generar nuevo token con ID
            String newToken = jwtUtil.generateToken(usuario.getIdUsuario(), usuario.getEmail(), usuario.getRol());

            // Respuesta completa
            response.put("message", "Token renovado exitosamente");
            response.put("token", newToken);
            response.put("idUsuario", usuario.getIdUsuario());
            response.put("nombre", usuario.getNombre());
            response.put("email", usuario.getEmail());
            response.put("dni", usuario.getDni());
            response.put("telefono", usuario.getTelefono());
            response.put("rol", usuario.getRol());

            log.info("Token renovado exitosamente para usuario ID: {}", usuario.getIdUsuario());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error al renovar token: {}", e.getMessage(), e);
            response.put("error", "Error al renovar el token: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /auth/token-info
     * Devuelve información sobre el token actual sin renovarlo
     */
    @GetMapping("/token-info")
    public ResponseEntity<Map<String, Object>> getTokenInfo(@RequestHeader("Authorization") String authHeader) {
        Map<String, Object> response = new HashMap<>();

        try {
            String token = authHeader.substring(7);

            if (!jwtUtil.validateToken(token)) {
                response.put("valid", false);
                response.put("error", "Token inválido");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            response.put("valid", true);

            try {
                Long userId = jwtUtil.getUserIdFromToken(token);
                String email = jwtUtil.getEmailFromToken(token);
                String rol = jwtUtil.getRolFromToken(token);

                response.put("tokenType", "new");
                response.put("userId", userId);
                response.put("email", email);
                response.put("rol", rol);

            } catch (Exception e) {
                // Token antiguo con solo email
                String email = jwtUtil.getEmailFromToken(token);
                response.put("tokenType", "old");
                response.put("email", email);
                response.put("message", "Token antiguo detectado. Considera renovarlo.");
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error al obtener info del token: {}", e.getMessage(), e);
            response.put("error", "Error al procesar el token");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
