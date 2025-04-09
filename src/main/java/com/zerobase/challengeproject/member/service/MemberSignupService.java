package com.zerobase.challengeproject.member.service;

import com.zerobase.challengeproject.exception.CustomException;
import com.zerobase.challengeproject.exception.ErrorCode;
import com.zerobase.challengeproject.member.components.MailComponents;
import com.zerobase.challengeproject.member.components.jwt.JwtUtil;
import com.zerobase.challengeproject.member.components.jwt.UserDetailsImpl;
import com.zerobase.challengeproject.member.domain.dto.MemberEmailAuthDto;
import com.zerobase.challengeproject.member.domain.dto.MemberSignupDto;
import com.zerobase.challengeproject.member.domain.form.MemberSignupForm;
import com.zerobase.challengeproject.member.entity.Member;
import com.zerobase.challengeproject.member.repository.MemberRepository;
import com.zerobase.challengeproject.member.repository.RefreshTokenRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberSignupService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailComponents mailComponents;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    /**
     * 회원 가입시 사용되는 서비스 메서드, 비밀번호와 확인 비밀번호가 다르면 예외 발생.
     * 이메일 주소가 중복이면 예외발생. 인증 키는 랜덤으로 생성.
     * 비밀번호는 BCryptPasswordEncoder를 이용하여 인코딩하여 저장
     * @param memberSignupForm 회원 가입 정보들(memberId,memberName,nickname,
     *                         email, phoneNum, password, confirmPassword)
     * @return 회원 가입한 유저의 비밀번호를 제외한 정보
     */
    public MemberSignupDto signup(@Valid MemberSignupForm memberSignupForm) {
        if(!Objects.equals(memberSignupForm.getPassword(), memberSignupForm.getConfirmPassword())) {
            throw new CustomException(ErrorCode.CONFIRM_PASSWORD_MISMATCH);
        }
        String password = passwordEncoder.encode(memberSignupForm.getPassword());
        String memberId = memberSignupForm.getMemberId();
        String emailAuthKey = UUID.randomUUID().toString();
        String email = memberSignupForm.getEmail();
        if(memberRepository.existsByEmail(email)){
            throw new CustomException(ErrorCode.ALREADY_REGISTER_EMAIL);
        }
        if(memberRepository.existsByMemberId(memberId)){
            throw new CustomException(ErrorCode.ALREADY_REGISTER_LOGIN_ID);
        }
        Member member = Member.from(memberSignupForm, password, emailAuthKey);
        memberRepository.save(member);

        sendEmail(memberSignupForm.getEmail(), emailAuthKey);
        return new MemberSignupDto(member);
    }

    /**
     * 회원 가입시 이메일 인증 메일을 보내기 위한 서비스 메서드
     * @param email 회원 가입시 유저가 제공한 이메일
     * @param emailAuthKey 회원 가입시 랜덤으로 생성된 인증 키
     */
    private void sendEmail(@NotBlank @Email String email, String emailAuthKey) {
        String subject = "ZerobaseChallenge에 가입해 주셔서 감사합니다";
        String text = "<p>ZerobaseChallenge 사이트 가입을 축하드립니다.</p>" +
                "<p>아래 링크를 클릭하셔서 가입을 완료하세요.</p>"
                + "<div><a href='http://localhost:8080/api/member/email-auth?id=" + emailAuthKey +"'>클릭하여 이메일 인증 완료 하기</a></div>";
        mailComponents.send(email, subject, text);
    }

    /**
     * 유저가 회원 가입시 제공한 이메일에 있는 링크를 클릭하면 이메일 인증을 완료하는 메서드
     * @param emailAuthKey 회원 가입시 랜덤으로 생성된 인증 키
     * @return 유저의 아이디, 인증 확인, 인증 날짜
     */
    @Transactional
    public MemberEmailAuthDto verifyEmail(String emailAuthKey) {
        Optional<Member> memberOptional = memberRepository.findByEmailAuthKey(emailAuthKey);

        if(memberOptional.isEmpty()){
            throw new CustomException(ErrorCode.NOT_FOUND_MEMBER);
        }

        Member member = memberOptional.get();
        if(member.isEmailAuthYn()){
            throw new CustomException(ErrorCode.ALREADY_VERIFY_EMAIL);
        }

        member.completeEmailAuth();
        return  new MemberEmailAuthDto(member);
    }

    /**
     * 회원 탈퇴시 사용하는 서비스 메서드
     * 유저 탈퇴시 가지고 있던 리프레시 토큰 삭제
     * 아무런 정보도 가지고 있지 않은 쿠키 생성
     * @param userDetails 로그인한 유저의 정보
     * @return 정보가 없는 쿠키
     */
    public ResponseCookie unregister(UserDetailsImpl userDetails) {
        Member member = memberRepository.findByMemberId(userDetails.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));
        refreshTokenRepository.deleteByMemberId(member.getMemberId());
        ResponseCookie responseCookie =  jwtUtil.createRefreshTokenCookie("", 0);
        memberRepository.delete(member);
        return responseCookie;
    }
}
