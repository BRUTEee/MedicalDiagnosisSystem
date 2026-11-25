package com.medical.util;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public final class LoginAttemptLimiter {

    private final int maxFailures;
    private final long windowMillis;
    private final long lockMillis;
    private final int maxClients;
    private final Map<String, Attempt> attempts = new LinkedHashMap<>();

    public LoginAttemptLimiter(int maxFailures, long windowMillis,
                               long lockMillis, int maxClients) {
        if (maxFailures < 1 || windowMillis < 1 || lockMillis < 1
                || maxClients < 1) {
            throw new IllegalArgumentException("Login limits must be positive.");
        }
        this.maxFailures = maxFailures;
        this.windowMillis = windowMillis;
        this.lockMillis = lockMillis;
        this.maxClients = maxClients;
    }

    public synchronized long remainingLockMillis(String clientKey, long now) {
        prune(now);
        if (clientKey == null || clientKey.isBlank()) {
            return lockMillis;
        }
        Attempt attempt = attempts.get(clientKey);
        if (attempt == null && attempts.size() >= maxClients) {
            return lockMillis;
        }
        if (attempt == null) {
            return 0;
        }
        if (attempt.lockedUntil > now) {
            return attempt.lockedUntil - now;
        }
        if (attempt.lockedUntil > 0 && now >= attempt.lockedUntil) {
            attempts.remove(clientKey);
            return 0;
        }
        if (now - attempt.lastFailure >= windowMillis) {
            attempts.remove(clientKey);
        }
        return 0;
    }

    public synchronized void recordFailure(String clientKey, long now) {
        if (clientKey == null || clientKey.isBlank()) {
            return;
        }

        prune(now);
        Attempt attempt = attempts.get(clientKey);
        if (attempt == null) {
            if (attempts.size() >= maxClients) {
                return;
            }
            attempt = new Attempt(now);
            attempts.put(clientKey, attempt);
        } else if (now - attempt.firstFailure >= windowMillis) {
            attempt = new Attempt(now);
            attempts.put(clientKey, attempt);
        }

        attempt.failures++;
        attempt.lastFailure = now;
        if (attempt.failures >= maxFailures) {
            attempt.lockedUntil = now + lockMillis;
        }
    }

    public synchronized void recordSuccess(String clientKey) {
        if (clientKey != null) {
            attempts.remove(clientKey);
        }
    }

    private void prune(long now) {
        Iterator<Map.Entry<String, Attempt>> iterator = attempts.entrySet().iterator();
        while (iterator.hasNext()) {
            Attempt attempt = iterator.next().getValue();
            if (attempt.lockedUntil <= now
                    && now - attempt.lastFailure >= windowMillis) {
                iterator.remove();
            }
        }

    }

    private static final class Attempt {
        private final long firstFailure;
        private int failures;
        private long lastFailure;
        private long lockedUntil;

        private Attempt(long now) {
            firstFailure = now;
            lastFailure = now;
        }
    }
}
