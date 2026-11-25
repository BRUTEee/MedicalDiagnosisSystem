package com.medical.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class LoginAttemptLimiterTest {

    @Test
    void locksAfterConfiguredFailuresAndResetsAfterLockExpires() {

        LoginAttemptLimiter limiter = new LoginAttemptLimiter(3, 1000, 500, 10);

        limiter.recordFailure("client-a", 100);
        limiter.recordFailure("client-a", 200);
        assertEquals(0, limiter.remainingLockMillis("client-a", 200));

        limiter.recordFailure("client-a", 300);
        assertEquals(500, limiter.remainingLockMillis("client-a", 300));
        assertEquals(1, limiter.remainingLockMillis("client-a", 799));
        assertEquals(0, limiter.remainingLockMillis("client-a", 800));

        limiter.recordFailure("client-a", 801);
        assertEquals(0, limiter.remainingLockMillis("client-a", 801));
    }

    @Test
    void expiresFailuresOutsideTheWindow() {

        LoginAttemptLimiter limiter = new LoginAttemptLimiter(2, 1000, 500, 10);
        limiter.recordFailure("client-a", 100);
        limiter.recordFailure("client-a", 1100);

        assertEquals(0, limiter.remainingLockMillis("client-a", 1100));
    }

    @Test
    void successfulAuthenticationClearsFailures() {

        LoginAttemptLimiter limiter = new LoginAttemptLimiter(2, 1000, 500, 10);
        limiter.recordFailure("client-a", 100);
        limiter.recordSuccess("client-a");
        limiter.recordFailure("client-a", 200);

        assertEquals(0, limiter.remainingLockMillis("client-a", 200));
    }

    @Test
    void rejectsNewClientsWhenBoundedCapacityIsFull() {

        LoginAttemptLimiter limiter = new LoginAttemptLimiter(1, 1000, 500, 1);
        limiter.recordFailure("client-a", 100);

        assertEquals(500, limiter.remainingLockMillis("client-b", 100));
        assertEquals(500, limiter.remainingLockMillis(null, 100));
    }

    @Test
    void prunesInactiveClientsBeforeApplyingCapacityLimit() {

        LoginAttemptLimiter limiter = new LoginAttemptLimiter(1, 1000, 500, 1);
        limiter.recordFailure("client-a", 100);

        assertEquals(0, limiter.remainingLockMillis("client-b", 1100));
        limiter.recordFailure("client-b", 1100);
        assertEquals(500, limiter.remainingLockMillis("client-b", 1100));
    }

    @Test
    void rejectsInvalidLimits() {

        assertThrows(IllegalArgumentException.class,
                () -> new LoginAttemptLimiter(0, 1000, 500, 10));
        assertThrows(IllegalArgumentException.class,
                () -> new LoginAttemptLimiter(1, 0, 500, 10));
        assertThrows(IllegalArgumentException.class,
                () -> new LoginAttemptLimiter(1, 1000, 0, 10));
        assertThrows(IllegalArgumentException.class,
                () -> new LoginAttemptLimiter(1, 1000, 500, 0));
    }
}
