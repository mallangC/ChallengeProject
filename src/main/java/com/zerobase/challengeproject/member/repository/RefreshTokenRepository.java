package com.zerobase.challengeproject.member.repository;

import com.zerobase.challengeproject.member.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    /**
     * 토큰 값을 기준으로 리프레시 토큰을 조회.
     * @param token 찾을 토큰 값
     * @return 해당 토큰을 포함하는 Optional<RefreshToken>
     */
    Optional<RefreshToken> findByToken(String token);
    /**
     * 특정 회원 ID와 연관된 리프레시 토큰을 삭제.
     * @param memberId 삭제할 회원 ID
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM RefreshToken r WHERE r.memberId = :memberId")
    void deleteByMemberId(@Param("memberId") String memberId);
    /**
     * 회원 ID를 기준으로 리프레시 토큰을 조회.
     * @param memberId 찾을 회원 ID
     * @return 해당 회원 ID와 연결된 Optional<RefreshToken>
     */
    Optional<RefreshToken> findByMemberId(String memberId);
}
