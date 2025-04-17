package com.zerobase.challengeproject.comment.entity;

import com.zerobase.challengeproject.account.entity.BaseEntity;
import com.zerobase.challengeproject.comment.domain.request.CoteCommentRequest;
import com.zerobase.challengeproject.comment.domain.request.CoteCommentUpdateRequest;
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
@AllArgsConstructor
@NoArgsConstructor
public class CoteComment extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne
  @JoinColumn(name = "member_id")
  private Member member;
  @ManyToOne
  @JoinColumn(name = "cote_challenge_id")
  private CoteChallenge coteChallenge;
  @Column(nullable = false)
  private String imageUrl;
  @Column(nullable = false)
  private String content;
  private LocalDateTime createAt;

  public static CoteComment from(CoteCommentRequest form,
                                 Member member,
                                 CoteChallenge coteChallenge) {
    return CoteComment.builder()
            .member(member)
            .coteChallenge(coteChallenge)
            .imageUrl(form.getImageUrl())
            .content(form.getContent())
            .createAt(LocalDateTime.now())
            .build();
  }

  public void update(CoteCommentUpdateRequest form) {
    this.imageUrl = form.getImageUrl();
    this.content = form.getContent();
  }
}
