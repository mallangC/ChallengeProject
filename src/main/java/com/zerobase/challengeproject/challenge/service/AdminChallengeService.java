package com.zerobase.challengeproject.challenge.service;


import com.zerobase.challengeproject.BaseResponseDto;
import com.zerobase.challengeproject.challenge.domain.dto.GetChallengeDto;
import com.zerobase.challengeproject.challenge.entity.Challenge;
import com.zerobase.challengeproject.challenge.repository.ChallengeRepository;
import com.zerobase.challengeproject.exception.CustomException;
import com.zerobase.challengeproject.exception.ErrorCode;
import com.zerobase.challengeproject.member.components.jwt.UserDetailsImpl;
import com.zerobase.challengeproject.type.MemberType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminChallengeService {

    private final ChallengeRepository challengeRepository;

    public ResponseEntity<BaseResponseDto<GetChallengeDto>> deleteChallengeByAdmin(Long challengeId, UserDetailsImpl userDetails){

        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CHALLENGE));

        if(!userDetails.getMember().getMemberType().equals(MemberType.ADMIN)){
            throw new CustomException(ErrorCode.NOT_MEMBER_TYPE_ADMIN);
        }

        challengeRepository.delete(challenge);
        return ResponseEntity.ok(new BaseResponseDto<>(null, "챌린지 삭제 성공", HttpStatus.OK));
    }
}
