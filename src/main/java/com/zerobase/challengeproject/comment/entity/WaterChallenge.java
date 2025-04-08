package com.zerobase.challengeproject.comment.entity;

import com.zerobase.challengeproject.account.entity.BaseEntity;
import com.zerobase.challengeproject.challenge.entity.Challenge;
import com.zerobase.challengeproject.comment.domain.form.WaterChallengeAddForm;
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
  private Integer goalMl;
  @Column(nullable = false)
  private Integer currentMl;
  @OneToMany(mappedBy = "waterChallenge", fetch = FetchType.LAZY)
  private List<WaterComment> comments;

  public static WaterChallenge from(WaterChallengeAddForm form, Challenge challenge, Member member) {
    return WaterChallenge.builder()
            .member(member)
            .challenge(challenge)
            .goalMl(form.getGoalMl())
            .currentMl(0)
            .build();
  }
}
