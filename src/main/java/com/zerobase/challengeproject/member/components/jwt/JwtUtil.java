package com.zerobase.challengeproject.member.components.jwt;

import com.zerobase.challengeproject.type.MemberType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 토큰을 생성하고 검증하는 유틸리티 클래스
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret-key}")
    private String SECRET_KEY;
    private SecretKey secretKey;
    private final long ACCESS_TOKEN_EXPIRATION = 60 * 60 * 1000;
    private final long REFRESH_TOKEN_EXPIRATION = 7 * 24 * 60 * 60 * 1000;

    @PostConstruct
    public void init() {
        secretKey = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }
    /**
     * 액세스 토큰 생성
     * @param memberId 사용자 이름
     * @param role 사용자 역할
     * @return 생성된 JWT 액세스 토큰
     */
    public String generateAccessToken(String memberId, MemberType role) {

        return Jwts.builder()
                .setSubject(memberId)
                .claim("role", role.getAuthority())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

    }
    /**
     * 리프레시 토큰 생성
     * @param memberId 사용자 이름
     * @param role 사용자 역할
     * @return 생성된 JWT 리프레시 토큰
     */
    public String generateRefreshToken(String memberId, MemberType role) {
        return Jwts.builder()
                .setSubject(memberId)
                .claim("role", role.getAuthority())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }
    /**
     * JWT에서 사용자 이름 추출
     * @param token JWT 토큰
     * @return 사용자 이름
     */
    public String extractMemberId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
    /**
     * JWT 토큰의 유효성 검사
     * @param token JWT 토큰
     * @return 유효한 경우 true, 그렇지 않으면 false
     */
    public boolean isTokenValid(String token) {
        return !extractExpiration(token).before(new Date());
    }
    /**
     * JWT에서 만료 날짜 추출
     * @param token JWT 토큰
     * @return 만료 날짜
     */
    private Date extractExpiration(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }
    /**
     * JWT에서 역할(Role) 정보 추출
     * @param token JWT 토큰
     * @return 역할 문자열
     */
    public String extractRoles(String token) {

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("role", String.class);
    }

    /**
     * 리프레시 토큰을 위한 HTTP 쿠키 생성
     * @param refreshToken 리프레시 토큰
     * @param expiresIn 만료 기간 (일 단위)
     * @return 생성된 ResponseCookie
     */
    public ResponseCookie createRefreshTokenCookie(String refreshToken, int expiresIn) {
        boolean isSecure = !isLocalEnvironment();

        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(isSecure)
                .path("/")
                .maxAge((long) expiresIn * 24 * 60 * 60)
                .build();
    }

    /**
     * 현재 환경이 로컬인지 확인
     * @return 로컬 환경이면 true, 그렇지 않으면 false
     */
    private boolean isLocalEnvironment() {
        String profile = System.getProperty("spring.profiles.active", "default");
        return profile.equals("local") || profile.equals("default");
    }
}
