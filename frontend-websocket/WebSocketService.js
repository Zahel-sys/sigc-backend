/**
 * WebSocketService - Cliente WebSocket con autenticación JWT
 * 
 * Gestiona la conexión WebSocket con el backend usando STOMP
 * Incluye reconexión automática, manejo de errores y segregación por canales
 */

import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

class WebSocketService {
    constructor() {
        this.client = null;
        this.connected = false;
        this.reconnectAttempts = 0;
        this.maxReconnectAttempts = 5;
        this.reconnectDelay = 3000; // 3 segundos
        this.subscriptions = new Map();
        this.messageHandlers = new Map();
        this.token = null;
        this.userId = null;
        this.userRole = null;
    }

    /**
     * Conecta al servidor WebSocket con autenticación JWT
     * @param {string} token - Token JWT del usuario
     * @param {string} userId - ID del usuario
     * @param {string} userRole - Rol del usuario (ADMIN, PACIENTE, DOCTOR)
     */
    connect(token, userId, userRole) {
        if (this.connected) {
            console.warn('⚠️ WebSocket ya está conectado');
            return Promise.resolve();
        }

        this.token = token;
        this.userId = userId;
        this.userRole = userRole;

        return new Promise((resolve, reject) => {
            try {
                // Crear cliente STOMP con SockJS
                this.client = new Client({
                    // WebSocket URL con SockJS fallback
                    webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
                    
                    // Headers de conexión (incluye JWT)
                    connectHeaders: {
                        Authorization: `Bearer ${token}`
                    },
                    
                    // Configuración de heartbeat (ping/pong)
                    heartbeatIncoming: 10000,
                    heartbeatOutgoing: 10000,
                    
                    // Configuración de reconexión
                    reconnectDelay: this.reconnectDelay,
                    
                    // Debug (desactivar en producción)
                    debug: (str) => {
                        if (import.meta.env.DEV) {
                            console.log('🔌 STOMP:', str);
                        }
                    },
                    
                    // Callback cuando se conecta
                    onConnect: (frame) => {
                        console.log('✅ WebSocket conectado:', frame);
                        this.connected = true;
                        this.reconnectAttempts = 0;
                        
                        // Auto-suscribirse a canales según rol
                        this.autoSubscribe();
                        
                        resolve();
                    },
                    
                    // Callback cuando hay error
                    onStompError: (frame) => {
                        console.error('❌ Error STOMP:', frame.headers['message']);
                        console.error('Detalles:', frame.body);
                        this.connected = false;
                        reject(new Error(frame.headers['message']));
                    },
                    
                    // Callback cuando se desconecta
                    onDisconnect: () => {
                        console.warn('⚠️ WebSocket desconectado');
                        this.connected = false;
                        this.handleReconnect();
                    },
                    
                    // Callback cuando falla conexión WebSocket
                    onWebSocketError: (event) => {
                        console.error('❌ Error WebSocket:', event);
                        this.connected = false;
                    }
                });

                // Activar el cliente
                this.client.activate();

            } catch (error) {
                console.error('❌ Error al crear cliente WebSocket:', error);
                reject(error);
            }
        });
    }

    /**
     * Auto-suscripción a canales según el rol del usuario
     */
    autoSubscribe() {
        if (!this.connected || !this.client) {
            console.warn('⚠️ No conectado, no se puede auto-suscribir');
            return;
        }

        try {
            // Canal personal del usuario
            this.subscribe(`/topic/user/${this.userId}`, (message) => {
                console.log('📬 Notificación personal:', message);
                this.notifyHandlers('user', message);
            });

            // Canal global (todos los usuarios)
            this.subscribe('/topic/global', (message) => {
                console.log('📢 Notificación global:', message);
                this.notifyHandlers('global', message);
            });

            // Canal de admins (solo para ADMIN)
            if (this.userRole === 'ADMIN') {
                this.subscribe('/topic/admin', (message) => {
                    console.log('👑 Notificación admin:', message);
                    this.notifyHandlers('admin', message);
                });
            }

            // Canal de horarios (doctores y pacientes)
            if (this.userRole === 'DOCTOR' || this.userRole === 'PACIENTE') {
                this.subscribe('/topic/horarios', (message) => {
                    console.log('🕒 Actualización de horarios:', message);
                    this.notifyHandlers('horarios', message);
                });
            }

            console.log('✅ Auto-suscripción completada');
        } catch (error) {
            console.error('❌ Error en auto-suscripción:', error);
        }
    }

    /**
     * Suscribirse a un canal específico
     * @param {string} destination - Canal (ej: /topic/user/1)
     * @param {function} callback - Función a ejecutar cuando llega un mensaje
     * @returns {object} Subscription object
     */
    subscribe(destination, callback) {
        if (!this.connected || !this.client) {
            console.warn('⚠️ No conectado, no se puede suscribir a', destination);
            return null;
        }

        try {
            const subscription = this.client.subscribe(destination, (message) => {
                try {
                    const notification = JSON.parse(message.body);
                    callback(notification);
                } catch (error) {
                    console.error('❌ Error parseando mensaje:', error);
                    callback(message.body);
                }
            });

            this.subscriptions.set(destination, subscription);
            console.log('✅ Suscrito a:', destination);
            return subscription;

        } catch (error) {
            console.error('❌ Error al suscribirse a', destination, error);
            return null;
        }
    }

    /**
     * Desuscribirse de un canal
     * @param {string} destination - Canal a desuscribirse
     */
    unsubscribe(destination) {
        const subscription = this.subscriptions.get(destination);
        if (subscription) {
            subscription.unsubscribe();
            this.subscriptions.delete(destination);
            console.log('✅ Desuscrito de:', destination);
        }
    }

    /**
     * Enviar mensaje al servidor
     * @param {string} destination - Endpoint del servidor (ej: /app/notify)
     * @param {object} body - Datos a enviar
     */
    send(destination, body) {
        if (!this.connected || !this.client) {
            console.warn('⚠️ No conectado, no se puede enviar mensaje');
            return false;
        }

        try {
            this.client.publish({
                destination,
                body: JSON.stringify(body),
                headers: {
                    Authorization: `Bearer ${this.token}`
                }
            });
            console.log('📤 Mensaje enviado a:', destination);
            return true;
        } catch (error) {
            console.error('❌ Error enviando mensaje:', error);
            return false;
        }
    }

    /**
     * Registrar handler para tipos de notificaciones
     * @param {string} type - Tipo de canal (user, admin, global, horarios)
     * @param {function} handler - Función callback
     */
    onMessage(type, handler) {
        if (!this.messageHandlers.has(type)) {
            this.messageHandlers.set(type, []);
        }
        this.messageHandlers.get(type).push(handler);
    }

    /**
     * Notificar a todos los handlers registrados
     * @param {string} type - Tipo de canal
     * @param {object} message - Mensaje recibido
     */
    notifyHandlers(type, message) {
        const handlers = this.messageHandlers.get(type) || [];
        handlers.forEach(handler => {
            try {
                handler(message);
            } catch (error) {
                console.error('❌ Error ejecutando handler:', error);
            }
        });
    }

    /**
     * Maneja la reconexión automática
     */
    handleReconnect() {
        if (this.reconnectAttempts >= this.maxReconnectAttempts) {
            console.error('❌ Máximo de intentos de reconexión alcanzado');
            return;
        }

        this.reconnectAttempts++;
        const delay = this.reconnectDelay * this.reconnectAttempts;

        console.log(`🔄 Reintentando conexión en ${delay}ms (intento ${this.reconnectAttempts}/${this.maxReconnectAttempts})`);

        setTimeout(() => {
            if (!this.connected && this.token) {
                console.log('🔄 Reconectando WebSocket...');
                this.connect(this.token, this.userId, this.userRole)
                    .catch(error => {
                        console.error('❌ Error en reconexión:', error);
                    });
            }
        }, delay);
    }

    /**
     * Desconectar del servidor
     */
    disconnect() {
        if (this.client && this.connected) {
            // Desuscribirse de todos los canales
            this.subscriptions.forEach((subscription, destination) => {
                this.unsubscribe(destination);
            });

            // Desactivar el cliente
            this.client.deactivate();
            this.connected = false;
            this.token = null;
            this.userId = null;
            this.userRole = null;
            console.log('✅ WebSocket desconectado correctamente');
        }
    }

    /**
     * Verifica si está conectado
     * @returns {boolean}
     */
    isConnected() {
        return this.connected;
    }
}

// Instancia singleton
const webSocketService = new WebSocketService();

export default webSocketService;
