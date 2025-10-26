package dk.lundogbendsen.scheduled;

import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;

public class ScheduledService {
    @Scheduled(cron = "*/10 * * * * *")
//    @Scheduled(cron = "0 1/2 * * * MON-SAT")
    public void myCronScheduledMethod() {
        System.out.println("CronTime is " + new Date());
    }

    @Scheduled(fixedRate = 10000)
    public void myRateScheduledMethod() {
        System.out.println("RateTime is " + new Date());
    }
}
