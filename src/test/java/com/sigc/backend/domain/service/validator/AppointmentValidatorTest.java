package com.sigc.backend.domain.service.validator;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
 
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AppointmentValidatorTest {

    @Test
    void validateDate_inPast_returnsError() {
        LocalDateTime past = LocalDateTime.now().minusDays(1).withHour(10).withMinute(0);
        List<String> errors = AppointmentValidator.validateDate(past);
        assertFalse(errors.isEmpty());
        assertTrue(errors.stream().anyMatch(s -> s.contains("no puede ser en el pasado") || s.contains("no puede ser en el pasado")));
    }

    @Test
    void validateDate_invalidMinutes_returnsError() {
        LocalDateTime date = LocalDateTime.now().plusDays(1).withHour(9).withMinute(15);
        List<String> errors = AppointmentValidator.validateDate(date);
        assertFalse(errors.isEmpty());
        assertTrue(errors.stream().anyMatch(s -> s.contains("slots de 30 minutos")));
    }

    @Test
    void validateDescription_empty_returnsError() {
        List<String> errors = AppointmentValidator.validateDescription("");
        assertFalse(errors.isEmpty());
        assertTrue(errors.get(0).contains("no puede estar vacía"));
    }

    @Test
    void validateDoctor_invalid_returnsError() {
        List<String> errors = AppointmentValidator.validateDoctor(0L);
        assertFalse(errors.isEmpty());
    }

    @Test
    void validate_validAppointment_noErrors() {
        LocalDateTime date = LocalDateTime.now().plusDays(2).withHour(10).withMinute(0);
        List<String> errors = AppointmentValidator.validate(date, "Consulta general", 1L);
        assertTrue(AppointmentValidator.isValid(errors));
    }
}
