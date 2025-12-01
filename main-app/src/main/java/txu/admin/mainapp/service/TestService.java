package txu.admin.mainapp.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

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
}
