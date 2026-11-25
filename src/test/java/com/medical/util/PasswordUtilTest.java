package com.medical.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PasswordUtilTest {

    @Test
    void hashUsesPbkdf2FormatWithIterationCount() {

        String hash = PasswordUtil.hash("doctor123");

        String[] parts = hash.split("\\$");

        assertEquals(4, parts.length);
        assertEquals("pbkdf2", parts[0]);
        assertEquals("210000", parts[1]);
        assertFalse(parts[2].isEmpty());
        assertFalse(parts[3].isEmpty());
    }

    @Test
    void hashIsSaltedSoEqualPasswordsDiffer() {
        assertNotEquals(PasswordUtil.hash("secret123"),
                        PasswordUtil.hash("secret123"));
    }

    @Test
    void verifyAcceptsCorrectPassword() {

        String hash = PasswordUtil.hash("doctor123");

        assertTrue(PasswordUtil.verify("doctor123", hash));
    }

    @Test
    void verifyRejectsWrongPassword() {

        String hash = PasswordUtil.hash("doctor123");

        assertFalse(PasswordUtil.verify("Doctor123", hash));
        assertFalse(PasswordUtil.verify("", hash));
    }

    @Test
    void verifyRejectsMalformedAndNullHashes() {

        assertFalse(PasswordUtil.verify("doctor123", null));
        assertFalse(PasswordUtil.verify("doctor123", ""));
        assertFalse(PasswordUtil.verify("doctor123", "pbkdf2$notanumber$a$b"));
        assertFalse(PasswordUtil.verify("doctor123", "pbkdf2$210000$bad-base64$bad"));
        assertFalse(PasswordUtil.verify("doctor123", "pbkdf2$210000$c2FsdA=="));
    }

    @Test
    void verifyRejectsExcessiveIterationsAndOutOfRangeKeyMaterial() {

        String salt = java.util.Base64.getEncoder().encodeToString(
                new byte[16]);
        String hash = java.util.Base64.getEncoder().encodeToString(
                new byte[32]);

        assertFalse(PasswordUtil.verify("secret123",
                "pbkdf2$2147483647$" + salt + "$" + hash));
        assertFalse(PasswordUtil.verify("secret123",
                "pbkdf2$210000$AA==$" + hash));
    }

    @Test
    void isHashedDistinguishesLegacyPlainText() {

        assertTrue(PasswordUtil.isHashed(PasswordUtil.hash("doctor123")));
        assertFalse(PasswordUtil.isHashed("doctor123"));
        assertFalse(PasswordUtil.isHashed(null));
    }

    @Test
    void legacyPlainTextStillAuthenticatesButIsNotHashed() {

        // Existing accounts store plain text; UserDAO upgrades them on login.
        assertTrue(PasswordUtil.verify("doctor123", "doctor123"));
        assertFalse(PasswordUtil.verify("wrong", "doctor123"));
        assertFalse(PasswordUtil.isHashed("doctor123"));
    }

    @Test
    void verifyRejectsNullPassword() {
        assertFalse(PasswordUtil.verify(null, PasswordUtil.hash("doctor123")));
    }

    @Test
    void lengthLimitsAreSane() {
        assertTrue(PasswordUtil.MIN_LENGTH > 0);
        assertTrue(PasswordUtil.MAX_LENGTH > PasswordUtil.MIN_LENGTH);
    }
}
