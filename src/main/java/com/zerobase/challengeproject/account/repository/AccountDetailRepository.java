package com.zerobase.challengeproject.account.repository;

import com.zerobase.challengeproject.account.entity.AccountDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountDetailRepository extends JpaRepository<AccountDetail, Long>, AccountDetailRepositoryCustom {
}
