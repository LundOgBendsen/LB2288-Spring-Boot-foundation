package dk.lundogbendsen.aop.examples;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AopAdviceExamplesTest {

    @Autowired
    private DummyService dummyService;

    @Test
    void testBeforeAdvice() {
        assertDoesNotThrow(() -> dummyService.processOrder("ORD-999"));
    }

    @Test
    void testAfterReturningAdvice() {
        String discountHigh = dummyService.calculateDiscount(200.0, 150);
        assertEquals("20% DISCOUNT", discountHigh);

        String discountLow = dummyService.calculateDiscount(50.0, 20);
        assertEquals("5% DISCOUNT", discountLow);
    }

    @Test
    void testAfterThrowingAdvice() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                dummyService.transferFunds("ACC-100", "ACC-200", -10.0)
        );
        assertEquals("Transfer amount must be positive, but was: -10.0", ex.getMessage());
    }

    @Test
    void testAfterAdvice() {
        // Successful execution - @After still triggers
        assertDoesNotThrow(() -> dummyService.processOrder("ORD-AFTER-TEST"));

        // Throwing execution - @After still triggers
        assertThrows(IllegalArgumentException.class, () ->
                dummyService.transferFunds("ACC-1", "ACC-2", 0.0)
        );
    }

    @Test
    void testAroundAdvice() {
        String report = dummyService.generateReport("Quarterly-Financials");
        assertNotNull(report);
        assertTrue(report.startsWith("Report: Quarterly-Financials"));
        assertTrue(report.endsWith("[Processed by @Around]"), "Around advice should have appended '[Processed by @Around]'");
    }
}
