package txu.admin.mainapp.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

@Slf4j
@Component
@RequiredArgsConstructor
public class ABC implements ABCD {

    private final RedisTemplate<String, Object> redisTemplate;
    private final Executor executor;

    @Override
    public <T> T get(
            String key,
            Class<T> type,
            Duration ttl,
            Supplier<T> loader
    ) {
        // 1️⃣ Try Redis (best-effort)
        try {
            Object cached = redisTemplate.opsForValue().get(key);
            if (cached != null) {
                log.info("Get result from Redis with key '" + key + "' is successful");
                return type.cast(cached);
            }
        } catch (Exception e) {
            log.warn("Redis GET failed, ignored. key={}", key);
        }

        // 2️⃣ DB is source of truth
        T value = loader.get();
        if (value != null) {
            log.info("Get result from DB is successful");
            // 3️⃣ Async warm cache
            asyncSet(key, value, ttl);
        }
        return value;
    }

    @Override
    public void evict(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.warn("Redis EVICT failed, ignored. key={}", key);
        }
    }

    private void asyncSet(String key, Object value, Duration ttl) {
        executor.execute(() -> {
            try {
                redisTemplate.opsForValue().set(key, value, ttl);
            } catch (Exception e) {
                log.warn("Redis SET failed, ignored. key={}", key);
            }
        });
    }
}

