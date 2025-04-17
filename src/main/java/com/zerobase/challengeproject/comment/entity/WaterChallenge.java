package com.zerobase.challengeproject.comment.entity;

import com.zerobase.challengeproject.account.entity.BaseEntity;
import com.zerobase.challengeproject.challenge.entity.Challenge;
import com.zerobase.challengeproject.comment.domain.request.WaterChallengeRequest;
import com.zerobase.challengeproject.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaterChallenge extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;
  @ManyToOne
  @JoinColumn(name = "challenge_id", nullable = false)
  private Challenge challenge;
  @Column(nullable = false)
  private Integer goalIntake;
  @Column(nullable = false)
  private Integer currentIntake;
  @OneToMany(mappedBy = "waterChallenge", fetch = FetchType.LAZY)
  private List<WaterComment> comments;

  public static WaterChallenge fromForm(WaterChallengeRequest form, Challenge challenge, Member member) {
    return WaterChallenge.builder()
            .member(member)
            .challenge(challenge)
            .goalIntake(form.getGoalIntake())
            .currentIntake(0)
            .build();
  }

  public static WaterChallenge fromWaterChallenge(WaterChallenge waterChallenge) {
    return WaterChallenge.builder()
            .member(waterChallenge.getMember())
            .challenge(waterChallenge.getChallenge())
            .goalIntake(waterChallenge.getGoalIntake())
            .currentIntake(0)
            .build();
  }

  public void updateGoalIntake(Integer goalIntake) {
    if (goalIntake == null) {
      throw new IllegalArgumentException("목표 섭취량을 입력해주세요.");
    } else if (goalIntake < 1000 || goalIntake > 2000) {
      throw new IllegalArgumentException("목표 섭취량은 1000ml이상, 2000ml이하로 입력해주세요.");
    }
    this.goalIntake = goalIntake;
  }

  public void updateCurrentIntake(Integer currentIntake) {
    if (currentIntake == null) {
      throw new IllegalArgumentException("현재 섭취량을 입력해주세요.");
    }
    this.currentIntake += currentIntake;
  }
}
