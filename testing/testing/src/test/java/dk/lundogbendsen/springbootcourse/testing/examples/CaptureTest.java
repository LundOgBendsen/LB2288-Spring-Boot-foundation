package dk.lundogbendsen.springbootcourse.testing.examples;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CaptureTest {
    @Mock
    List<String> mockedList;

    @Captor
    ArgumentCaptor<String> argCaptor;

    @Test
    public void whenUseCaptorAnnotation_thenTheSam() {
        mockedList.add("one");
        verify(mockedList).add(argCaptor.capture());

        assertEquals("one", argCaptor.getValue());
    }

}
