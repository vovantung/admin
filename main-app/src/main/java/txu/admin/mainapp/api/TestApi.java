package txu.admin.mainapp.api;

import io.minio.MinioClient;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import txu.admin.mainapp.entity.WeeklyReportEntity;
import txu.admin.mainapp.service.RoleService;
import txu.admin.mainapp.service.TestService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@CrossOrigin(origins = "*", allowCredentials = "false", maxAge = 86400, allowedHeaders = "*")
@RestController
@RequiredArgsConstructor
public class TestApi {


    private final TestService testService;

    @PostMapping(value = "/test")
    public String test() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        testService.test();
        return "test";
    }

    @PostMapping("/test1")
    public void upload(@RequestParam("file") MultipartFile file) {
        try {
               testService.test1(file);
        } catch (Exception e) {

        }
    }


}
