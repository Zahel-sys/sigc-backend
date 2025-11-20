package com.sigc.backend.controller;

import com.sigc.backend.model.Usuario;
import com.sigc.backend.repository.UsuarioRepository;
import com.sigc.backend.dto.CambiarPasswordRequest;
import com.sigc.backend.dto.CambiarPasswordResponse;
import com.sigc.backend.security.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175"})
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public List<Usuario> listarUsuarios() {
        try {
            log.info("Listando todos los usuarios");
            List<Usuario> usuarios = usuarioRepository.findAll();
            log.info("Se encontraron {} usuarios", usuarios.size());
            return usuarios;
        } catch (Exception e) {
            log.error("Error al listar usuarios: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @PostMapping
    public ResponseEntity<?> crearUsuario(@RequestBody Usuario usuario) {
        try {
            log.info("Creando nuevo usuario: {}", usuario.getEmail());
            Usuario saved = usuarioRepository.save(usuario);
            log.info("Usuario creado exitosamente con ID: {}", saved.getIdUsuario());
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            log.error("Error al crear usuario: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear el usuario");
        }
    }

    /**
     * GET /usuarios/{idOrEmail}
     * Obtiene un usuario por ID o Email
     * - Si es un número: busca por ID
     * - Si es texto: busca por email (retrocompatibilidad con tokens antiguos)
     */
    @GetMapping("/{idOrEmail}")
    public ResponseEntity<?> obtenerUsuario(@PathVariable String idOrEmail) {
        try {
            log.info("Obteniendo usuario: {}", idOrEmail);
            
            Usuario usuario = null;
            
            // Intentar primero como ID (número)
            try {
                Long id = Long.parseLong(idOrEmail);
                log.info("Buscando usuario por ID: {}", id);
                usuario = usuarioRepository.findById(id).orElse(null);
            } catch (NumberFormatException e) {
                // No es un número, buscar por email
                log.info("Buscando usuario por email: {}", idOrEmail);
                usuario = usuarioRepository.findByEmail(idOrEmail);
            }
            
            if (usuario != null) {
                log.info("Usuario encontrado: ID={}, Email={}", usuario.getIdUsuario(), usuario.getEmail());
                return ResponseEntity.ok(usuario);
            } else {
                log.warn("Usuario no encontrado: {}", idOrEmail);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Usuario no encontrado: " + idOrEmail);
            }
        } catch (Exception e) {
            log.error("Error al obtener usuario {}: {}", idOrEmail, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al buscar el usuario");
        }
    }

    /**
     * GET /usuarios/email/{email}
     * Obtiene un usuario por su email
     * Útil para retrocompatibilidad con tokens antiguos
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<?> obtenerUsuarioPorEmail(@PathVariable String email) {
        try {
            log.info("Obteniendo usuario por email: {}", email);
            Usuario usuario = usuarioRepository.findByEmail(email);
            if (usuario != null) {
                log.info("Usuario encontrado: {}", usuario.getIdUsuario());
                return ResponseEntity.ok(usuario);
            } else {
                log.warn("Usuario no encontrado con email: {}", email);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Usuario no encontrado con email: " + email);
            }
        } catch (Exception e) {
            log.error("Error al obtener usuario por email {}: {}", email, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al buscar el usuario");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuario) {
        try {
            log.info("Actualizando usuario ID: {}", id);
            Usuario existente = usuarioRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
            
            existente.setNombre(usuario.getNombre());
            existente.setEmail(usuario.getEmail());
            existente.setRol(usuario.getRol());
            existente.setActivo(usuario.isActivo());
            
            Usuario actualizado = usuarioRepository.save(existente);
            log.info("Usuario {} actualizado exitosamente", id);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            log.error("Error al actualizar usuario {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar el usuario");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
        try {
            log.info("Eliminando usuario ID: {}", id);
            usuarioRepository.deleteById(id);
            log.info("Usuario {} eliminado exitosamente", id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al eliminar usuario {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar el usuario");
        }
    }

    /**
     * Endpoint PUT para cambiar contraseña
     * 
     * Headers requeridos:
     *   Authorization: Bearer {token_jwt}
     * 
     * Body:
     * {
     *   "passwordActual": "Admin123456",
     *   "passwordNueva": "NuevaPassword123",
     *   "passwordConfirmar": "NuevaPassword123"
     * }
     */
    @PutMapping("/cambiar-password")
    public ResponseEntity<?> cambiarPassword(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody CambiarPasswordRequest request) {
        try {
            log.info("📝 Recibiendo petición para cambiar contraseña");

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