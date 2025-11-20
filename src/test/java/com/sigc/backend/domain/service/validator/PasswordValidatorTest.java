package com.sigc.backend.domain.service.validator;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PasswordValidatorTest {

    @Test
    void validPasswordPassesValidation() {
        var result = PasswordValidator.validate("Abcdef12");
        assertTrue(result.isValid());
    }

    @Test
    void shortPasswordFails() {
        var result = PasswordValidator.validate("Ab1");
        assertFalse(result.isValid());
    }

    @Test
    void matchReturnsTrueForEqualPasswords() {
        assertTrue(PasswordValidator.match("Secret123", "Secret123"));
    }

    @Test
    void matchReturnsFalseForNullsOrDifferent() {
        assertFalse(PasswordValidator.match(null, "x"));
        assertFalse(PasswordValidator.match("a", null));
        assertFalse(PasswordValidator.match("a", "b"));
    }

    @Test
    void validateConfirmationDetectsMismatch() {
        var res = PasswordValidator.validateConfirmation("Secret123", "Other123");
        assertFalse(res.isValid());
    }
}
