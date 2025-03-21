package org.example.expert.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    // 파일 업로드
    public void uploadFileToS3(String s3Path, File file) {  // s3Path: S3에 저장될 때 사용할 "경로 + 파일명"
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Path)    // S3에 업로드될 파일의 경로(키)
                .acl(ObjectCannedACL.BUCKET_OWNER_FULL_CONTROL) // // 업로드된 객체의 권한을 버킷 소유자에게 완전한 제어 권한으로 설정
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromFile(file));
    }

    // 파일 다운로드 (로컬에 저장)
    public void downloadFile(String s3Path, String localFilePath) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Path)
                .build();

        s3Client.getObject(getObjectRequest, ResponseTransformer.toFile(Paths.get(localFilePath)));
    }

    // 파일 삭제
    public void deleteFile(String s3Path) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Path)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }
}

