package com.sigc.backend.controller;

import com.sigc.backend.application.service.UserApplicationService;
import com.sigc.backend.domain.model.Usuario;
import com.sigc.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para obtener datos del usuario autenticado
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175"})
@RequiredArgsConstructor
public class MeController {

    private final JwtUtil jwtUtil;
    private final UserApplicationService userApplicationService;

    /**
     * GET /auth/me
     * Obtiene los datos del usuario autenticado desde el token JWT
     * Este endpoint extrae el ID del usuario del token automáticamente
     */
    @GetMapping("/me")
    public ResponseEntity<?> obtenerUsuarioAutenticado(@RequestHeader("Authorization") String authHeader) {
        try {
            log.info("Obteniendo datos del usuario autenticado");
            
            // Extraer token del header (quitar "Bearer ")
            String token = authHeader.substring(7);

            // Validar token
            if (!jwtUtil.validateToken(token)) {
                log.warn("Token inválido o expirado");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Token inválido o expirado"));
            }

            Long idUsuario;
            String email;
            
            try {
                // Intentar obtener ID del token nuevo
                idUsuario = jwtUtil.getUserIdFromToken(token);
                email = jwtUtil.getEmailFromToken(token);
                log.info("Token nuevo detectado - ID: {}, Email: {}", idUsuario, email);
            } catch (Exception e) {
                // Token antiguo con email en sub
                log.warn("Token antiguo detectado, intentando obtener por email");
                email = jwtUtil.getEmailFromToken(token);
                
                // Buscar usuario por email
                try {
                    Usuario usuarioTemp = userApplicationService.getUserByEmail(email);
                    idUsuario = usuarioTemp.getId();
                } catch (Exception ex) {
                    log.error("Usuario no encontrado con email: {}", email);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(Map.of(
                                "error", "Usuario no encontrado",
                                "message", "Token antiguo detectado. Por favor, cierra sesión y vuelve a hacer login."
                            ));
                }
            }

            // Buscar usuario por ID
            Usuario usuario = userApplicationService.getUserById(idUsuario);

            // Preparar respuesta con todos los datos
            Map<String, Object> response = new HashMap<>();
            response.put("idUsuario", usuario.getId());
            response.put("nombre", usuario.getNombre());
            response.put("email", usuario.getEmail());
            response.put("dni", usuario.getDni());
            response.put("telefono", usuario.getTelefono());
            response.put("rol", usuario.getRole());
            response.put("activo", usuario.getActivo());
            response.put("fechaRegistro", usuario.getCreatedAt());

            log.info("Datos del usuario {} obtenidos exitosamente", usuario.getId());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error al obtener usuario autenticado: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener datos del usuario: " + e.getMessage()));
        }
    }

    /**
     * GET /auth/profile
     * Alias de /auth/me para compatibilidad
     */
    @GetMapping("/profile")
    public ResponseEntity<?> obtenerPerfil(@RequestHeader("Authorization") String authHeader) {
        return obtenerUsuarioAutenticado(authHeader);
    }
}
