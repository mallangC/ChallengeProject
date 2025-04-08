package com.zerobase.challengeproject.challenge.service;


import com.zerobase.challengeproject.challenge.domain.dto.BaseResponseDto;
import com.zerobase.challengeproject.challenge.domain.dto.GetChallengeDto;
import com.zerobase.challengeproject.challenge.entity.Challenge;
import com.zerobase.challengeproject.challenge.repository.ChallengeRepository;
import com.zerobase.challengeproject.type.CategoryType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final ChallengeRepository challengeRepository;

    /**
     * 제목으로 챌린지 검색
     */
    public ResponseEntity<BaseResponseDto<Page<GetChallengeDto>>> searchByTitle(String title, Pageable pageable) {
        Page<Challenge> challenges = challengeRepository.searchByTitleFullText(title, pageable);

        Page<GetChallengeDto> searchResult = challenges.map(challenge -> new GetChallengeDto(challenge));

        return ResponseEntity.ok(new BaseResponseDto<>(searchResult, "제목검색 성공", HttpStatus.OK));

    }

    /**
     * 카테고리로 챌린지 검색
     */
    public ResponseEntity<BaseResponseDto<Page<GetChallengeDto>>> searchByCategory(CategoryType categoryType, Pageable pageable) {
        Page<Challenge> challenges = challengeRepository.findByCategoryType(categoryType, pageable);

        Page<GetChallengeDto> searchResult = challenges.map(challenge -> new GetChallengeDto(challenge));

        return ResponseEntity.ok(new BaseResponseDto<>(searchResult, "카테고리검색 성공", HttpStatus.OK));

    }
}
