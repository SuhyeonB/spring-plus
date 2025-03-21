package org.example.expert.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.service.ProfileImageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users/image")
@RequiredArgsConstructor
public class ProfileImageController {
    private final ProfileImageService profileImageService;

    @PostMapping
    public ResponseEntity<String> uploadProfileImage(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestPart("file")MultipartFile file
    ) {
        String imageUrl = profileImageService.upload(authUser.getId(), file);
        return ResponseEntity.ok(imageUrl);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteProfileImage(@AuthenticationPrincipal AuthUser authUser) {
        profileImageService.delete(authUser.getId());
        return ResponseEntity.noContent().build();
    }
}
