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

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración de seguridad de Spring Security
 * - Configura BCrypt para encriptación de contraseñas
 * - Desactiva CSRF (para API REST)
 * - Configura CORS para permitir peticiones del frontend
 * - Permite acceso sin autenticación a endpoints públicos y archivos estáticos (imágenes)
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // Constructor para inyección de dependencias
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    // Encriptación con BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // CORS: Permitir peticiones del frontend en localhost (para desarrollo)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:5173",
            "http://localhost:5174",
            "http://localhost:5175",
            "http://localhost:8080",  // ✅ Agregar localhost:8080
            "http://127.0.0.1:8080",
            "http://localhost",
            "http://127.0.0.1"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // Cadena de filtros de seguridad
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable())) // ⭐️ PERMITIR FRAMES PARA H2 ⭐️
            .httpBasic(httpBasic -> httpBasic.disable())
            .formLogin(form -> form.disable())
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos - sin autenticación
                .requestMatchers("/auth/login", "/auth/register").permitAll()
                .requestMatchers("/test/**").permitAll() // ⭐️ TEMPORAL PARA DEBUGGING ⭐️
                .requestMatchers("/h2-console/**").permitAll() // ⭐️ H2 CONSOLE ACCESO TEMPORAL ⭐️
                .requestMatchers("/api/especialidades/**", "/especialidades/**").permitAll()
                // Permitir el endpoint de SockJS / WebSocket (handshake /info, etc.)
                .requestMatchers("/ws/**").permitAll()
                .requestMatchers("/api/doctores/**", "/doctores/**").permitAll()
                .requestMatchers("/api/horarios/**", "/horarios/**").permitAll()
                .requestMatchers("/api/citas/**", "/citas/**").permitAll()
                .requestMatchers("/api/servicios/**", "/servicios/**").permitAll()
                .requestMatchers("/api/usuarios/**", "/usuarios/**").permitAll()
                .requestMatchers("/uploads/**").permitAll()
                .requestMatchers("/images/**").permitAll() // ⭐️ PERMITIR ARCHIVOS ESTÁTICOS ⭐️
                .requestMatchers("/favicon.svg", "/favicon.ico").permitAll() // ⭐️ FAVICON ⭐️
                // SWAGGER/OpenAPI Documentation - sin autenticación
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                // Otras peticiones exigen autenticación
                .anyRequest().authenticated()
            )
            // Agregar filtro JWT antes del filtro de autenticación de usuario/contraseña
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
