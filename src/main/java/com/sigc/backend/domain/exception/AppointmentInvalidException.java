package com.sigc.backend.domain.exception;

import com.sigc.backend.shared.constant.ErrorCodes;

/**
 * Excepción: Cita inválida.
 */
public class AppointmentInvalidException extends DomainException {
    
    public AppointmentInvalidException(String message) {
        super(ErrorCodes.APPOINTMENT_INVALID_DATE, message);
    }
}
