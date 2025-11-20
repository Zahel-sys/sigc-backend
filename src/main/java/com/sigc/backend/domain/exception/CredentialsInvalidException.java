package com.sigc.backend.domain.exception;

import com.sigc.backend.shared.constant.ErrorCodes;

/**
 * Excepción: Credenciales inválidas.
 */
public class CredentialsInvalidException extends DomainException {
    
    public CredentialsInvalidException() {
        super(ErrorCodes.AUTH_INVALID_CREDENTIALS, "Credenciales inválidas");
    }
    
    public CredentialsInvalidException(String message) {
        super(ErrorCodes.AUTH_INVALID_CREDENTIALS, message);
    }
}
