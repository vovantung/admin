package txu.admin.mainapp.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;
@Configuration
@EnableCaching
@Slf4j
public class RedisConfig {

    @Bean
    @Primary
    public CacheManager cacheManager(RedisConnectionFactory redisFactory) {

        CacheManager redis =
                RedisCacheManager.builder(redisFactory)
                        .cacheDefaults(
                                RedisCacheConfiguration.defaultCacheConfig()
                                        .entryTtl(Duration.ofMinutes(10))
                        )
                        .build();

        CacheManager caffeine =
                new CaffeineCacheManager();

        return new CompositeCacheManager(redis, caffeine);
    }
}