package dk.lundogbendsen.springbootcourse.testing.validation;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CprValidationTest {
    CprValidation cprValidation = new CprValidation();

    @Test
    public void testValidCpr() {
        String cpr = "2715023388";
        final boolean b = cprValidation.validateCpr(cpr);
        assertTrue(b);
    }

    @Test
    public void testInvalidCpr() {
        String cpr = "2715023389";
        final boolean b = cprValidation.validateCpr(cpr);
        assertFalse(b);
    }

    @Test
    public void testInvalidCprTooShort() {
        String cpr = "27150233";
        final boolean b = cprValidation.validateCpr(cpr);
        assertFalse(b);
    }

}