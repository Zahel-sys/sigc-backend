package com.sigc.backend.controller;

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
}
