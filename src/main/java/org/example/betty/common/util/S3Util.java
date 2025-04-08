package org.example.betty.common.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Utilities;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.net.URL;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Util {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String upload(byte[] imageBytes, String contentType, String gameCode, String teamCode, String inning) {
        String key = "display/" + gameCode + "/" + teamCode + "/" + inning + "/" + UUID.randomUUID() + ".png";

        try {
            PutObjectRequest req = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(req, RequestBody.fromBytes(imageBytes));
            S3Utilities s3Utilities = s3Client.utilities();
            URL url = s3Utilities.getUrl(builder -> builder.bucket(bucket).key(key));
            return url.toString();
        } catch (S3Exception e) {
            log.error("S3 upload failed for key={} message={}", key, e.getMessage());
            throw new IllegalStateException("Failed to upload image to S3", e);
        }
    }

}
