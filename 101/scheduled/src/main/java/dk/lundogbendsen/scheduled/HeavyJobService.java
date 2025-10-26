package dk.lundogbendsen.scheduled;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class HeavyJobService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Scheduled(cron = "*/10 * * * * *")
    public void doHeavyJob() {
        if (takeDistributedLock()) {
            System.out.println("I got the distributed Lock - I'll start working!");
        } else {
            System.out.println("Someone else got the distributed lock - I'll just skip this one...");
        }

    }

    private boolean takeDistributedLock() {
        final Boolean transfer_lock = redisTemplate.opsForValue().setIfAbsent("TRANSFER_LOCK", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), 5, TimeUnit.SECONDS);
        return transfer_lock;
    }
}
