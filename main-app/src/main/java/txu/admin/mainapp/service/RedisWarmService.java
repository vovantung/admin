package txu.admin.mainapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import txu.admin.mainapp.entity.DepartmentEntity;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnBean(RedisConnectionFactory.class)
public class RedisWarmService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Async
    public void warmDepartment(int id, DepartmentEntity dept) {
        try {
            redisTemplate.opsForValue()
                    .set("department::" + id, dept, Duration.ofMinutes(10));
        } catch (Exception e) {
            log.warn("Redis warm failed – ignored");
        }
    }
}