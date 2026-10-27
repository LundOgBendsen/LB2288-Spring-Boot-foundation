package dk.lundogbendsen.aop.examples;

import org.springframework.stereotype.Service;

/**
 * Shared dummy service class used across all Spring AOP advice examples.
 */
@Service
public class DummyService {

    /**
     * Demonstrates a normal business operation (used with @Before and @After).
     */
    public void processOrder(String orderId) {
        System.out.println("  -> [DummyService.processOrder] Processing order: " + orderId);
    }

    /**
     * Demonstrates a method returning a result (used with @AfterReturning).
     */
    public String calculateDiscount(double price, int customerPoints) {
        System.out.println("  -> [DummyService.calculateDiscount] Calculating discount for price: " + price + ", points: " + customerPoints);
        if (customerPoints >= 100) {
            return "20% DISCOUNT";
        }
        return "5% DISCOUNT";
    }

    /**
     * Demonstrates a method that throws an exception on invalid input (used with @AfterThrowing and @After).
     */
    public void transferFunds(String fromAccount, String toAccount, double amount) {
        System.out.println("  -> [DummyService.transferFunds] Transferring $" + amount + " from " + fromAccount + " to " + toAccount);
        if (amount <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive, but was: " + amount);
        }
    }

    /**
     * Demonstrates a method with computation/delay (used with @Around for timing and return value modification).
     */
    public String generateReport(String reportName) {
        System.out.println("  -> [DummyService.generateReport] Generating report: " + reportName);
        try {
            Thread.sleep(25);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "Report: " + reportName;
    }
}
