package com.zerobase.challengeproject.comment.entity;

import com.zerobase.challengeproject.account.entity.BaseEntity;
import com.zerobase.challengeproject.comment.domain.form.WaterCommentAddForm;
import com.zerobase.challengeproject.comment.domain.form.WaterCommentUpdateForm;
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
  private Integer drinkingMl;
  @Column(nullable = false)
  private String image;

  public static WaterComment from(WaterCommentAddForm form,
                                  WaterChallenge waterChallenge,
                                  Member member) {
    return WaterComment.builder()
            .waterChallenge(waterChallenge)
            .member(member)
            .drinkingMl(form.getDrinkingMl())
            .image(form.getImage())
            .build();
  }

  public void update(WaterCommentUpdateForm form) {
    this.waterChallenge.updateCurrentMl(form.getDrinkingMl() - this.drinkingMl);
    this.drinkingMl = form.getDrinkingMl();
    this.image = form.getImage();
  }
}
