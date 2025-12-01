package txu.admin.mainapp.api;

import io.minio.MinioClient;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
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



}
