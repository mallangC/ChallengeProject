package com.zerobase.challengeproject.account.entity;

import com.zerobase.challengeproject.member.entity.Member;
import com.zerobase.challengeproject.type.AccountType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDetail extends BaseEntity {
  @Id
  @GeneratedValue(strategy= GenerationType.IDENTITY)
  private Long id;
  @ManyToOne
  @JoinColumn(name = "member_id")
  private Member member;
  @Column(nullable = false)
  private Long preAmount; //이전 금액
  @Column(nullable = false)
  private Long curAmount; //충전, 환불 이후 금액
  @Column(nullable = false)
  private Long amount; //충전, 환불한 금액
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private AccountType accountType; //충전 내역인지, 환불내역인지 구별
  private boolean isRefunded; //환불이 된 충전 내역인지 구별


  public static AccountDetail charge(Member member, Long amount){
    return AccountDetail.builder()
            .member(member)
            .preAmount(member.getAccount())
            .curAmount(member.getAccount() + amount)
            .amount(amount)
            .accountType(AccountType.CHARGE)
            .isRefunded(false)
            .build();
  }

  public static AccountDetail refund(Member member, Long amount){
    return AccountDetail.builder()
            .member(member)
            .preAmount(member.getAccount())
            .curAmount(member.getAccount() - amount)
            .amount(amount)
            .accountType(AccountType.REFUND)
            .isRefunded(false)
            .build();
  }

  public static AccountDetail deposit(Member member, Long amount){
    return AccountDetail.builder()
            .member(member)
            .preAmount(member.getAccount())
            .curAmount(member.getAccount() - amount)
            .amount(amount)
            .accountType(AccountType.DEPOSIT)
            .isRefunded(false)
            .build();
  }

  public static AccountDetail depositBack(Member member, Long amount){
    return AccountDetail.builder()
            .member(member)
            .preAmount(member.getAccount())
            .curAmount(member.getAccount() + amount)
            .amount(amount)
            .accountType(AccountType.DEPOSIT_BACK)
            .isRefunded(false)
            .build();
  }

  public void refundTrue(){
    this.isRefunded = true;
  }

}
