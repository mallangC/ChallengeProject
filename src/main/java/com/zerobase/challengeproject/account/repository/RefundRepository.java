package com.zerobase.challengeproject.account.repository;

import com.zerobase.challengeproject.account.entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefundRepository extends JpaRepository<Refund, Long> , RefundRepositoryCustom{
  boolean existsByAccountDetail_Id(Long accountDetailId);
}
