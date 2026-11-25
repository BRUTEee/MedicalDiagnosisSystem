package com.medical.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PasswordHashToolTest {

    @Test
    void hashesMatchingPasswordAndClearsInputBuffers() {

        char[] password = "a-unique-password".toCharArray();
        char[] confirmation = "a-unique-password".toCharArray();

        String hash = PasswordHashTool.hashPassword(password, confirmation);

        assertTrue(PasswordUtil.isHashed(hash));
        assertTrue(PasswordUtil.verify("a-unique-password", hash));
        assertFalse(containsCharacters(password));
        assertFalse(containsCharacters(confirmation));
    }

    @Test
    void rejectsMismatchAndClearsInputBuffers() {

        char[] password = "a-unique-password".toCharArray();
        char[] confirmation = "a-different-password".toCharArray();

        assertThrows(IllegalArgumentException.class,
                () -> PasswordHashTool.hashPassword(password, confirmation));
        assertFalse(containsCharacters(password));
        assertFalse(containsCharacters(confirmation));
    }

    @Test
    void rejectsTooShortPasswordAndClearsInputBuffers() {

        char[] password = "short".toCharArray();
        char[] confirmation = "short".toCharArray();

        assertThrows(IllegalArgumentException.class,
                () -> PasswordHashTool.hashPassword(password, confirmation));
        assertFalse(containsCharacters(password));
        assertFalse(containsCharacters(confirmation));
    }

    private boolean containsCharacters(char[] value) {
        for (char character : value) {
            if (character != '\0') {
                return true;
            }
        }
        return false;
    }
}
