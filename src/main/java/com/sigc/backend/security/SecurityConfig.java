package com.sigc.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Configuración de seguridad de Spring Security
 * - Configura BCrypt para encriptación de contraseñas
 * - Desactiva CSRF (para API REST)
 * - Configura CORS para permitir peticiones del frontend
 * - Permite acceso sin autenticación a endpoints públicos
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Bean para encriptar contraseñas con BCrypt
     * Fuerza: 10 rondas (balance entre seguridad y rendimiento)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configuración de CORS
     * Permite peticiones desde el frontend en localhost:5173, 5174 y 5175
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:5173",
            "http://localhost:5174",
            "http://localhost:5175"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L); // Cache preflight por 1 hora
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Configuración de la cadena de filtros de seguridad
     * - CSRF deshabilitado (API REST con JWT)
     * - HTTP Basic Auth DESHABILITADO (usamos JWT Bearer Tokens)
     * - Form Login DESHABILITADO (usamos JWT)
     * - Sesiones STATELESS (sin estado - JWT puro)
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults()) // Habilita CORS con el bean definido arriba
            .csrf(csrf -> csrf.disable()) // Desactiva CSRF (necesario para APIs REST)
            .httpBasic(httpBasic -> httpBasic.disable()) // ⭐ CRÍTICO: Deshabilitar HTTP Basic Auth
            .formLogin(form -> form.disable()) // ⭐ Deshabilitar form login
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos - sin autenticación
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/api/especialidades/**", "/especialidades/**").permitAll()
                .requestMatchers("/api/doctores/**", "/doctores/**").permitAll()
                .requestMatchers("/api/horarios/**", "/horarios/**").permitAll()
                .requestMatchers("/api/citas/**", "/citas/**").permitAll()
                .requestMatchers("/api/servicios/**", "/servicios/**").permitAll()
                .requestMatchers("/api/usuarios/**", "/usuarios/**").permitAll()
                .requestMatchers("/uploads/**").permitAll()
                // Cualquier otra petición requiere autenticación
                .anyRequest().authenticated()
            );

        return http.build();
    }
}
