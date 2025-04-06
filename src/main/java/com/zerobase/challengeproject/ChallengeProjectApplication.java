package com.zerobase.challengeproject;

import io.jsonwebtoken.security.Keys;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.crypto.SecretKey;
import java.util.Base64;

@EnableJpaAuditing
@SpringBootApplication
public class ChallengeProjectApplication {

  public static void main(String[] args) {
    SpringApplication.run(ChallengeProjectApplication.class, args);

  }

}
