package com.zerobase.challengeproject.member.repository;

import com.zerobase.challengeproject.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    /**
     * 회원 가입시 이메일 중복을 막기 위한 메서드
     * @param email  회원 가입을 요청한 유저의 이메일
     * @return 동일 이메일 주소 유무
     */
    boolean existsByEmail(String email);

    /**
     * 회원 가입시 아이디 중복을 막기 위한 메서드
     * @param memberId 회원 가입을 요청한 유저의 아이디
     * @return 동일 아이디 존재 유무
     */
    boolean existsByMemberId(String memberId);
    /**
     * 이메일 인증시 유저를 찾기 위한 메서드
     * @param emailAuthKey 회원 가입시 생성된 이메일 인증 키
     * @return 해당 이메일 인증 키를 가지고 있는 유저
     */
    Optional<Member> findByEmailAuthKey(String emailAuthKey);

    /**
     * 유저 아이디로 유저 객체를 찾기 위한 메서드
     * @param memberId 유저 아이디
     * @return 유저 객체
     */
    Optional<Member> findByMemberId(String memberId);


}
