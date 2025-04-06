package com.zerobase.challengeproject.challenge.entity;

import com.zerobase.challengeproject.challenge.domain.form.CreateChallengeForm;
import com.zerobase.challengeproject.challenge.domain.form.UpdateChallengeForm;
import com.zerobase.challengeproject.comment.entity.CoteChallenge;
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

    @OneToMany(mappedBy = "challenge")
    private List<MemberChallenge> memberChallenges;

    @Column(nullable = false)
    private String title;

    private String img;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private CategoryType categoryType;

    @Column(nullable = false)
    private Long maxParticipant;

    @Column
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
    private List<CoteChallenge> coteChallenge;
//    @OneToMany(mappedBy = "callenge", fetch = FetchType.LAZY)
//    List<DrinkingComment> drinkingComments;
//    @OneToMany(mappedBy = "callenge", fetch = FetchType.LAZY)
//    List<DietComment> dietComments;

    private LocalDateTime createAt;
    
    private LocalDateTime updateAt;

    public Challenge(CreateChallengeForm form, Member member) {
        this.currentParticipant = 1L;
        this.title = form.getTitle();
        this.member = member;
        this.categoryType = form.getCategoryType();
        this.img = form.getImg();
        this.maxParticipant = form.getMaxParticipant();
        this.maxDeposit = form.getMaxDeposit();
        this.standard = form.getStandard();
        this.minDeposit = form.getMinDeposit();
        this.description = form.getDescription();
        this.startDate = form.getStartDate();
        this.endDate = form.getEndDate();
        this.createAt = LocalDateTime.now();
    }

    public void update(UpdateChallengeForm form) {

        if (form.getTitle() != null) this.setTitle(form.getTitle());
        if (form.getCategoryType() != null) this.setCategoryType(form.getCategoryType());
        if (form.getStandard() != null) this.setStandard(form.getStandard());
        if (form.getImg() != null) this.setImg(form.getImg());
        if (form.getMaxParticipant() != null) this.setMaxParticipant(form.getMaxParticipant());
        if (form.getDescription() != null) this.setDescription(form.getDescription());
        if (form.getMinDeposit() != null) this.setMinDeposit(form.getMinDeposit());
        if (form.getMaxDeposit() != null) this.setMaxDeposit(form.getMaxDeposit());
        if (form.getStartDate() != null) this.setStartDate(form.getStartDate());
        if (form.getEndDate() != null) this.setEndDate(form.getEndDate());
        this.updateAt = LocalDateTime.now();
    }
    public void registration(){
        this.setCurrentParticipant(this.getCurrentParticipant() + 1);
    }
}
