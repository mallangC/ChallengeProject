package com.zerobase.challengeproject.comment.entity;

import com.zerobase.challengeproject.account.entity.BaseEntity;
import com.zerobase.challengeproject.comment.domain.request.WaterCommentAddRequest;
import com.zerobase.challengeproject.comment.domain.request.WaterCommentUpdateRequest;
import com.zerobase.challengeproject.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaterComment extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne
  @JoinColumn(name = "water_challenge_id", nullable = false)
  private WaterChallenge waterChallenge;
  @ManyToOne
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;
  @Column(nullable = false)
  private Integer drinkingIntake;
  @Column(nullable = false)
  private String imageUrl;

  public static WaterComment from(WaterCommentAddRequest form,
                                  WaterChallenge waterChallenge,
                                  Member member) {
    return WaterComment.builder()
            .waterChallenge(waterChallenge)
            .member(member)
            .drinkingIntake(form.getDrinkingIntake())
            .imageUrl(form.getImageUrl())
            .build();
  }

  public void update(WaterCommentUpdateRequest form) {
    this.waterChallenge.updateCurrentIntake(form.getDrinkingIntake() - this.drinkingIntake);
    this.drinkingIntake = form.getDrinkingIntake();
    this.imageUrl = form.getImageUrl();
  }
}
