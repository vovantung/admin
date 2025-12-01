package txu.admin.mainapp.service;

import io.minio.*;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import txu.admin.mainapp.entity.DepartmentEntity;
import txu.admin.mainapp.entity.WeeklyReportEntity;
import txu.admin.mainapp.security.CustomUserDetails;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static txu.admin.mainapp.common.DateUtil.*;

@Service
@RequiredArgsConstructor
public class TestService {
    private final MinioClient minioClient;
    @Value("${minio.bucket}")
    private String bucketName;

    @Value("${minio.url}")
    private String url;

    public void test() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String content = "Hello Ceph RGW via MinIO SDK!";
        byte[] data = content.getBytes(StandardCharsets.UTF_8);

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket("storage1")
                        .object("test-file.txt")
                        .contentType("text/plain")
                        .stream(
                                new java.io.ByteArrayInputStream(data),
                                data.length,
                                -1
                        )
                        .build()
        );
    }

    public void test1(MultipartFile file) throws Exception {

        // Upload to MinIO
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object("anh.jpg")
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build()
        );


    }
}
