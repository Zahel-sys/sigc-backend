package com.sigc.backend.exception;

/**
 * Excepción lanzada cuando se intenta registrar un email que ya existe
 */
public class EmailDuplicadoException extends RuntimeException {
    
    public EmailDuplicadoException(String mensaje) {
        super(mensaje);
    }
}
