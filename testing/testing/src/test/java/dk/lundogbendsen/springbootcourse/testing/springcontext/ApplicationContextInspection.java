package dk.lundogbendsen.springbootcourse.testing.springcontext;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
//@SpringBootTest
public class ApplicationContextInspection {
    @Autowired
    ApplicationContext context;

    @Test
    public void test() {
        AutowireCapableBeanFactory beanFactory = context.getAutowireCapableBeanFactory();
    }
}
