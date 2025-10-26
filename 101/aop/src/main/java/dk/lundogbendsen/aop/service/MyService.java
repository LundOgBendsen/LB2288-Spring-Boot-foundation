package dk.lundogbendsen.aop.service;

import org.springframework.stereotype.Service;

@Service
public class MyService {

    public void myTimedMethod(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
