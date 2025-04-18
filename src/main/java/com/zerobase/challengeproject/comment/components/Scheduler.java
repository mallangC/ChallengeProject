package com.zerobase.challengeproject.comment.components;

import com.zerobase.challengeproject.comment.service.WaterChallengeService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Scheduler {

  private final WaterChallengeService waterChallengeService;

  @Scheduled(cron = "0 0 0 * * *")
  public void addAllWaterChallenge(){
    waterChallengeService.addAllWaterChallenge();
  }
}
