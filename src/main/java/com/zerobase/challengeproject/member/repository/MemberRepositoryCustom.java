package com.zerobase.challengeproject.member.repository;

import com.zerobase.challengeproject.member.entity.Member;

import java.time.LocalDateTime;

public interface MemberRepositoryCustom {
  Member searchByLoginIdAndAccountDetailsToDate(String loginId, LocalDateTime searchByDate);

  Member searchByLoginIdAndAccountDetailId(String loginId, Long accountId);

  Member searchByLoginId(String loginId);
}
