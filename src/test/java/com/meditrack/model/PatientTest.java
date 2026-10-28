package com.meditrack.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PatientTest {

    @Test
    void minorWhenUnderEighteen() {
        Patient p = new Patient("Child A", "a@x.com", "123", "NHS1", 10);
        assertTrue(p.isMinor());
    }

    @Test
    void notMinorWhenEighteenOrOver() {
        Patient p = new Patient("Adult B", "b@x.com", "456", "NHS2", 30);
        assertFalse(p.isMinor());
    }

    @Test
    void gettersAndSettersWork() {
        Patient p = new Patient();
        p.setName("Jane");
        p.setEmail("jane@clinic.com");
        p.setPhone("07000");
        p.setNhsNumber("NHS9");
        p.setAge(45);
        p.setId(5L);

        assertEquals("Jane", p.getName());
        assertEquals("jane@clinic.com", p.getEmail());
        assertEquals("07000", p.getPhone());
        assertEquals("NHS9", p.getNhsNumber());
        assertEquals(45, p.getAge());
        assertEquals(5L, p.getId());
    }
}
