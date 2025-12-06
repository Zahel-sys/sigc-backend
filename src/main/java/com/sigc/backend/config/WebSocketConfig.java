package com.sigc.backend.config;

import com.sigc.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.socket.config.annotation.*;

import java.util.List;

/**
 * Configuración de WebSocket con STOMP
 * 
 * Habilita comunicación bidireccional en tiempo real entre frontend y backend
 * Incluye autenticación JWT en el handshake
 */
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtUtil jwtUtil;

    /**
     * Registra el endpoint WebSocket
     * URL: ws://localhost:8080/ws
     * Con SockJS como fallback si WebSocket no está disponible
     */
    @Override
    public void registerStompEndpoints(@NonNull StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins(
                    "http://localhost:5173",
                    "http://localhost:5174",
                    "http://localhost:5175",
                    "http://localhost:3000" // React default
                )
                .withSockJS(); // Fallback a long-polling si WebSocket no funciona
        
        log.info("🔌 WebSocket endpoint registrado en /ws");
    }

    /**
     * Configura el message broker (intermediario de mensajes)
     * 
     * /topic - Para broadcast a múltiples suscriptores (pub/sub)
     * /queue - Para mensajes punto a punto (1 a 1)
     * /app - Prefijo para mensajes del cliente al servidor
     */
    @Override
    public void configureMessageBroker(@NonNull MessageBrokerRegistry registry) {
        // Habilita un broker simple en memoria
        // Para producción, considerar RabbitMQ o ActiveMQ
        registry.enableSimpleBroker("/topic", "/queue");
        
        // Prefijo para endpoints del servidor (@MessageMapping)
        registry.setApplicationDestinationPrefixes("/app");
        
        log.info("📡 Message broker configurado: /topic, /queue, /app");
    }

    /**
     * Interceptor para validar JWT en cada conexión WebSocket
     * Se ejecuta antes de establecer la conexión
     */
    @Override
    public void configureClientInboundChannel(@NonNull ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
                StompHeaderAccessor accessor = 
                    MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                
                if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
                    // Extraer token JWT del header Authorization
                    String authToken = accessor.getFirstNativeHeader("Authorization");
                    
                    if (authToken != null && authToken.startsWith("Bearer ")) {
                        String token = authToken.substring(7);
                        
                        try {
                            // Validar token
                            if (jwtUtil.validateToken(token)) {
                                Long userId = jwtUtil.getUserIdFromToken(token);
                                String email = jwtUtil.getEmailFromToken(token);
                                String role = jwtUtil.getRolFromToken(token);
                                
                                // Crear autenticación con rol
                                UsernamePasswordAuthenticationToken authentication = 
                                    new UsernamePasswordAuthenticationToken(
                                        email, 
                                        null, 
                                        List.of(new SimpleGrantedAuthority("ROLE_" + role))
                                    );
                                
                                // Guardar usuario en el contexto de WebSocket
                                accessor.setUser(authentication);
                                
                                // Log de éxito
                                log.info("✅ WebSocket conectado: {} (ID: {}, Rol: {})", email, userId, role);
                            } else {
                                log.warn("⚠️ Token JWT inválido en WebSocket");
                                throw new IllegalArgumentException("Token inválido");
                            }
                        } catch (Exception e) {
                            log.error("❌ Error validando token WebSocket: {}", e.getMessage());
                            throw new IllegalArgumentException("Autenticación fallida: " + e.getMessage());
                        }
                    } else {
                        log.warn("⚠️ Sin token JWT en conexión WebSocket");
                        throw new IllegalArgumentException("Token JWT requerido");
                    }
                }
                
                return message;
            }
        });
        
        log.info("🔒 Interceptor de autenticación JWT configurado para WebSocket");
    }
}
