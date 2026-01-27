package txu.admin.mainapp.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import txu.admin.mainapp.base.AbstractApi;
import txu.admin.mainapp.dto.LimitRequest;
import txu.admin.mainapp.dto.UsernameRequest;
import txu.admin.mainapp.entity.AccountEntity;
import txu.admin.mainapp.service.AccountService;


import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/account")
@RequiredArgsConstructor
public class AccountApi extends AbstractApi {

    private final AccountService accountService;

//    @PostMapping("/update-avatar")
//    public AccountEntity updateAvatar(
//            @RequestPart(value = "file", required = false) MultipartFile file, // ✅ optional
//            @RequestPart("username") String username,
//            @RequestPart("password") String password,
//            @RequestPart("firstName") String firstName,
//            @RequestPart("lastName") String lastName,
//            @RequestPart("email") String email,
//            @RequestPart("phoneNumber") String phoneNumber
//
//    ) throws  IOException, NoSuchAlgorithmException, InvalidKeyException{
//        return accountService.updateAvatar(file, username, password, firstName, lastName, email, phoneNumber);
//    }

    @PostMapping(value = "create-or-update")
    public AccountEntity createOrUpdate(@RequestBody AccountEntity accountEntity) {
        return accountService.createOrUpdate(accountEntity);
    }

    @PostMapping(value = "get-limit")
    public List<AccountEntity> getLimit(@RequestBody LimitRequest request) {
        return accountService.getWithLimit(request.getLimit());
    }

    @DeleteMapping(value = "remove")
    public boolean removeByUsername(@RequestBody UsernameRequest request) {
        return accountService.removeByUsername(request.getUsername());
    }

    @PostMapping(value = "get-by-username")
    public AccountEntity getByUsername(@RequestBody UsernameRequest request) {
        return accountService.getByUsername(request.getUsername());
    }

//    @GetMapping(value = "get-current-user")
//    public AccountEntity getCurrentUser() {
//        return accountService.getCurrentUser();
//    }

}
