package org.example.expert.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.example.expert.s3.S3Service;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ProfileImageService {

    private final S3Service s3Service;
    private final UserRepository userRepository;

    private final String bucketName = "springplusbucket";

    public String upload(Long userId, MultipartFile file) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // S3 경로: profile-images/{userId}/{파일이름}
            String s3Path = "profile-images/" + userId + "/" + file.getOriginalFilename();

            // 업로드
            s3Service.uploadFileToS3(s3Path, (File) file);

            // S3 public URL 생성 및 DB 업데이트
            String s3Url = "https://" + bucketName + ".s3.ap-southeast-2.amazonaws.com/" + s3Path;
            user.updateProfileImage(s3Url);
            userRepository.save(user);

            return s3Url;
    }

    public void delete(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getProfileImage() != null) {
            // s3 key 추출
            String profileImageUrl = user.getProfileImage();
            String key = profileImageUrl.substring(profileImageUrl.indexOf(".com/") + 5);

            // S3에서 삭제
            s3Service.deleteFile(key);

            // DB에서 profileImage null 처리
            user.updateProfileImage(null);
            userRepository.save(user);
        }
    }
}


