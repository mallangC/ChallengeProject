package com.zerobase.challengeproject.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {
  private final S3Client s3Client;
  //S3디렉토리 이름
  private final String keyPrefix = "images/";

  @Value("${aws.bucket}")
  private String bucketName;

  @Value("${aws.cloudfrontPath}")
  private String cloudfrontPath;

  public String uploadFile(MultipartFile file) throws IOException {
    InputStream inputStream = file.getInputStream();
    String contentType = file.getContentType();
    //파일 이름이 겹칠 수도 있으므로 새로 이름을 만듦
    String newFileName = UUID.randomUUID() + "-" + file.getOriginalFilename();

    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(keyPrefix + newFileName)
            .contentType(contentType)
            .build();

    s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, inputStream.available()));
    //S3에 Get요청은 클라우드 프론트를 통해서 받음
    return cloudfrontPath + newFileName;
  }

  public void deleteFile(String fileName) {
    //클라우드 프론트 주소를 제외하고 UUID+파일이름 가져오기
    String newFileName = fileName.substring(38);
    DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
            .bucket(bucketName)
            .key(keyPrefix + newFileName)
            .build();
    s3Client.deleteObject(deleteObjectRequest);
  }

}
