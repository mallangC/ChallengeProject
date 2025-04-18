package com.zerobase.challengeproject.member.service;

import com.zerobase.challengeproject.exception.CustomException;
import com.zerobase.challengeproject.exception.ErrorCode;
import com.zerobase.challengeproject.member.components.jwt.JwtUtil;
import com.zerobase.challengeproject.member.domain.dto.MemberLogoutDto;
import com.zerobase.challengeproject.member.domain.dto.RefreshTokenDto;
import com.zerobase.challengeproject.member.entity.Member;
import com.zerobase.challengeproject.member.entity.RefreshToken;
import com.zerobase.challengeproject.member.repository.MemberRepository;
import com.zerobase.challengeproject.member.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberLoginService {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * 로그아웃 처리 서비스
     * 엑세스 토큰에서 "Bearer "를 제거하고 회원 아이디를 추출.
     * 리프레시 토큰이 제공되지 않거나 유효하지 안으면 예외 처리
     * 리프레시 토큰이 유효할 경우 삭제
     * @param token JWT 액세스 토큰
     * @return 로그아웃 후 반환할 DTO 객체
     * @throws CustomException 토큰이 제공되지 않거나 유효하지 않은 경우 예외 발생
     */
    public MemberLogoutDto logout(String token) {
        String loginId = jwtUtil.extractLoginId(token);
        refreshTokenRepository.deleteByLoginId(loginId);
        ResponseCookie responseCookie =  jwtUtil.createRefreshTokenCookie("", 0);
        return new MemberLogoutDto(loginId, responseCookie);
    }
    /**
     * 리프레시 토큰을 이용해 새로운 액세스 토큰을 발급하는 서비스
     * @param refreshToken 리프레시 토큰
     * @return 새로운 액세스 토큰과 리프레시 토큰을 포함하는 DTO 객체
     * @throws CustomException 토큰이 유효하지 않거나 만료된 경우 예외 발생
     */
    public RefreshTokenDto refreshAccessToken(String refreshToken) {

        String loginId = jwtUtil.extractLoginId(refreshToken);
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND_MEMBER));

        Optional<RefreshToken> storedToken = refreshTokenRepository.findByToken(refreshToken);

        if (storedToken.isPresent() && storedToken.get().getLoginId().equals(loginId)) {
            String newAccessToken = jwtUtil.generateAccessToken(loginId, member.getMemberType());
            return new RefreshTokenDto(refreshToken, newAccessToken);
        } else {
            throw new CustomException(ErrorCode.EXPIRED_TOKEN);
        }
    }
}
