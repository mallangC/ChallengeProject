package com.zerobase.challengeproject.member.service;

import com.zerobase.challengeproject.exception.CustomException;
import com.zerobase.challengeproject.exception.ErrorCode;
import com.zerobase.challengeproject.member.components.jwt.JwtUtil;
import com.zerobase.challengeproject.member.domain.dto.MemberLoginResponse;
import com.zerobase.challengeproject.member.domain.dto.MemberLogoutDto;
import com.zerobase.challengeproject.member.domain.dto.RefreshTokenDto;
import com.zerobase.challengeproject.member.domain.form.MemberLoginForm;
import com.zerobase.challengeproject.member.entity.Member;
import com.zerobase.challengeproject.member.entity.RefreshToken;
import com.zerobase.challengeproject.member.repository.MemberRepository;
import com.zerobase.challengeproject.member.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberLoginService {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * 유저가 로그인을 시도할 때 사용되는 서비스 메서드
     * @param form 유저 아이디, 비밀번호
     * @return 토큰, 유저 아이디
     */
    public MemberLoginResponse login(MemberLoginForm form) {
        Member member = memberRepository.findByMemberId(form.getMemberId())
                .orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND_MEMBER));
        if(!passwordEncoder.matches(form.getPassword(), member.getPassword())) {
            throw new CustomException(ErrorCode.INCORRECT_PASSWORD);
        }
        String accessToken = jwtUtil.generateAccessToken(form.getMemberId(), member.getMemberType());
        String refreshToken = jwtUtil.generateRefreshToken(form.getMemberId(), member.getMemberType());

        //리프레시 토큰이 존재시 한명의 유저가 여러 개의 리프레시토큰을 가질 수 있어 그것을 방지하고 자 만듦
        Optional<RefreshToken> existingToken = refreshTokenRepository.findByMemberId(form.getMemberId());
        existingToken.ifPresent(token -> refreshTokenRepository.deleteByMemberId(token.getMemberId()));

        RefreshToken refreshTokenEntity = new RefreshToken(null, form.getMemberId(), refreshToken, Instant.now()
                .plusMillis(7 * 24 * 60 * 60 * 1000));
        refreshTokenRepository.save(refreshTokenEntity);

        ResponseCookie responseCookie = jwtUtil.createRefreshTokenCookie(refreshToken, 7);

        return new MemberLoginResponse(accessToken, responseCookie, member.getMemberId());
    }
    /**
     * 로그아웃 처리 서비스
     * 엑세스 토큰에서 "Bearer "를 제거하고 회원 아이디를 추출.
     * 리프레시 토큰이 제공되지 않거나 유효하지 안으면 예외 처리
     * 리프레시 토큰이 유효할 경우 삭제
     * @param token JWT 액세스 토큰
     * @param refreshToken 리프레시 토큰
     * @return 로그아웃 후 반환할 DTO 객체
     * @throws CustomException 토큰이 제공되지 않거나 유효하지 않은 경우 예외 발생
     */
    public MemberLogoutDto logout(String token, String refreshToken) {
        token = token.substring(7);
        String memberId = jwtUtil.extractMemberId(token);
        if (refreshToken == null) {
            throw new CustomException(ErrorCode.TOKEN_NOT_PROVIDED);
        }
        if (!jwtUtil.isTokenValid(refreshToken)) {
            throw new CustomException(ErrorCode.TOKEN_IS_INVALID_OR_EXPIRED);
        }
        refreshTokenRepository.deleteByMemberId(memberId);

        ResponseCookie responseCookie =  jwtUtil.createRefreshTokenCookie("", 0);
        return new MemberLogoutDto(memberId, responseCookie);
    }
    /**
     * 리프레시 토큰을 이용해 새로운 액세스 토큰을 발급하는 서비스
     * @param refreshToken 리프레시 토큰
     * @return 새로운 액세스 토큰과 리프레시 토큰을 포함하는 DTO 객체
     * @throws CustomException 토큰이 유효하지 않거나 만료된 경우 예외 발생
     */
    public RefreshTokenDto refreshAccessToken(String refreshToken) {
        if (refreshToken == null || !jwtUtil.isTokenValid(refreshToken)) {
            throw new CustomException(ErrorCode.TOKEN_IS_INVALID_OR_EXPIRED);
        }

        String memberId = jwtUtil.extractMemberId(refreshToken);
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND_MEMBER));

        Optional<RefreshToken> storedToken = refreshTokenRepository.findByToken(refreshToken);

        if (storedToken.isPresent() && storedToken.get().getMemberId().equals(memberId)) {
            String newAccessToken = jwtUtil.generateAccessToken(memberId, member.getMemberType());
            return new RefreshTokenDto(refreshToken, newAccessToken);
        } else {
            throw new CustomException(ErrorCode.EXPIRED_TOKEN);
        }
    }
}
