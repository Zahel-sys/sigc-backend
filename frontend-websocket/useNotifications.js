/**
 * useNotifications - Hook React para gestionar notificaciones WebSocket
 * 
 * Proporciona estado y funciones para trabajar con notificaciones en tiempo real
 */

import { useState, useEffect, useCallback, useRef } from 'react';
import webSocketService from './WebSocketService';

export const useNotifications = (token, userId, userRole) => {
    const [notifications, setNotifications] = useState([]);
    const [unreadCount, setUnreadCount] = useState(0);
    const [isConnected, setIsConnected] = useState(false);
    const [error, setError] = useState(null);
    const audioRef = useRef(null);

    /**
     * Inicializar conexión WebSocket
     */
    useEffect(() => {
        if (!token || !userId || !userRole) {
            console.warn('⚠️ Faltan credenciales para conectar WebSocket');
            return;
        }

        console.log('🔌 Iniciando conexión WebSocket...', { userId, userRole });

        // Conectar al servidor
        webSocketService.connect(token, userId, userRole)
            .then(() => {
                console.log('✅ WebSocket conectado desde hook');
                setIsConnected(true);
                setError(null);

                // Registrar handlers para notificaciones
                registerHandlers();
            })
            .catch((err) => {
                console.error('❌ Error conectando WebSocket desde hook:', err);
                setError(err.message || 'Error de conexión');
                setIsConnected(false);
            });

        // Cleanup al desmontar
        return () => {
            console.log('🔌 Desconectando WebSocket...');
            webSocketService.disconnect();
            setIsConnected(false);
        };
    }, [token, userId, userRole]);

    /**
     * Registrar handlers para diferentes tipos de notificaciones
     */
    const registerHandlers = useCallback(() => {
        // Handler para notificaciones personales
        webSocketService.onMessage('user', (notification) => {
            console.log('📬 Nueva notificación personal:', notification);
            addNotification(notification);
            playNotificationSound();
        });

        // Handler para notificaciones globales
        webSocketService.onMessage('global', (notification) => {
            console.log('📢 Nueva notificación global:', notification);
            addNotification(notification);
        });

        // Handler para notificaciones de admin
        if (userRole === 'ADMIN') {
            webSocketService.onMessage('admin', (notification) => {
                console.log('👑 Nueva notificación admin:', notification);
                addNotification(notification);
            });
        }

        // Handler para cambios de horarios
        if (userRole === 'DOCTOR' || userRole === 'PACIENTE') {
            webSocketService.onMessage('horarios', (notification) => {
                console.log('🕒 Cambio en horarios:', notification);
                addNotification(notification);
            });
        }
    }, [userRole]);

    /**
     * Agregar nueva notificación al estado
     */
    const addNotification = useCallback((notification) => {
        const newNotification = {
            ...notification,
            id: `${notification.tipo}_${Date.now()}`,
            read: false,
            receivedAt: new Date()
        };

        setNotifications(prev => [newNotification, ...prev]);
        setUnreadCount(prev => prev + 1);

        // Mostrar notificación del navegador si está permitido
        showBrowserNotification(notification);
    }, []);

    /**
     * Marcar notificación como leída
     */
    const markAsRead = useCallback((notificationId) => {
        setNotifications(prev => 
            prev.map(notif => 
                notif.id === notificationId 
                    ? { ...notif, read: true }
                    : notif
            )
        );
        setUnreadCount(prev => Math.max(0, prev - 1));
    }, []);

    /**
     * Marcar todas como leídas
     */
    const markAllAsRead = useCallback(() => {
        setNotifications(prev => 
            prev.map(notif => ({ ...notif, read: true }))
        );
        setUnreadCount(0);
    }, []);

    /**
     * Eliminar notificación
     */
    const removeNotification = useCallback((notificationId) => {
        setNotifications(prev => {
            const notification = prev.find(n => n.id === notificationId);
            if (notification && !notification.read) {
                setUnreadCount(c => Math.max(0, c - 1));
            }
            return prev.filter(notif => notif.id !== notificationId);
        });
    }, []);

    /**
     * Limpiar todas las notificaciones
     */
    const clearAll = useCallback(() => {
        setNotifications([]);
        setUnreadCount(0);
    }, []);

    /**
     * Reproducir sonido de notificación
     */
    const playNotificationSound = useCallback(() => {
        try {
            if (!audioRef.current) {
                audioRef.current = new Audio('/notification.mp3');
                audioRef.current.volume = 0.5;
            }
            audioRef.current.play().catch(err => {
                console.warn('⚠️ No se pudo reproducir sonido:', err);
            });
        } catch (error) {
            console.warn('⚠️ Error reproduciendo sonido:', error);
        }
    }, []);

    /**
     * Mostrar notificación nativa del navegador
     */
    const showBrowserNotification = useCallback((notification) => {
        if (!('Notification' in window)) {
            console.warn('⚠️ Notificaciones del navegador no soportadas');
            return;
        }

        if (Notification.permission === 'granted') {
            try {
                new Notification(notification.titulo || 'Nueva notificación', {
                    body: notification.mensaje,
                    icon: '/logo.png',
                    badge: '/badge.png',
                    tag: notification.tipo,
                    requireInteraction: notification.requiereAccion || false
                });
            } catch (error) {
                console.warn('⚠️ Error mostrando notificación del navegador:', error);
            }
        } else if (Notification.permission !== 'denied') {
            Notification.requestPermission().then(permission => {
                if (permission === 'granted') {
                    showBrowserNotification(notification);
                }
            });
        }
    }, []);

    /**
     * Solicitar permisos de notificación
     */
    const requestNotificationPermission = useCallback(async () => {
        if (!('Notification' in window)) {
            console.warn('⚠️ Notificaciones del navegador no soportadas');
            return false;
        }

        if (Notification.permission === 'granted') {
            return true;
        }

        try {
            const permission = await Notification.requestPermission();
            return permission === 'granted';
        } catch (error) {
            console.error('❌ Error solicitando permisos:', error);
            return false;
        }
    }, []);

    /**
     * Filtrar notificaciones por tipo
     */
    const getNotificationsByType = useCallback((tipo) => {
        return notifications.filter(n => n.tipo === tipo);
    }, [notifications]);

    /**
     * Obtener notificaciones no leídas
     */
    const getUnreadNotifications = useCallback(() => {
        return notifications.filter(n => !n.read);
    }, [notifications]);

    return {
        // Estado
        notifications,
        unreadCount,
        isConnected,
        error,

        // Acciones
        markAsRead,
        markAllAsRead,
        removeNotification,
        clearAll,
        requestNotificationPermission,

        // Consultas
        getNotificationsByType,
        getUnreadNotifications,

        // Servicio directo (para casos avanzados)
        webSocketService
    };
};

export default useNotifications;
