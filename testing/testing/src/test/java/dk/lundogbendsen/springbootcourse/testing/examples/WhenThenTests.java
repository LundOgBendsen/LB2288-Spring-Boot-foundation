package dk.lundogbendsen.springbootcourse.testing.examples;


import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import static com.jayway.jsonpath.internal.path.PathCompiler.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

// Examples from https://www.vogella.com/tutorials/Mockito/article.html
public class WhenThenTests {


    // demonstrates the return of multiple values
    @Test
    public void testMoreThanOneReturnValue() {
        Iterator<String> i = mock(Iterator.class);
        when(i.next())
                .thenReturn("Mockito")
                .thenReturn("rocks");
        String result = i.next() + " " + i.next();
        //assert
        assertEquals("Mockito rocks", result);
    }

    // this test demonstrates how to return values based on the input
    @Test
    public void testReturnValueDependentOnMethodParameter() {
        Comparable<String> c = mock(Comparable.class);
        when(c.compareTo("Mockito")).thenReturn(1);
        when(c.compareTo("Eclipse")).thenReturn(2);
        //assert
        assertEquals(1, c.compareTo("Mockito"));
    }

// this test demonstrates how to return values independent of the input value

    @Test
    public void testReturnValueInDependentOnMethodParameter() {
        Comparable<Integer> c = mock(Comparable.class);
        when(c.compareTo(anyInt())).thenReturn(-1);
        //assert
        assertEquals(-1, c.compareTo(9));
    }

// return a value based on the type of the provide parameter

    @Test
    public void testReturnValueInDependentOnMethodParameter2() {
        Comparable<HashMap> c = mock(Comparable.class);
        when(c.compareTo(isA(HashMap.class))).thenReturn(0);
        //assert
        assertEquals(0, c.compareTo(new HashMap()));
    }

// The when(…).thenReturn(…) method chain can be used to throw an exception.
    @Test
    public void testExceptionWhenExpectationsAreNotMet() {
        Properties properties = mock(Properties.class);

        when(properties.get("Anddroid")).thenThrow(new IllegalArgumentException("not in properties"));

        try {
            properties.get("Anddroid");
            fail("Anddroid is misspelled");
        } catch (IllegalArgumentException ex) {
            // good!
        }
    }
}
