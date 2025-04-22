package com.zerobase.challengeproject.comment.controller;

import com.zerobase.challengeproject.HttpApiResponse;
import com.zerobase.challengeproject.comment.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/challenge/image")
public class S3Controller {
  private final S3Service s3Service;

  @PostMapping
  public ResponseEntity<HttpApiResponse<String>> uploadImage(MultipartFile imageFile) throws IOException {
    return ResponseEntity.ok(new HttpApiResponse<>(
            s3Service.uploadFile(imageFile),
            "이미지 저장 성공",
            HttpStatus.OK));
  }

  @DeleteMapping
  public ResponseEntity<HttpApiResponse<String>> deleteImage(String imageUrl) {
    s3Service.deleteFile(imageUrl);
    String fileName = imageUrl.substring(38);
    return ResponseEntity.ok(new HttpApiResponse<>(
            fileName,
            "파일 삭제 성공",
            HttpStatus.OK));
  }
}
