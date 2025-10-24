package com.sigc.backend.controller;

import com.sigc.backend.model.Usuario;
import com.sigc.backend.repository.UsuarioRepository;
import com.sigc.backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
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
        String email = credentials.get("email");
        String password = credentials.get("password");

        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Usuario no encontrado"));
        }

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
    }
}
