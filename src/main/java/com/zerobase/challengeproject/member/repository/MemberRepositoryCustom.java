package com.zerobase.challengeproject.member.repository;

import com.zerobase.challengeproject.member.entity.Member;

import java.time.LocalDateTime;
import java.util.Optional;

public interface MemberRepositoryCustom {
  Optional<Member> searchByLoginIdAndAccountDetailsToDate(String loginId, LocalDateTime searchByDate);

  Optional<Member> searchByLoginIdAndAccountDetailId(String loginId, Long accountId);

}
