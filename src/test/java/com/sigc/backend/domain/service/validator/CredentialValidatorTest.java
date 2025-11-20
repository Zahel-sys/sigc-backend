package com.sigc.backend.domain.service.validator;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CredentialValidatorTest {

    @Test
    void validateLoginCredentialsValid() {
        var res = CredentialValidator.validateLoginCredentials("user@example.com", "Secret123");
        assertTrue(res.isValid());
    }

    @Test
    void validateLoginCredentialsInvalidEmail() {
        var res = CredentialValidator.validateLoginCredentials("invalid-email", "Secret123");
        assertFalse(res.isValid());
        assertFalse(res.getErrors().isEmpty());
    }

    @Test
    void validateRegistrationCredentialsMismatchPasswords() {
        var res = CredentialValidator.validateRegistrationCredentials("u@e.com", "Secret123", "Other123");
        assertFalse(res.isValid());
    }

    @Test
    void validateEmailRejectsBadEmail() {
        var res = CredentialValidator.validateEmail("bad-email");
        assertFalse(res.isValid());
    }
}
