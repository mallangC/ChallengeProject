package com.zerobase.challengeproject.comment.entity;

import com.zerobase.challengeproject.account.entity.BaseEntity;
import com.zerobase.challengeproject.challenge.entity.Challenge;
import com.zerobase.challengeproject.comment.domain.request.DietChallengeRequest;
import com.zerobase.challengeproject.exception.CustomException;
import com.zerobase.challengeproject.exception.ErrorCode;
import com.zerobase.challengeproject.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DietChallenge extends BaseEntity {
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
  private Float goalWeight;
  @Column(nullable = false)
  private Float currentWeight;
  @OneToMany(mappedBy = "dietChallenge", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<DietComment> comments;

  public static DietChallenge from(DietChallengeRequest form, Member member, Challenge challenge) {
    if (form.getCurrentWeight() - form.getGoalWeight() < 5) {
      throw new CustomException(ErrorCode.DIFFERENCE_MORE_THEN_5KG);
    }
    return DietChallenge.builder()
            .challenge(challenge)
            .member(member)
            .goalWeight(form.getGoalWeight())
            .currentWeight(form.getCurrentWeight())
            .comments(new ArrayList<>())
            .build();
  }

  public void update(DietChallengeRequest form) {
    if (form.getCurrentWeight() - form.getGoalWeight() < 5) {
      throw new CustomException(ErrorCode.DIFFERENCE_MORE_THEN_5KG);
    }
    this.goalWeight = form.getGoalWeight();
    this.currentWeight = form.getCurrentWeight();
  }

  public void updateWeight(Float currentWeight) {
    this.currentWeight = currentWeight;
  }
}
