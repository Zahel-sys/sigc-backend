package com.sigc.backend.controller;

import com.sigc.backend.model.Usuario;
import com.sigc.backend.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175"})
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

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
}
