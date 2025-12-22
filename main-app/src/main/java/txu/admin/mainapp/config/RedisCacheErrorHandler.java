package txu.admin.mainapp.config;

import org.springframework.cache.interceptor.CacheErrorHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.RedisConnectionFailureException;

@Slf4j
@Configuration
public class RedisCacheErrorHandler implements CacheErrorHandler {
    @Override
    public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
        log.warn("Redis GET error. Cache={}, key={}, reason={}",
                cache.getName(), key, exception.getMessage());
        // NUỐT LỖI → tiếp tục gọi DB
    }

    @Override
    public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
        log.warn("Redis PUT error. Cache={}, key={}, reason={}",
                cache.getName(), key, exception.getMessage());
    }

    @Override
    public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
        log.warn("Redis EVICT error. Cache={}, key={}, reason={}",
                cache.getName(), key, exception.getMessage());
    }

    @Override
    public void handleCacheClearError(RuntimeException exception, Cache cache) {
        log.warn("Redis CLEAR error. Cache={}, reason={}",
                cache.getName(), exception.getMessage());
    }
}
