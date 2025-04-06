package com.zerobase.challengeproject.comment.entity;

import com.zerobase.challengeproject.account.entity.BaseEntity;
import com.zerobase.challengeproject.comment.domain.form.DietCommentAddForm;
import com.zerobase.challengeproject.comment.domain.form.DietCommentUpdateForm;
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
  private String image;
  @Column(nullable = false)
  private String content;

  public static DietComment from(DietCommentAddForm form, DietChallenge dietChallenge, Member member) {
    return DietComment.builder()
            .dietChallenge(dietChallenge)
            .member(member)
            .image(form.getImage())
            .content(form.getContent())
            .build();
  }

  public void update(DietCommentUpdateForm form) {
    this.image = form.getImage();
    this.content = form.getContent();
  }

}
