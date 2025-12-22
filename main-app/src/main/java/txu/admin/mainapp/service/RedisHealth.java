package txu.admin.mainapp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
    @Slf4j
    public class RedisHealth {

        private final RedisConnectionFactory factory;
        private final AtomicBoolean available = new AtomicBoolean(true);

        public RedisHealth(RedisConnectionFactory factory) {
            this.factory = factory;
        }

        public boolean isAvailable() {
            return available.get();
        }

        @Scheduled(fixedDelay = 5000)
        public void check() {
            try {
                factory.getConnection().ping();
                available.set(true);
            } catch (Exception e) {
                available.set(false);
                log.warn("Redis DOWN → cache disabled");
            }
        }
    }