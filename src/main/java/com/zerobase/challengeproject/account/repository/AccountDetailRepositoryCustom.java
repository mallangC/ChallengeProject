package com.zerobase.challengeproject.account.repository;

import com.zerobase.challengeproject.account.entity.AccountDetail;
import org.springframework.data.domain.Page;

public interface AccountDetailRepositoryCustom {
  Page<AccountDetail> searchAllAccountDetail(int page, String loginId);
}
