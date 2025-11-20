package com.sigc.backend.controller;

import com.sigc.backend.dto.CambiarPasswordRequest;
import com.sigc.backend.dto.CambiarPasswordResponse;
import com.sigc.backend.dto.RegistroRequest;
import com.sigc.backend.dto.RegistroResponse;
import com.sigc.backend.model.Usuario;
import com.sigc.backend.repository.UsuarioRepository;
import com.sigc.backend.security.JwtUtil;
import com.sigc.backend.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
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
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final JwtUtil jwtUtil;
    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;

    /**
     * POST /auth/register
     * Registra un nuevo usuario en el sistema
     * 
     * @param request Datos del usuario a registrar (validados automáticamente)
     * @return 201 Created con datos del usuario registrado
     */
    @PostMapping("/register")
    public ResponseEntity<RegistroResponse> register(@Valid @RequestBody RegistroRequest request) {
        log.info("Recibida petición de registro para: {}", request.getEmail());
        
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
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        log.info("Intento de login para: {}", email);

        Usuario usuario = usuarioRepository.findByEmail(email);

        Map<String, Object> response = new HashMap<>();
        
        // Validar usuario y contraseña encriptada
        if (usuario != null && passwordEncoder.matches(password, usuario.getPassword())) {
            // Generar token con ID como subject
            String token = jwtUtil.generateToken(usuario.getIdUsuario(), usuario.getEmail(), usuario.getRol());
            
            // Respuesta completa con todos los datos del usuario
            response.put("message", "Login exitoso");
            response.put("token", token);
            response.put("rol", usuario.getRol());
            response.put("idUsuario", usuario.getIdUsuario());
            response.put("nombre", usuario.getNombre());
            response.put("email", usuario.getEmail());
            response.put("dni", usuario.getDni());
            response.put("telefono", usuario.getTelefono());
            
            log.info("Login exitoso para usuario ID: {} - {}", usuario.getIdUsuario(), email);
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "Credenciales inválidas");
            log.warn("Login fallido para: {}", email);
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
            log.info("📝 Recibiendo petición para cambiar contraseña desde /auth");

            // ✅ VALIDACION 1: Verificar autenticación (token JWT)
            if (authHeader == null || authHeader.isEmpty()) {
                log.warn("⚠️ Falta header Authorization");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(crearError("Token JWT requerido en header Authorization"));
            }

            String token = authHeader.startsWith("Bearer ") 
                    ? authHeader.substring(7) 
                    : authHeader;

            if (!jwtUtil.validateToken(token)) {
                log.warn("❌ Token JWT inválido o expirado");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(crearError("Token JWT inválido o expirado"));
            }

            // Extraer el idUsuario del token
            Long idUsuario = jwtUtil.getIdUsuarioFromToken(token);
            if (idUsuario == null) {
                log.warn("❌ No se pudo extraer el ID del usuario del token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(crearError("Token inválido"));
            }
            log.info("✓ Usuario autenticado: ID {}", idUsuario);

            // ✅ VALIDACION 2: Obtener usuario
            Usuario usuario = usuarioRepository.findById(idUsuario)
                    .orElseThrow(() -> {
                        log.error("❌ Usuario no encontrado con ID: {}", idUsuario);
                        throw new RuntimeException("USUARIO_NO_ENCONTRADO");
                    });
            log.info("✓ Usuario encontrado: {}", usuario.getEmail());

            // ✅ VALIDACION 3: Validar que passwordActual, passwordNueva y passwordConfirmar no sean nulos
            if (request.getPasswordActual() == null || request.getPasswordActual().isEmpty()) {
                log.error("❌ Error: contraseña actual no proporcionada");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(crearError("Debe proporcionar la contraseña actual"));
            }

            if (request.getPasswordNueva() == null || request.getPasswordNueva().isEmpty()) {
                log.error("❌ Error: contraseña nueva no proporcionada");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(crearError("Debe proporcionar la contraseña nueva"));
            }

            if (request.getPasswordConfirmar() == null || request.getPasswordConfirmar().isEmpty()) {
                log.error("❌ Error: confirmación de contraseña no proporcionada");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(crearError("Debe confirmar la contraseña nueva"));
            }

            // ✅ VALIDACION 4: Verificar que la contraseña actual sea correcta
            if (!passwordEncoder.matches(request.getPasswordActual(), usuario.getPassword())) {
                log.warn("⚠️ Contraseña actual incorrecta para usuario {}", idUsuario);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(crearError("La contraseña actual es incorrecta"));
            }
            log.info("✓ Contraseña actual validada correctamente");

            // ✅ VALIDACION 5: Verificar que passwordNueva coincida con passwordConfirmar
            if (!request.getPasswordNueva().equals(request.getPasswordConfirmar())) {
                log.warn("⚠️ Las contraseñas nuevas no coinciden para usuario {}", idUsuario);
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                        .body(crearError("Las contraseñas nuevas no coinciden"));
            }
            log.info("✓ Las contraseñas nuevas coinciden");

            // ✅ VALIDACION 6: Verificar que passwordNueva sea diferente a passwordActual
            if (request.getPasswordNueva().equals(request.getPasswordActual())) {
                log.warn("⚠️ La contraseña nueva es igual a la actual para usuario {}", idUsuario);
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                        .body(crearError("La contraseña nueva debe ser diferente a la actual"));
            }
            log.info("✓ La contraseña nueva es diferente a la actual");

            // ✅ VALIDACION 7: Verificar que passwordNueva tenga al menos 6 caracteres
            if (request.getPasswordNueva().length() < 6) {
                log.warn("⚠️ Contraseña nueva muy corta para usuario {}", idUsuario);
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                        .body(crearError("La contraseña debe tener al menos 6 caracteres"));
            }
            log.info("✓ Longitud de contraseña válida");

            // ✅ VALIDACION 8: Encriptar la nueva contraseña y guardar
            String passwordEncriptada = passwordEncoder.encode(request.getPasswordNueva());
            usuario.setPassword(passwordEncriptada);
            usuarioRepository.save(usuario);
            log.info("✅ Contraseña actualizada exitosamente para usuario {}", idUsuario);

            // Retornar respuesta exitosa SIN la contraseña
            return ResponseEntity.ok(CambiarPasswordResponse.exitoso(usuario.getIdUsuario(), usuario.getEmail()));

        } catch (RuntimeException e) {
            String mensaje = e.getMessage();

            if (mensaje != null && mensaje.equals("USUARIO_NO_ENCONTRADO")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(crearError("Usuario no encontrado"));
            } else {
                log.error("❌ Error de validación al cambiar contraseña: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(crearError("Error: " + e.getMessage()));
            }
        } catch (Exception e) {
            log.error("❌ Error inesperado al cambiar contraseña: {}", e.getMessage(), e);
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
