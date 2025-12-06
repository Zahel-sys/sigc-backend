package com.sigc.backend.controller;

import com.sigc.backend.dto.CambiarPasswordRequest;
import com.sigc.backend.dto.CambiarPasswordResponse;
import com.sigc.backend.dto.RegistroRequest;
import com.sigc.backend.dto.RegistroResponse;
import com.sigc.backend.domain.service.usecase.auth.LoginRequest;
import com.sigc.backend.domain.service.usecase.auth.LoginResponse;
import com.sigc.backend.domain.service.usecase.auth.ChangePasswordUseCase;
import com.sigc.backend.application.service.AuthApplicationService;
import com.sigc.backend.security.JwtUtil;
import com.sigc.backend.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador de autenticación
 * Maneja registro y login de usuarios
 * Expone endpoints bajo /auth (sin /api según requerimiento)
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175"})
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Autenticación", description = "Endpoints para login, registro y gestión de autenticación")
public class AuthController {

    private final AuthApplicationService authApplicationService;
    private final JwtUtil jwtUtil;
    private final UsuarioService usuarioService;

    /**
     * POST /auth/register
     * Registra un nuevo usuario en el sistema
     *
     * @param request Datos del usuario a registrar (validados automáticamente)
     * @return 201 Created con datos del usuario registrado
     */
    @PostMapping("/register")
    @Operation(summary = "Registrar nuevo usuario", description = "Crea una nueva cuenta de usuario en el sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "409", description = "El email ya está registrado")
    })
    public ResponseEntity<RegistroResponse> register(@Valid @RequestBody RegistroRequest request) {
        log.info("Recibida petición de registro para: {}", request.getEmail());
        
        // Usar UsuarioService que maneja todos los campos de RegistroRequest
        RegistroResponse response = usuarioService.registrarUsuario(request);
        
        log.info("Usuario registrado exitosamente: {}", response.getIdUsuario());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * POST /auth/login
     * Autentica un usuario y genera un token JWT
     * 
     * @param credentials Mapa con email y password
     * @return Token JWT y datos completos del usuario si las credenciales son válidas
     */
    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica usuario y retorna token JWT")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login exitoso"),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        log.info("Intento de login para: {}", email);
        // Construir request de dominio y delegar a Application Service
        LoginRequest loginRequest = new LoginRequest(email, password);
        try {
            LoginResponse loginResponse = authApplicationService.login(loginRequest);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login exitoso");
            response.put("token", loginResponse.getToken());
            response.put("rol", loginResponse.getRole());
            response.put("idUsuario", loginResponse.getUserId());
            response.put("email", loginResponse.getEmail());
            log.info("Login exitoso para usuario ID: {} - {}", loginResponse.getUserId(), email);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Credenciales inválidas");
            log.warn("Login fallido para: {} - {}", email, e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

    }

    /**
     * POST /auth/cambiar-contrasena (Alias)
     * Reenvia la peticion al metodo de cambio de contrasena en UsuarioController
     * Esta ruta es un alias para compatibilidad con frontends antiguos
     * 
     * @param authHeader Token JWT en header Authorization
     * @param request Datos para cambiar contrasena
     * @return Respuesta con resultado del cambio de contrasena
     */
    @PostMapping("/cambiar-contrasena")
    public ResponseEntity<?> cambiarContrasena(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody CambiarPasswordRequest request) {
        return cambiarPasswordImpl(authHeader, request);
    }

    /**
     * PUT /auth/cambiar-password (Alias)
     * Reenvía la petición al método de cambio de contraseña en UsuarioController
     * 
     * @param authHeader Token JWT en header Authorization
     * @param request Datos para cambiar contraseña
     * @return Respuesta con resultado del cambio de contraseña
     */
    @PutMapping("/cambiar-password")
    public ResponseEntity<?> cambiarPasswordPut(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody CambiarPasswordRequest request) {
        return cambiarPasswordImpl(authHeader, request);
    }

    /**
     * Implementación compartida del cambio de contraseña
     * Llamada por los endpoints /auth/cambiar-contrasena y /auth/cambiar-password
     */
    private ResponseEntity<?> cambiarPasswordImpl(
            String authHeader,
            CambiarPasswordRequest request) {
        try {
            // Validar header
            if (authHeader == null || authHeader.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(crearError("Token JWT requerido en header Authorization"));
            }

            String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;

            // Extraer userId del token usando JwtUtil
            Long userId;
            try {
                userId = jwtUtil.getUserIdFromToken(token);
            } catch (Exception ex) {
                log.warn("Error al extraer userId del token: {}", ex.getMessage());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(crearError("Token inválido o expirado"));
            }

            ChangePasswordUseCase.ChangePasswordRequest changeReq = new ChangePasswordUseCase.ChangePasswordRequest(
                    userId,
                    request.getPasswordActual(),
                    request.getPasswordNueva(),
                    request.getPasswordConfirmar()
            );

            ChangePasswordUseCase.ChangePasswordResponse resp = authApplicationService.changePassword(changeReq);
            if (resp.isSuccess()) {
                return ResponseEntity.ok(CambiarPasswordResponse.exitoso(userId, ""));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(crearError(resp.getMessage()));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearError("Error interno al cambiar la contraseña"));
        }

    }

    /**
     * Método auxiliar para crear respuestas de error estándar
     */
    private Map<String, Object> crearError(String mensaje) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", mensaje);
        error.put("timestamp", LocalDateTime.now());
        return error;
    }
}
