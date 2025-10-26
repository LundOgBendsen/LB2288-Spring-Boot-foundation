package dk.lundogbendsen.springbootcourse.testing.examples;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

// Demonstrates how to verify the interactions with the mocks on a test target.
public class VerifyTest {
    @Test
    public void testVerify()  {
        // create and configure mock
        MyImportantService test = Mockito.mock(MyImportantService.class);
        when(test.getUniqueId()).thenReturn(43);


        // call method testing on the mock with parameter 12
        test.testing(12);
        test.getUniqueId();
        test.getUniqueId();


        // now check if method testing was called with the parameter 12
        verify(test).testing(ArgumentMatchers.eq(12));

        // was the method called twice?
        verify(test, times(2)).getUniqueId();

        // other alternatives for verifiying the number of method calls for a method
        verify(test, never()).someMethod("never called");
        verify(test, atMostOnce()).someMethod("at most once");
        verify(test, atLeast(2)).getUniqueId();
        verify(test, times(2)).getUniqueId();
        verify(test, atMost(3)).getUniqueId();
        // This let's you check that no other methods where called on this object.
        // You call it after you have verified the expected method calls.
        verifyNoMoreInteractions(test);
    }
}
