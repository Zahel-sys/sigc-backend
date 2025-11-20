package com.sigc.backend.domain.exception;

/**
 * Excepción base del dominio.
 * Todas las excepciones de negocio heredan de esta.
 */
public class DomainException extends RuntimeException {
    
    private final String errorCode;
    
    public DomainException(String message) {
        super(message);
        this.errorCode = "DOMAIN_ERROR";
    }
    
    public DomainException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public DomainException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "DOMAIN_ERROR";
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}
