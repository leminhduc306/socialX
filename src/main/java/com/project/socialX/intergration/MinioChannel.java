package com.project.socialX.intergration;

import com.project.socialX.web.rest.errors.BusinessException;
import io.minio.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class MinioChannel {

    private static final String BUCKET = "resources";
    private final MinioClient minioClient;

    @Value("${integration.minio.url}")
    private String url;

    @PostConstruct
    private void init() {
        createBucket(BUCKET);
    }

    private void createBucket(final String name) {
        try {
            final var found = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(name)
                            .build());

            if (!found) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(name)
                                .build());

                // Thiết lập bucket là public bằng cách set policy
                final var policy = """
                            {
                              "Version": "2012-10-17",
                              "Statement": [
                               {
                                  "Effect": "Allow",
                                  "Principal": "*",
                                  "Action": "s3:GetObject",
                                  "Resource": "arn:aws:s3:::%s/*"
                                }
                              ]
                            }
                        """.formatted(name);
                minioClient.setBucketPolicy(
                        SetBucketPolicyArgs.builder().bucket(name).config(policy).build());

                log.info("Đã tạo mới và thiết lập quyền public cho bucket: {}", name);
            } else {
                log.info("Bucket {} đã tồn tại.", name);
            }

        } catch (Exception ex) {
            log.error("Lỗi khi kiểm tra hoặc tạo bucket MinIO \n {}", ex.getMessage());
            throw new BusinessException("500", "Không thể khởi tạo bucket: " + name, ex);
        }
    }

    public String upload(@NonNull final MultipartFile file) {
        log.info("Bucket: {}, file size: {}", BUCKET, file.getSize());
        final var fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(BUCKET)
                            .object(fileName)
                            .contentType(Objects.isNull(file.getContentType()) ? "image/png; image/jpg"
                                    : file.getContentType())
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .build());

            // Vì bucket đã là public, ta trả về link trực tiếp không hết hạn
            return String.format("%s/%s/%s", url, BUCKET, fileName);

        } catch (Exception ex) {
            log.error("Lỗi khi lưu file lên MinIO \n {} ", ex.getMessage());
            throw new BusinessException("400", "Unable to upload file", ex);
        }
    }

    public byte[] download(String bucket, String name) {
        try (GetObjectResponse inputStream = minioClient.getObject(GetObjectArgs.builder()
                .bucket(bucket)
                .object(name)
                .build())) {

            return inputStream.readAllBytes();

        } catch (Exception e) {
            log.error("Lỗi khi tải file từ MinIO \n {} ", e.getMessage());
            throw new BusinessException("400", "Unable to download file", e);
        }
    }

    public InputStream getFileStream(String bucket, String name) {
        try {
            // Không dùng try-with-resources ở đây, vì luồng này phải mở để truyền thẳng về
            // Frontend
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(name)
                    .build());
        } catch (Exception e) {
            log.error("Lỗi khi mở luồng tải file từ MinIO \n {} ", e.getMessage());
            throw new BusinessException("400", "Unable to stream file", e);
        }
    }
}
