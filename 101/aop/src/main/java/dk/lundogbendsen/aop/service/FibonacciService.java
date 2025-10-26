package dk.lundogbendsen.aop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FibonacciService {

    @Autowired
    FibonacciService fibonacciService;

    public int fib(int n) {
        if (n <= 1)
            return n;
        return fibonacciService.fib(n-1) + fibonacciService.fib(n-2);
    }
}
