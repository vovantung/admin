package txu.admin.mainapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import txu.admin.mainapp.dao.AccountDao;
import txu.admin.mainapp.entity.AccountEntity;
import txu.common.exception.ConflictException;
import txu.common.exception.NotFoundException;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountDao accountDao;

    @Transactional
    public AccountEntity createOrUpdate(AccountEntity accountEntity) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        accountEntity.setPassword(bCryptPasswordEncoder.encode(accountEntity.getPassword()));
        accountEntity.setCreatedAt(DateTime.now().toDate());
        accountEntity.setUpdateAt(DateTime.now().toDate());
        AccountEntity account = null;
        try {
            account = accountDao.save(accountEntity);
        } catch (DataIntegrityViolationException ex) {
            // Log hoặc custom response
            log.warn(ex.getMessage());
            throw new ConflictException(ex.getMessage());
//            if (ex.getCause() instanceof ConstraintViolationException) {
//                Throwable realCause = ex.getCause().getCause();
//                if (realCause instanceof SQLException && realCause.getMessage().contains("ORA-00001")) {
//                    log.warn("Username đã tồn tại.");
//                    throw new ConflictException("Username đã tồn tại.");
//                }
//            }
//            throw ex; // nếu không phải lỗi unique thì ném lại
        }

        return account;
    }

    @Transactional
    public AccountEntity getByUsername(String username) {
        AccountEntity user = accountDao.getByUsername(username);
        if (user == null) {
            throw new NotFoundException("User is not found");
        }
        return user;
    }

    public List<AccountEntity> getWithLimit(int limit) {
        return accountDao.getWithLimit(limit);
    }

    public boolean removeByUsername(String username) {
        AccountEntity account = accountDao.getByUsername(username);
        if (account == null) {
            throw new NotFoundException("User is not found");
        }
        accountDao.remove(account);
        return true;
    }
}
