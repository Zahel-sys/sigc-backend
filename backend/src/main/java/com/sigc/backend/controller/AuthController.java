package com.sigc.backend.controller;

import com.sigc.backend.model.Usuario;
import com.sigc.backend.repository.UsuarioRepository;
import com.sigc.backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public Usuario register(@RequestBody Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        Usuario usuario = usuarioRepository.findByEmail(email);

        Map<String, String> response = new HashMap<>();
        if (usuario != null && usuario.getPassword().equals(password)) {
            String token = jwtUtil.generateToken(usuario.getEmail());
            response.put("token", token);
            response.put("rol", usuario.getRol().toString());
            response.put("message", "Login exitoso");
        } else {
            response.put("error", "Credenciales inválidas");
        }
        return response;
    }
}
