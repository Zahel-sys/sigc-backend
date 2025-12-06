package com.sigc.backend.controller;

import com.sigc.backend.application.service.UserApplicationService;
import com.sigc.backend.domain.model.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
@Tag(name = "Autenticación", description = "Endpoints para login, registro y gestión de autenticación")
@SecurityRequirement(name = "JWT")
public class MeController {

    private final UserApplicationService userApplicationService;

    /**
     * GET /auth/me
     * Obtiene los datos del usuario autenticado desde el contexto de seguridad
     * Este endpoint usa la autenticación automática de Spring Security
     */
    @GetMapping("/me")
    @Operation(summary = "Obtener perfil de usuario autenticado", description = "Retorna los datos del usuario actualmente autenticado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Perfil de usuario obtenido exitosamente"),
        @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<?> obtenerUsuarioAutenticado() {
        try {
            log.info("Obteniendo datos del usuario autenticado");

            // Obtener el usuario autenticado del contexto de seguridad
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
                log.warn("Usuario no autenticado");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Usuario no autenticado"));
            }

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();

            // Buscar usuario por email
            Usuario usuario = userApplicationService.getUserByEmail(email);
            
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
    public ResponseEntity<?> obtenerPerfil() {
        return obtenerUsuarioAutenticado();
    }
}
