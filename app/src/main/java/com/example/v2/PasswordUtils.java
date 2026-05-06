package com.example.v2;

import android.util.Base64;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

final class PasswordUtils {

    private static final int SALT_BYTES   = 16;
    private static final int ITERATIONS   = 310_000;
    private static final int KEY_LENGTH   = 256;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";

    private PasswordUtils() {}

    /** Returns "salt:hash" to store in SharedPreferences. */
    static String hashNewPassword(String password) {
        byte[] salt = new byte[SALT_BYTES];
        new SecureRandom().nextBytes(salt);
        String saltB64 = Base64.encodeToString(salt, Base64.NO_WRAP);
        return saltB64 + ":" + computeHash(password, salt);
    }

    /** Verifies a plain-text password against a stored "salt:hash" string. */
    static boolean verifyPassword(String password, String stored) {
        if (stored == null || !stored.contains(":")) return false;
        String[] parts = stored.split(":", 2);
        byte[] salt = Base64.decode(parts[0], Base64.NO_WRAP);
        String expected = computeHash(password, salt);
        return constantTimeEquals(expected, parts[1]);
    }

    private static String computeHash(String password, byte[] salt) {
        try {
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] hash = factory.generateSecret(spec).getEncoded();
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) hex.append(String.format("%02x", b));
            return hex.toString();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("PBKDF2 not available", e);
        }
    }

    /** Timing-safe comparison to prevent timing attacks. */
    private static boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) return false;
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }
}
