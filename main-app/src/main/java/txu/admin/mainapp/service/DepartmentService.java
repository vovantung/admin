package txu.admin.mainapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import txu.admin.mainapp.dao.DepartmentDao;
import txu.admin.mainapp.entity.DepartmentEntity;
import txu.admin.mainapp.event.DepartmentLoadedEvent;
import txu.common.exception.BadParameterException;
import txu.common.exception.NotFoundException;
import txu.common.exception.TxException;

import java.time.Duration;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentDao departmentDao;

    @Transactional
    public DepartmentEntity createOrUpdate(DepartmentEntity departmentEntity) {

        // Add new
        if (departmentEntity.getId() == null || departmentEntity.getId() == 0) {
            if (departmentEntity.getName() == null || departmentEntity.getName().isEmpty()) {
                throw new BadParameterException("Name fied is required");
            }

            departmentEntity.setCreatedAt(DateTime.now().toDate());
            departmentEntity.setUpdatedAt(DateTime.now().toDate());
            DepartmentEntity department = null;

            try {
                department = departmentDao.save(departmentEntity);
            } catch (DataIntegrityViolationException ex) {
                log.warn(ex.getMessage());
                throw new TxException("Cannot save account");
            }
            return department;
        }

        // Update
        DepartmentEntity department = departmentDao.findById(departmentEntity.getId());

        if (department != null) {

            if (departmentEntity.getName() == null || departmentEntity.getName().isEmpty()) {
                throw new BadParameterException("Name fied is required");
            }

            if (departmentEntity.getName() != null && !departmentEntity.getName().isEmpty()) {
                department.setName(departmentEntity.getName());
            }
            if (departmentEntity.getDescription() != null && !departmentEntity.getDescription().isEmpty()) {
                department.setDescription(departmentEntity.getDescription());
            }
            department.setUpdatedAt(DateTime.now().toDate());
            try {
                return departmentDao.save(department);
            } catch (DataIntegrityViolationException ex) {
                log.warn(ex.getMessage());
                throw new TxException("Cannot save department");
            }
        } else {
            throw new NotFoundException("Department not found");
        }
    }

    public List<DepartmentEntity> getWithLimit(int limit) {
        return departmentDao.getWithLimit(limit);
    }

//    private final ApplicationEventPublisher publisher;
//
//    public DepartmentEntity getById(int id) {
//        DepartmentEntity dept = departmentDao.findById(id);
//
//        // bắn event, không phụ thuộc Redis
//        publisher.publishEvent(new DepartmentLoadedEvent(id, dept));
//
//        return dept;
//    }

    private final RedisTemplate<String, Object> redisTemplate;

    public DepartmentEntity getById(int id) {
        String key = "department:" + id;

        // 1️⃣ Try Redis (best-effort)
        try {
            DepartmentEntity cached =
                    (DepartmentEntity) redisTemplate.opsForValue().get(key);
            if (cached != null) {
                log.info("Get result from Redis is successful");
                return cached;
            }
        } catch (Exception e) {
            log.warn("Redis GET failed – ignored");
        }

        // 2️⃣ DB là source of truth
        DepartmentEntity dept = departmentDao.findById(id);
        log.info("Get result from DB is successful");

        // 3️⃣ Try Redis SET (best-effort)
        try {
            redisTemplate.opsForValue().set(
                    key, dept, Duration.ofMinutes(10)
            );
        } catch (Exception e) {
            log.warn("Redis SET failed – ignored");
        }
        return dept;
    }

    public boolean removeById(int id) {
        DepartmentEntity department = departmentDao.findById(id);
        if (department == null) {
            throw new NotFoundException("Department is not found");
        }

        try {
            departmentDao.remove(department);
        } catch (DataIntegrityViolationException ex) {
            log.warn(ex.getMessage());
            throw new TxException(ex.getMessage());
        }
        return true;
    }
}
