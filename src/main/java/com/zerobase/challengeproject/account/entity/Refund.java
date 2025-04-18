package com.zerobase.challengeproject.account.entity;

import com.zerobase.challengeproject.account.domain.request.RefundUpdateRequest;
import com.zerobase.challengeproject.exception.CustomException;
import com.zerobase.challengeproject.exception.ErrorCode;
import com.zerobase.challengeproject.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Refund extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @OneToOne
  @JoinColumn(name = "account_detail_id")
  private AccountDetail accountDetail;
  @ManyToOne
  @JoinColumn(name = "member_id")
  private Member member;
  private String memberContent;
  private String adminContent;
  private boolean isRefunded;
  private boolean isDone;

  public static Refund from(String content,
                            Member member
  ) {
    return Refund.builder()
            .accountDetail(member.getAccountDetails().get(0))
            .member(member)
            .memberContent(content)
            .build();
  }

  public void rejectRefund(RefundUpdateRequest form) {
    if (isDone()) {
      throw new CustomException(ErrorCode.ALREADY_DONE);
    }
    this.adminContent = form.getContent();
    this.isRefunded = false;
    this.isDone = true;
  }

  public void approveRefund() {
    if (isDone() || isRefunded()) {
      throw new CustomException(ErrorCode.ALREADY_DONE);
    }
    this.adminContent = "환불 완료";
    this.isRefunded = true;
    this.isDone = true;
  }
}
