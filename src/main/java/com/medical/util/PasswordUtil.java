package com.medical.util;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Password hashing with PBKDF2-HMAC-SHA256.
 *
 * Stored format: pbkdf2$<iterations>$<salt base64>$<hash base64>
 */
public final class PasswordUtil {

    private static final String PREFIX = "pbkdf2";
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 210_000;
    private static final int MIN_VERIFY_ITERATIONS = 100_000;
    private static final int MAX_VERIFY_ITERATIONS = 1_000_000;
    private static final int SALT_BYTES = 16;
    private static final int KEY_BITS = 256;

    public static final int MIN_LENGTH = 8;
    public static final int MAX_LENGTH = 128;

    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordUtil() {
    }

    public static String hash(String password) {

        byte[] salt = new byte[SALT_BYTES];
        RANDOM.nextBytes(salt);

        byte[] hash = derive(password, salt, ITERATIONS, KEY_BITS);

        return PREFIX + "$" + ITERATIONS
                + "$" + Base64.getEncoder().encodeToString(salt)
                + "$" + Base64.getEncoder().encodeToString(hash);
    }

    /** Returns true when the stored value is in the hashed format. */
    public static boolean isHashed(String stored) {
        return stored != null && stored.startsWith(PREFIX + "$");
    }

    /**
     * Verifies a password against a stored value. Values that are not in the
     * hashed format are treated as legacy plain text so existing accounts
     * keep working; callers should re-hash those after a successful login.
     */
    public static boolean verify(String password, String stored) {

        if (password == null || stored == null || stored.isEmpty()) {
            return false;
        }

        if (!isHashed(stored)) {
            return MessageDigest.isEqual(
                    password.getBytes(StandardCharsets.UTF_8),
                    stored.getBytes(StandardCharsets.UTF_8));
        }

        String[] parts = stored.split("\\$");

        if (parts.length != 4) {
            return false;
        }

        try {
            int iterations = Integer.parseInt(parts[1]);
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expected = Base64.getDecoder().decode(parts[3]);

            if (iterations < MIN_VERIFY_ITERATIONS
                    || iterations > MAX_VERIFY_ITERATIONS
                    || salt.length < 16 || salt.length > 64
                    || expected.length < 16 || expected.length > 64) {
                return false;
            }

            byte[] actual = derive(
                    password, salt, iterations, expected.length * 8);

            return MessageDigest.isEqual(expected, actual);

        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static byte[] derive(String password, byte[] salt,
                                 int iterations, int keyBits) {

        PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(), salt, iterations, keyBits);

        try {
            return SecretKeyFactory.getInstance(ALGORITHM)
                    .generateSecret(spec)
                    .getEncoded();

        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Password hashing unavailable.", e);

        } finally {
            spec.clearPassword();
        }
    }
}
