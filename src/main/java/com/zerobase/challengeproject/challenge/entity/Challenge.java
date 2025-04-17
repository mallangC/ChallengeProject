package com.zerobase.challengeproject.challenge.entity;

import com.zerobase.challengeproject.challenge.domain.request.CreateChallengeRequest;
import com.zerobase.challengeproject.challenge.domain.request.UpdateChallengeRequest;
import com.zerobase.challengeproject.comment.entity.CoteChallenge;
import com.zerobase.challengeproject.comment.entity.DietChallenge;
import com.zerobase.challengeproject.comment.entity.WaterChallenge;
import com.zerobase.challengeproject.member.entity.Member;
import com.zerobase.challengeproject.type.CategoryType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Challenge {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  @OneToMany(mappedBy = "challenge", cascade = CascadeType.REMOVE, orphanRemoval = true)
  private List<MemberChallenge> memberChallenges;

  @Column(nullable = false)
  private String title;

  private String imageUrl;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @NotNull
  private CategoryType categoryType;

  @Column(nullable = false)
  private Long maxParticipant;

  @Column
  @Builder.Default
  private Long currentParticipant = 0L;

  @Column(nullable = false)
  private String description;

  @Column(nullable = false)
  private Long minDeposit;

  @Column(nullable = false)
  private Long maxDeposit;

  @Column(nullable = false)
  private String standard;

  @Column(nullable = false)
  private LocalDateTime startDate;

  @Column(nullable = false)
  private LocalDateTime endDate;

  @OneToMany(mappedBy = "challenge", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
  private List<CoteChallenge> coteChallenges;
  @OneToMany(mappedBy = "challenge", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
  private List<WaterChallenge> waterChallenges;
  @OneToMany(mappedBy = "challenge", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
  private List<DietChallenge> dietChallenges;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  public Challenge(CreateChallengeRequest form, Member member) {
    this.title = form.getTitle();
    this.member = member;
    this.categoryType = form.getCategoryType();
    this.imageUrl = form.getImg();
    this.maxParticipant = form.getMaxParticipant();
    this.currentParticipant = 1L;
    this.maxDeposit = form.getMaxDeposit();
    this.minDeposit = form.getMinDeposit();
    this.description = form.getDescription();
    this.startDate = form.getStartDate();
    this.endDate = form.getEndDate();
    this.createdAt = LocalDateTime.now();
  }

  public void update(UpdateChallengeRequest form) {

    if (form.getTitle() != null) this.setTitle(form.getTitle());
    if (form.getCategoryType() != null) this.setCategoryType(form.getCategoryType());
    if (form.getStandard() != null) this.setStandard(form.getStandard());
    if (form.getImg() != null) this.setImageUrl(form.getImg());
    if (form.getMaxParticipant() != null) this.setMaxParticipant(form.getMaxParticipant());
    if (form.getDescription() != null) this.setDescription(form.getDescription());
    if (form.getMinDeposit() != null) this.setMinDeposit(form.getMinDeposit());
    if (form.getMaxDeposit() != null) this.setMaxDeposit(form.getMaxDeposit());
    if (form.getStartDate() != null) this.setStartDate(form.getStartDate());
    if (form.getEndDate() != null) this.setEndDate(form.getEndDate());
    this.updatedAt = LocalDateTime.now();
  }

  public void registration() {
    this.setCurrentParticipant(this.getCurrentParticipant() + 1);
  }
}
