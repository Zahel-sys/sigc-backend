package com.sigc.backend.domain.exception;

import com.sigc.backend.shared.constant.ErrorCodes;

/**
 * Excepción: Email ya registrado.
 */
public class EmailAlreadyRegisteredException extends DomainException {
    
    public EmailAlreadyRegisteredException(String email) {
        super(ErrorCodes.USER_EMAIL_ALREADY_EXISTS, "El email " + email + " ya está registrado");
    }
}
