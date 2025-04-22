package com.zerobase.challengeproject.comment.entity;

import com.zerobase.challengeproject.account.entity.BaseEntity;
import com.zerobase.challengeproject.comment.domain.request.DietCommentAddRequest;
import com.zerobase.challengeproject.comment.domain.request.DietCommentUpdateRequest;
import com.zerobase.challengeproject.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DietComment extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne
  @JoinColumn(name = "diet_challenge_id", nullable = false)
  private DietChallenge dietChallenge;
  @ManyToOne
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;
  @Column(nullable = false)
  private String imageUrl;
  @Column(nullable = false)
  private String content;
  @Column(nullable = false)
  private Float currentWeight;
  @Column(nullable = false)
  private LocalDateTime createdAt;

  public static DietComment from(DietCommentAddRequest form, DietChallenge dietChallenge, Member member) {
    return DietComment.builder()
            .dietChallenge(dietChallenge)
            .member(member)
            .currentWeight(form.getCurrentWeight())
            .imageUrl(form.getImageUrl())
            .content(form.getContent())
            .createdAt(LocalDateTime.now())
            .build();
  }

  public void update(DietCommentUpdateRequest form) {
    this.imageUrl = form.getImageUrl();
    this.content = form.getContent();
  }

  public void updateImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

}
