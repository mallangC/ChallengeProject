package com.zerobase.challengeproject.comment.controller;

import com.zerobase.challengeproject.comment.service.S3Service;
import lombok.RequiredArgsConstructor;
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
  public ResponseEntity<String> uploadImage(MultipartFile file) throws IOException {
    return ResponseEntity.ok(s3Service.uploadFile(file));
  }

  @DeleteMapping
  public ResponseEntity<String> deleteImage(String imagePath) {
    s3Service.deleteFile(imagePath);
    return ResponseEntity.ok("삭제 완료");
  }
}
