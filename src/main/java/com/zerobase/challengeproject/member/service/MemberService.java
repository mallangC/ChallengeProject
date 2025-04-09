package com.zerobase.challengeproject.member.service;

import com.zerobase.challengeproject.exception.CustomException;
import com.zerobase.challengeproject.exception.ErrorCode;
import com.zerobase.challengeproject.member.components.jwt.UserDetailsImpl;
import com.zerobase.challengeproject.member.domain.dto.MemberProfileDto;
import com.zerobase.challengeproject.member.domain.form.ChangePasswordForm;
import com.zerobase.challengeproject.member.domain.form.MemberProfileFrom;
import com.zerobase.challengeproject.member.entity.Member;
import com.zerobase.challengeproject.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    /**
     * 유저가 회원 정보를 조회할 때 사용되는 서비스 메서드
     * @param userDetails 로그인한 유저의 정보
     * @return 유저 정보(로그인아이디, 이름, 닉네임, 전화번호, 이메일주소)
     */
    public MemberProfileDto getProfile(UserDetailsImpl userDetails) {
        Member member = findMemberByMemberId(userDetails.getUsername());
        return new MemberProfileDto(member);
    }

    /**
     * 유저의 정보를 수정할 떄 사용되는 서비스 메서드
     * @param userDetails 로그인한 유저의 정보
     * @param form 수정 정보 form(nickname, phoneNum)
     * @return 수정한 유저의 정보
     */
    @Transactional
    public MemberProfileDto updateProfile(UserDetailsImpl userDetails, MemberProfileFrom form) {
        Member member = findMemberByMemberId(userDetails.getUsername());
        member.updateProfile(form.getPhoneNum(),form.getNickname());
        return new MemberProfileDto(member);
    }

    /**
     * 유저의 비밀번호를 변경시 사용되는 서비스 메서드
     * 유저가 입력한 비밀번호가 테이블에 저장된 비밀번호가 다를 시 예외 발생
     * 새비밀번호와 새 비밀번호 확인이 다를시 예외 발생
     * 새 비밀번호와 테이블에 저장된 비밀번호가 같을 시 예외발생
     *
     * @param userDetails 로그인한 유저의 정보
     * @param form 비밀번호, 새비밀번호, 새비밀번호확인
     * @return 비밀번호를 변경한 유저의 아이디
     */
    @Transactional
    public String changePassword(UserDetailsImpl userDetails, ChangePasswordForm form) {
        Member member = findMemberByMemberId(userDetails.getUsername());
        if(!passwordEncoder.matches(form.getPassword(), member.getPassword())) {
            throw new CustomException(ErrorCode.INCORRECT_PASSWORD);
        }
        if(!form.getNewPassword().equals(form.getNewPasswordVerify())) {
            throw new CustomException(ErrorCode.INCORRECT_PASSWORD);
        }
        if(passwordEncoder.matches(form.getNewPassword(), member.getPassword())) {
            throw new CustomException(ErrorCode.MATCHES_PREVIOUS_PASSWORD);
        }
        String password = passwordEncoder.encode(form.getNewPassword());
        member.changePassword(password);
        return member.getMemberId();
    }

    /**
     * userDetails에 있는 유저의 정보로 DB에서 유저 객체를 가져오고 없으면 예외 발생
     * @param memberId 유저의 로그인 아이디
     * @return 유저 객체
     */
    private Member findMemberByMemberId(String memberId) {
        return memberRepository.findByMemberId(memberId)
                .orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND_MEMBER));
    }
}
