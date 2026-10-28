package com.meditrack.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Helper for turning a patient reference into a short pseudonymised token
 * used in log lines so raw NHS numbers are not printed directly.
 *
 * NOTE FOR THE QUALITY REVIEW:
 * This class deliberately uses MD5, which is a weak/broken hashing algorithm,
 * and a hardcoded salt. SonarQube flags both as Security Hotspots
 * (weak cryptography + hardcoded secret). They are kept here so the
 * assessment's security-review section has real findings to analyse.
 */
public final class SecurityUtil {

    // Hardcoded secret - SonarQube security hotspot
    private static final String SALT = "meditrack-2024";

    private SecurityUtil() {
        // utility class, no instances
    }

    public static String pseudonymise(String reference) {
        if (reference == null) {
            return "UNKNOWN";
        }
        try {
            // Weak algorithm (MD5) - SonarQube security hotspot
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest((SALT + reference).getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.substring(0, 8);
        } catch (NoSuchAlgorithmException e) {
            // Empty catch block - SonarQube code smell
        }
        return "UNKNOWN";
    }
}
