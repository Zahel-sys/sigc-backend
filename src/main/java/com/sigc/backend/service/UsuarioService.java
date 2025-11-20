package com.sigc.backend.service;

import com.sigc.backend.dto.RegistroRequest;
import com.sigc.backend.dto.RegistroResponse;
import com.sigc.backend.exception.EmailDuplicadoException;
import com.sigc.backend.domain.model.Usuario;
import com.sigc.backend.domain.port.IUsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio para gestión de usuarios
 * Maneja la lógica de negocio para registro, validaciones y encriptación
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {

    private final IUsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Registra un nuevo usuario en el sistema
     * 
     * @param request Datos del usuario a registrar
     * @return Respuesta con datos del usuario registrado
     * @throws EmailDuplicadoException si el email ya está registrado
     */
    @Transactional
    public RegistroResponse registrarUsuario(RegistroRequest request) {
        log.info("Iniciando registro de usuario con email: {}", request.getEmail());
        
        // Validar que el email no exista
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            log.warn("Intento de registro con email duplicado: {}", request.getEmail());
            throw new EmailDuplicadoException("El email " + request.getEmail() + " ya está registrado");
        }
        // Crear entidad de dominio Usuario
        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword())); // Encriptar contraseña
        usuario.setDni(request.getDni());
        usuario.setTelefono(request.getTelefono());
        usuario.setRole(request.getRol() != null ? request.getRol() : "PACIENTE"); // Default PACIENTE
        usuario.setActivo(true);

        // Guardar vía puerto de dominio
        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        log.info("Usuario registrado exitosamente con ID: {}", usuarioGuardado.getId());

        // Construir respuesta (sin password)
        return RegistroResponse.builder()
                .idUsuario(usuarioGuardado.getId())
                .nombre(usuarioGuardado.getNombre())
                .email(usuarioGuardado.getEmail())
                .mensaje("Usuario registrado exitosamente")
                .build();
    }
}
