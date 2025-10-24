package com.sigc.backend.controller;

import com.sigc.backend.dto.RegistroRequest;
import com.sigc.backend.dto.RegistroResponse;
import com.sigc.backend.model.Usuario;
import com.sigc.backend.repository.UsuarioRepository;
import com.sigc.backend.security.JwtUtil;
<<<<<<< HEAD
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
=======
import com.sigc.backend.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
>>>>>>> origin/feature/resoluciones-y-JWT
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador de autenticación
 * Maneja registro y login de usuarios
 * Expone endpoints bajo /auth (sin /api según requerimiento)
 */
@RestController
@RequestMapping("/auth")
<<<<<<< HEAD
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired(required = false)
    private JwtUtil jwtUtil;

    // ✅ Registro con validaciones y control de errores
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Usuario usuario) {
        try {
            Usuario existente = usuarioRepository.findByEmail(usuario.getEmail());
            if (existente != null) {
                return ResponseEntity.badRequest().body(Map.of("error", "El correo ya está registrado."));
            }

            if (usuario.getRol() == null || usuario.getRol().isBlank()) {
                usuario.setRol("PACIENTE");
            }

            usuario.setActivo(true);
            Usuario guardado = usuarioRepository.save(usuario);

            return ResponseEntity.ok(Map.of(
                    "message", "Usuario registrado exitosamente",
                    "usuario", guardado
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Error al registrar el usuario."));
        }
    }

    // ✅ Login con respuestas claras
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
=======
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
>>>>>>> origin/feature/resoluciones-y-JWT
        String email = credentials.get("email");
        String password = credentials.get("password");

        log.info("Intento de login para: {}", email);

        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Usuario no encontrado"));
        }

<<<<<<< HEAD
        if (!usuario.getPassword().equals(password)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Contraseña incorrecta"));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("idUsuario", usuario.getIdUsuario());
        response.put("nombre", usuario.getNombre());
        response.put("email", usuario.getEmail());
        response.put("rol", usuario.getRol());
        response.put("activo", usuario.isActivo());
        response.put("message", "Login exitoso");

        if (jwtUtil != null) {
            String token = jwtUtil.generateToken(usuario.getEmail());
            response.put("token", token);
        }

        return ResponseEntity.ok(response);
=======
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
>>>>>>> origin/feature/resoluciones-y-JWT
    }
}
