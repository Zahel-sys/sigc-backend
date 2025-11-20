package com.sigc.backend.domain.exception;

import com.sigc.backend.shared.constant.ErrorCodes;

/**
 * Excepción: Usuario no encontrado.
 */
public class UserNotFoundException extends DomainException {
    
    public UserNotFoundException() {
        super(ErrorCodes.USER_NOT_FOUND, "Usuario no encontrado");
    }
    
    public UserNotFoundException(String message) {
        super(ErrorCodes.USER_NOT_FOUND, message);
    }
    
    public UserNotFoundException(Long userId) {
        super(ErrorCodes.USER_NOT_FOUND, "Usuario con ID " + userId + " no encontrado");
    }
}
