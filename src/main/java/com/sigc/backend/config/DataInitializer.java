package com.sigc.backend.config;

import com.sigc.backend.model.Usuario;
import com.sigc.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Inicializador de datos
 * Se ejecuta automáticamente al iniciar la aplicación
 * Crea el usuario administrador por defecto si no existe
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    // Credenciales del administrador por defecto
    private static final String ADMIN_EMAIL = "admin@sigc.com";
    private static final String ADMIN_PASSWORD = "Admin123456";
    private static final String ADMIN_NOMBRE = "Administrador del Sistema";
    private static final String ADMIN_DNI = "00000000";
    private static final String ADMIN_TELEFONO = "999999999";

    @Override
    public void run(String... args) throws Exception {
        crearAdministradorPorDefecto();
    }

    /**
     * Crea el usuario administrador por defecto si no existe
     */
    private void crearAdministradorPorDefecto() {
        try {
            // Verificar si ya existe un administrador
            Usuario adminExistente = usuarioRepository.findByEmail(ADMIN_EMAIL);
            
            if (adminExistente != null) {
                log.info("✅ Usuario administrador ya existe: {}", ADMIN_EMAIL);
                return;
            }

            // Crear nuevo administrador
            Usuario admin = new Usuario();
            admin.setNombre(ADMIN_NOMBRE);
            admin.setEmail(ADMIN_EMAIL);
            admin.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
            admin.setDni(ADMIN_DNI);
            admin.setTelefono(ADMIN_TELEFONO);
            admin.setRol("ADMIN");
            admin.setActivo(true);

            usuarioRepository.save(admin);

            log.info("══════════════════════════════════════════════════");
            log.info("✅ USUARIO ADMINISTRADOR CREADO AUTOMÁTICAMENTE");
            log.info("══════════════════════════════════════════════════");
            log.info("📧 Email:    {}", ADMIN_EMAIL);
            log.info("🔑 Password: {}", ADMIN_PASSWORD);
            log.info("⚠️  IMPORTANTE: Cambia la contraseña después del primer inicio de sesión");
            log.info("══════════════════════════════════════════════════");

        } catch (Exception e) {
            log.error("❌ Error al crear usuario administrador: {}", e.getMessage(), e);
        }
    }
}
