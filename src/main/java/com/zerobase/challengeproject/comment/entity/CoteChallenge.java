package com.zerobase.challengeproject.comment.entity;

import com.zerobase.challengeproject.challenge.entity.Challenge;
import com.zerobase.challengeproject.comment.domain.form.CoteChallengeForm;
import com.zerobase.challengeproject.comment.domain.form.CoteChallengeUpdateForm;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CoteChallenge {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne
  @JoinColumn(name = "challenge_id", nullable = false)
  private Challenge challenge;
  @Column(nullable = false)
  private String title;
  @Column(nullable = false)
  private String question;
  @Column(nullable = false)
  private LocalDateTime startAt;
  @OneToMany(mappedBy = "coteChallenge", fetch = FetchType.LAZY)
  private List<CoteComment> comments;

  public static CoteChallenge from(CoteChallengeForm form, Challenge challenge) {
    return CoteChallenge.builder()
            .challenge(challenge)
            .title(form.getTitle())
            .question(form.getQuestion())
            .startAt(form.getStartAt())
            .comments(new ArrayList<>())
            .build();
  }

  public void update(CoteChallengeUpdateForm form) {
    this.title = form.getTitle();
    this.question = form.getQuestion();
  }
}
