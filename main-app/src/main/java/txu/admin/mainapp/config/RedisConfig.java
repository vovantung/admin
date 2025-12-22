package txu.admin.mainapp.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
@Slf4j
public class RedisConfig {

    @Bean
    public CacheManager cacheManager(
            ObjectProvider<RedisConnectionFactory> redisFactoryProvider
    ) {
        List<CacheManager> managers = new ArrayList<>();

        // Redis (optional)
        RedisConnectionFactory redisFactory = redisFactoryProvider.getIfAvailable();
        if (redisFactory != null) {
            RedisCacheManager redisCacheManager =
                    RedisCacheManager.builder(redisFactory)
                            .cacheDefaults(
                                    RedisCacheConfiguration.defaultCacheConfig()
                                            .entryTtl(Duration.ofMinutes(10))
                            )
                            .build();
            managers.add(redisCacheManager);
        }

        // Caffeine fallback
        CaffeineCacheManager caffeine = new CaffeineCacheManager();
        caffeine.setCaffeine(
                Caffeine.newBuilder()
                        .maximumSize(10_000)
                        .expireAfterWrite(10, TimeUnit.MINUTES)
        );
        managers.add(caffeine);

        CompositeCacheManager composite = new CompositeCacheManager();
        composite.setCacheManagers(managers);
        composite.setFallbackToNoOpCache(true);

        return composite;
    }

    @Bean
    public CacheErrorHandler cacheErrorHandler() {
        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(
                    RuntimeException exception, Cache cache, Object key) {
                log.error("Cache GET error on {} key={}", cache.getName(), key, exception);
            }

            @Override
            public void handleCachePutError(
                    RuntimeException exception, Cache cache, Object key, Object value) {
                log.error("Cache PUT error on {}", cache.getName(), exception);
            }

            @Override
            public void handleCacheEvictError(
                    RuntimeException exception, Cache cache, Object key) {
                log.error("Cache EVICT error on {}", cache.getName(), exception);
            }

            @Override
            public void handleCacheClearError(
                    RuntimeException exception, Cache cache) {
                log.error("Cache CLEAR error on {}", cache.getName(), exception);
            }
        };
    }
}