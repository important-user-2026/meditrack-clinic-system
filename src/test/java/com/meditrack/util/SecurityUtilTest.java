package com.meditrack.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SecurityUtilTest {

    @Test
    void pseudonymiseReturnsStableToken() {
        String a = SecurityUtil.pseudonymise("NHS123");
        String b = SecurityUtil.pseudonymise("NHS123");
        assertEquals(a, b);
        assertEquals(8, a.length());
    }

    @Test
    void pseudonymiseHandlesNull() {
        assertEquals("UNKNOWN", SecurityUtil.pseudonymise(null));
    }

    @Test
    void pseudonymiseDiffersByInput() {
        String a = SecurityUtil.pseudonymise("NHS123");
        String c = SecurityUtil.pseudonymise("NHS999");
        assertNotNull(c);
        // different references should normally produce different tokens
        org.junit.jupiter.api.Assertions.assertNotEquals(a, c);
    }
}
