/**
 * NotificationCenter - Componente React para mostrar notificaciones en tiempo real
 * 
 * Incluye: lista de notificaciones, badge con contador, sonidos, y actualización en tiempo real
 */

import React, { useState } from 'react';
import { Bell, X, Check, AlertCircle, Info, CheckCircle } from 'lucide-react';
import useNotifications from './useNotifications';

const NotificationCenter = ({ token, userId, userRole }) => {
    const [isOpen, setIsOpen] = useState(false);
    const {
        notifications,
        unreadCount,
        isConnected,
        error,
        markAsRead,
        markAllAsRead,
        removeNotification,
        clearAll,
        requestNotificationPermission
    } = useNotifications(token, userId, userRole);

    /**
     * Obtener icono según tipo de notificación
     */
    const getIcon = (tipo, prioridad) => {
        const iconClass = "w-5 h-5";
        
        if (prioridad === "ALTA") {
            return <AlertCircle className={`${iconClass} text-red-500`} />;
        }
        
        switch (tipo) {
            case 'CITA_CREADA':
            case 'CITA_CONFIRMADA':
                return <CheckCircle className={`${iconClass} text-green-500`} />;
            case 'CITA_CANCELADA':
                return <X className={`${iconClass} text-red-500`} />;
            case 'CITA_ACTUALIZADA':
                return <Info className={`${iconClass} text-blue-500`} />;
            case 'HORARIO_ACTUALIZADO':
                return <Info className={`${iconClass} text-orange-500`} />;
            default:
                return <Bell className={`${iconClass} text-gray-500`} />;
        }
    };

    /**
     * Obtener color de fondo según prioridad
     */
    const getBackgroundColor = (prioridad, read) => {
        if (read) return 'bg-gray-50';
        
        switch (prioridad) {
            case 'ALTA':
                return 'bg-red-50';
            case 'MEDIA':
                return 'bg-blue-50';
            case 'BAJA':
                return 'bg-gray-50';
            default:
                return 'bg-white';
        }
    };

    /**
     * Formatear timestamp
     */
    const formatTime = (timestamp) => {
        const date = new Date(timestamp);
        const now = new Date();
        const diffMs = now - date;
        const diffMins = Math.floor(diffMs / 60000);
        const diffHours = Math.floor(diffMins / 60);
        const diffDays = Math.floor(diffHours / 24);

        if (diffMins < 1) return 'Ahora';
        if (diffMins < 60) return `Hace ${diffMins} min`;
        if (diffHours < 24) return `Hace ${diffHours} h`;
        if (diffDays < 7) return `Hace ${diffDays} días`;
        
        return date.toLocaleDateString('es-ES', { 
            day: '2-digit', 
            month: 'short' 
        });
    };

    /**
     * Manejar click en notificación
     */
    const handleNotificationClick = (notification) => {
        markAsRead(notification.id);
        
        // Navegar a URL si existe
        if (notification.url) {
            window.location.href = notification.url;
        }
    };

    /**
     * Solicitar permisos al abrir por primera vez
     */
    React.useEffect(() => {
        if (isOpen) {
            requestNotificationPermission();
        }
    }, [isOpen]);

    return (
        <div className="relative">
            {/* Botón de notificaciones */}
            <button
                onClick={() => setIsOpen(!isOpen)}
                className="relative p-2 rounded-full hover:bg-gray-100 transition-colors"
                aria-label="Notificaciones"
            >
                <Bell className={`w-6 h-6 ${isConnected ? 'text-gray-700' : 'text-gray-400'}`} />
                
                {/* Badge con contador */}
                {unreadCount > 0 && (
                    <span className="absolute top-0 right-0 inline-flex items-center justify-center px-2 py-1 text-xs font-bold leading-none text-white transform translate-x-1/2 -translate-y-1/2 bg-red-500 rounded-full">
                        {unreadCount > 99 ? '99+' : unreadCount}
                    </span>
                )}

                {/* Indicador de conexión */}
                {isConnected && (
                    <span className="absolute bottom-0 right-0 w-3 h-3 bg-green-500 border-2 border-white rounded-full"></span>
                )}
            </button>

            {/* Panel de notificaciones */}
            {isOpen && (
                <div className="absolute right-0 mt-2 w-96 bg-white rounded-lg shadow-xl border border-gray-200 z-50">
                    {/* Header */}
                    <div className="flex items-center justify-between p-4 border-b border-gray-200">
                        <h3 className="text-lg font-semibold text-gray-800">
                            Notificaciones
                        </h3>
                        <div className="flex items-center gap-2">
                            {unreadCount > 0 && (
                                <button
                                    onClick={markAllAsRead}
                                    className="text-sm text-blue-600 hover:text-blue-700 font-medium"
                                >
                                    Marcar todas
                                </button>
                            )}
                            <button
                                onClick={() => setIsOpen(false)}
                                className="p-1 hover:bg-gray-100 rounded"
                            >
                                <X className="w-5 h-5 text-gray-500" />
                            </button>
                        </div>
                    </div>

                    {/* Estado de conexión */}
                    {!isConnected && (
                        <div className="p-3 bg-yellow-50 border-b border-yellow-200">
                            <p className="text-sm text-yellow-800 flex items-center gap-2">
                                <AlertCircle className="w-4 h-4" />
                                {error || 'Conectando al servidor...'}
                            </p>
                        </div>
                    )}

                    {/* Lista de notificaciones */}
                    <div className="max-h-96 overflow-y-auto">
                        {notifications.length === 0 ? (
                            <div className="p-8 text-center text-gray-500">
                                <Bell className="w-12 h-12 mx-auto mb-2 text-gray-300" />
                                <p>No hay notificaciones</p>
                            </div>
                        ) : (
                            <div className="divide-y divide-gray-100">
                                {notifications.map((notification) => (
                                    <div
                                        key={notification.id}
                                        className={`p-4 hover:bg-gray-50 cursor-pointer transition-colors ${
                                            getBackgroundColor(notification.prioridad, notification.read)
                                        }`}
                                        onClick={() => handleNotificationClick(notification)}
                                    >
                                        <div className="flex gap-3">
                                            {/* Icono */}
                                            <div className="flex-shrink-0 mt-1">
                                                {getIcon(notification.tipo, notification.prioridad)}
                                            </div>

                                            {/* Contenido */}
                                            <div className="flex-1 min-w-0">
                                                <div className="flex items-start justify-between gap-2">
                                                    <h4 className={`text-sm font-medium ${
                                                        notification.read ? 'text-gray-600' : 'text-gray-900'
                                                    }`}>
                                                        {notification.titulo}
                                                    </h4>
                                                    <span className="text-xs text-gray-500 whitespace-nowrap">
                                                        {formatTime(notification.timestamp)}
                                                    </span>
                                                </div>
                                                
                                                <p className="mt-1 text-sm text-gray-600">
                                                    {notification.mensaje}
                                                </p>

                                                {/* Badge de prioridad */}
                                                {notification.prioridad === 'ALTA' && (
                                                    <span className="inline-block mt-2 px-2 py-1 text-xs font-medium text-red-700 bg-red-100 rounded">
                                                        Urgente
                                                    </span>
                                                )}

                                                {/* Indicador de acción requerida */}
                                                {notification.requiereAccion && (
                                                    <span className="inline-block mt-2 px-2 py-1 text-xs font-medium text-orange-700 bg-orange-100 rounded">
                                                        Requiere acción
                                                    </span>
                                                )}
                                            </div>

                                            {/* Botones de acción */}
                                            <div className="flex flex-col gap-1">
                                                {!notification.read && (
                                                    <button
                                                        onClick={(e) => {
                                                            e.stopPropagation();
                                                            markAsRead(notification.id);
                                                        }}
                                                        className="p-1 hover:bg-gray-200 rounded"
                                                        title="Marcar como leída"
                                                    >
                                                        <Check className="w-4 h-4 text-green-600" />
                                                    </button>
                                                )}
                                                <button
                                                    onClick={(e) => {
                                                        e.stopPropagation();
                                                        removeNotification(notification.id);
                                                    }}
                                                    className="p-1 hover:bg-gray-200 rounded"
                                                    title="Eliminar"
                                                >
                                                    <X className="w-4 h-4 text-gray-500" />
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>

                    {/* Footer */}
                    {notifications.length > 0 && (
                        <div className="p-3 border-t border-gray-200 bg-gray-50">
                            <button
                                onClick={clearAll}
                                className="w-full text-sm text-red-600 hover:text-red-700 font-medium"
                            >
                                Limpiar todas
                            </button>
                        </div>
                    )}
                </div>
            )}
        </div>
    );
};

export default NotificationCenter;
