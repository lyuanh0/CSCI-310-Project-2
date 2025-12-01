package com.example.chatpet;


import org.junit.Test;
import static org.junit.Assert.*;

import com.example.chatpet.util.ValidationUtils;

public class ValidationUtilsTest {

    // ---------- Username ----------
    @Test
    public void testValidUsername_true() {
        assertTrue(ValidationUtils.isValidUsername("John"));
    }

    @Test
    public void testValidUsername_tooShort() {
        assertFalse(ValidationUtils.isValidUsername("Jo"));
    }

    @Test
    public void testValidUsername_null() {
        assertFalse(ValidationUtils.isValidUsername(null));
    }

    @Test
    public void testValidUsername_emptyString() {
        assertFalse(ValidationUtils.isValidUsername(""));
    }

    // ---------- Date ----------
    @Test
    public void testValidDate_true() {
        assertTrue(ValidationUtils.isValidDate("12-25-2024"));
    }

    @Test
    public void testValidDate_invalidMonth() {
        assertFalse(ValidationUtils.isValidDate("13-25-2024"));
    }
    @Test
    public void testValidDate_invalidSeparation() {
        assertFalse(ValidationUtils.isValidDate("12/25/2024"));
    }

    @Test
    public void testValidDate_invalidFormat() {
        assertFalse(ValidationUtils.isValidDate("2024-12-25"));
    }

    @Test
    public void testValidDate_empty() {
        assertFalse(ValidationUtils.isValidDate(""));
    }

}
