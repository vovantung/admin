package txu.admin.mainapp.service;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RedisHealth {

    private final RedisConnectionFactory factory;
    private volatile boolean available = true;

    public RedisHealth(RedisConnectionFactory factory) {
        this.factory = factory;
    }

    @Scheduled(fixedDelay = 5000)
    public void check() {
        try (var c = factory.getConnection()) {
            c.ping();
            available = true;
        } catch (Exception e) {
            available = false;
        }
    }

    public boolean isAvailable() {
        return available;
    }
}