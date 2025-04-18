package com.zerobase.challengeproject.account.repository;

import com.zerobase.challengeproject.account.entity.Refund;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RefundRepositoryCustom {
  Page<Refund> searchAllRefund(int page, LocalDateTime startAt, Boolean isDone, Boolean isRefunded);

  Optional<Refund> searchRefundById(Long id);

  Page<Refund> searchAllMyRefund(int page, String userId);

  Optional<Refund> searchRefundByIdAndLoginId(Long refundId, String loginId);
}
