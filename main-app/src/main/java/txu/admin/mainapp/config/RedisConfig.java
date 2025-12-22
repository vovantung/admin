package txu.admin.mainapp.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import java.time.Duration;
@Configuration
@EnableCaching
@Slf4j
public class RedisConfig {


    @Bean
    @Primary
    public RedisConnectionFactory redisConnectionFactory(RedisProperties properties) {

        try {
            RedisSentinelConfiguration sentinel = new RedisSentinelConfiguration();
            sentinel.master(properties.getSentinel().getMaster());

            properties.getSentinel().getNodes().forEach(node -> {
                String[] p = node.split(":");
                sentinel.sentinel(p[0], Integer.parseInt(p[1]));
            });

            sentinel.setPassword(properties.getPassword());
            sentinel.setDatabase(properties.getDatabase());

            LettuceClientConfiguration clientConfig =
                    LettuceClientConfiguration.builder()
                            .commandTimeout(Duration.ofSeconds(2))
                            .shutdownTimeout(Duration.ofMillis(200))
                            .build();

            log.info("✅ Redis Sentinel ENABLED");
            return new LettuceConnectionFactory(sentinel, clientConfig);

        } catch (Exception e) {
            log.error("❌ Redis unavailable → DISABLE CACHE", e);
            return new NoOpRedisConnectionFactory(); // 🔥 CỐT LÕI
        }

    }

    @Bean
    public CacheErrorHandler cacheErrorHandler() {
        return new RedisCacheErrorHandler();
    }

}