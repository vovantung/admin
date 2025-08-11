package txu.admin.mainapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import txu.admin.mainapp.dao.AccountDao;
import txu.admin.mainapp.dao.DepartmentDao;
import txu.admin.mainapp.entity.AccountEntity;
import txu.admin.mainapp.security.CustomUserDetails;
import txu.common.exception.BadParameterException;
import txu.common.exception.ConflictException;
import txu.common.exception.NotFoundException;
import txu.common.exception.TxException;

import java.util.Collection;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountDao accountDao;
    private final DepartmentDao departmentDao;

    @Transactional
    public AccountEntity createOrUpdate(AccountEntity accountEntity) {

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        // Add new
        if (accountEntity.getId() == null || accountEntity.getId() == 0) {
            if (accountEntity.getUsername() == null || accountEntity.getUsername().isEmpty()) {
                throw new BadParameterException("Username is required");
            }
            if (accountEntity.getPassword() == null || accountEntity.getPassword().isEmpty()) {
                throw new BadParameterException("Password is required");
            }

            if (accountEntity.getEmail() == null || accountEntity.getEmail().isEmpty()) {
                throw new BadParameterException("Email is required");
            }

            if (accountDao.getByUsername(accountEntity.getUsername()) != null) {
                throw new ConflictException("Account with [" + accountEntity.getUsername() + "]  already exists");
            }

            if (accountDao.getByEmail(accountEntity.getEmail()) != null) {
                throw new ConflictException("Account with [" + accountEntity.getEmail() + "]  already exists");
            }

            if (departmentDao.findById(accountEntity.getDepartment().getId()) == null) {
                throw new NotFoundException("Department not found");
            }

            if (accountEntity.getPassword() != null && !accountEntity.getPassword().isEmpty()) {
                accountEntity.setPassword(bCryptPasswordEncoder.encode(accountEntity.getPassword()));
            }
            accountEntity.setCreatedAt(DateTime.now().toDate());
            accountEntity.setUpdateAt(DateTime.now().toDate());
            AccountEntity account = null;

            try {
                account = accountDao.save(accountEntity);
            } catch (DataIntegrityViolationException ex) {
                log.warn(ex.getMessage());
                throw new TxException("Cannot save account");
            }
            return account;
        }

        // Update
        AccountEntity account = accountDao.findById(accountEntity.getId());

        if (account != null) {

            if (accountDao.getByEmail(accountEntity.getEmail()) != null && !account.getEmail().equals(accountEntity.getEmail())) {
                throw new ConflictException("Account with [" + accountEntity.getEmail() + "]  already exists");
            }
            if (departmentDao.findById(accountEntity.getDepartment().getId()) == null) {
                throw new NotFoundException("Department not found");
            }

            if (accountEntity.getPassword() != null && !accountEntity.getPassword().isEmpty()) {
                account.setPassword(bCryptPasswordEncoder.encode(accountEntity.getPassword()));
            }
            if (accountEntity.getLastName() != null && !accountEntity.getLastName().isEmpty()) {
                account.setLastName(accountEntity.getLastName());
            }
            if (accountEntity.getFirstName() != null && !accountEntity.getFirstName().isEmpty()) {
                account.setFirstName(accountEntity.getFirstName());
            }
            if (accountEntity.getEmail() != null && !accountEntity.getEmail().isEmpty()) {
                account.setEmail(accountEntity.getEmail());
            }
            if (accountEntity.getPhoneNumber() != null && !accountEntity.getPhoneNumber().isEmpty()) {
                account.setPhoneNumber(accountEntity.getPhoneNumber());
            }

            account.setDepartment(accountEntity.getDepartment());
            account.setUpdateAt(DateTime.now().toDate());

            try {
                return accountDao.save(account);
            } catch (DataIntegrityViolationException ex) {
                log.warn(ex.getMessage());
                throw new TxException("Cannot save account");
            }
        } else {
            throw new NotFoundException("Account not found");
        }

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

    public AccountEntity getCurrentUser(){
        // Lấy thông tin người dùng gửi request thông qua token, mà lớp filter đã thực hiện qua lưu vào Security context holder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        AccountEntity account;
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomUserDetails userDetails) {
                account = getByUsername(userDetails.getUsername());
            } else {
                account = null;
            }
        } else {
            account = null;
        }
        return account;








    }
}
