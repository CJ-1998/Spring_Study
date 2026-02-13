package com.study.SpringStudy.common.image.service;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.SetBucketPolicyArgs;
import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    @Value("${app.storage.url}")
    private String storageUrl;

    @PostConstruct
    public void init() {
        ensureBucketExists();
    }

    /**
     * 이미지 업로드 메인 메서드
     */
    public String uploadImage(MultipartFile file) {
        // 1. 버킷 준비 (존재 확인 및 생성/정책 설정)
        // ensureBucketExists();

        // 2. 고유 파일명 생성
        String saveFilename = generateUniqueFilename(file.getOriginalFilename());

        // 3. MinIO에 파일 업로드
        uploadToMinio(file, saveFilename);

        // 4. 접근 URL 반환
        return getFileUrl(saveFilename);
    }

    /**
     * 버킷이 존재하는지 확인하고, 없으면 생성 후 Public 권한을 부여합니다.
     */
    private void ensureBucketExists() {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (!found) {
                log.info("MinIO Bucket '{}' not found. Creating...", bucket);
                createBucket();
                setPublicReadPolicy();
                log.info("MinIO Bucket '{}' created with Public Read policy.", bucket);
            }
        } catch (Exception e) {
            log.error("Bucket checking/creation failed", e);
            throw new RuntimeException("MinIO 버킷 확인/생성 중 오류 발생: " + e.getMessage());
        }
    }

    /**
     * 버킷 생성
     */
    private void createBucket() throws Exception {
        minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
    }

    /**
     * 버킷 정책 설정 (Public Read)
     */
    private void setPublicReadPolicy() throws Exception {
        String policyJson = getPublicReadPolicyJson(bucket);
        minioClient.setBucketPolicy(
                SetBucketPolicyArgs.builder()
                        .bucket(bucket)
                        .config(policyJson)
                        .build()
        );
    }

    /**
     * 실제 파일을 MinIO에 업로드합니다.
     */
    private void uploadToMinio(MultipartFile file, String filename) {
        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(filename)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        } catch (Exception e) {
            log.error("Image upload failed", e);
            throw new RuntimeException("이미지 업로드에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * UUID를 포함한 고유 파일명 생성
     */
    private String generateUniqueFilename(String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return UUID.randomUUID() + extension;
    }

    /**
     * 최종 이미지 URL 생성
     */
    private String getFileUrl(String filename) {
        return String.format("%s/%s/%s", storageUrl, bucket, filename);
    }

    /**
     * MinIO Public Read 정책 JSON 생성 (Text Block 사용)
     */
    private String getPublicReadPolicyJson(String bucketName) {
        return """
                {
                    "Version": "2012-10-17",
                    "Statement": [
                        {
                            "Effect": "Allow",
                            "Principal": { "AWS": ["*"] },
                            "Action": ["s3:GetObject"],
                            "Resource": ["arn:aws:s3:::%s/*"]
                        }
                    ]
                }
                """.formatted(bucketName);
    }
}