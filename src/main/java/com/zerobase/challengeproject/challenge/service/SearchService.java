package com.zerobase.challengeproject.challenge.service;


import com.zerobase.challengeproject.challenge.domain.dto.GetChallengeDto;
import com.zerobase.challengeproject.challenge.entity.Challenge;
import com.zerobase.challengeproject.challenge.repository.ChallengeRepository;
import com.zerobase.challengeproject.type.CategoryType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final ChallengeRepository challengeRepository;

    /**
     * 제목으로 챌린지 검색
     */
    public Page<GetChallengeDto> searchChallengesByTitle(String title, Pageable pageable) {
        Page<Challenge> challenges = challengeRepository.searchByTitleFullText(title, pageable);
        return challenges.map(GetChallengeDto::new);
    }

    /**
     * 카테고리로 챌린지 검색
     */
    public Page<GetChallengeDto> searchChallengesByCategory(CategoryType categoryType, Pageable pageable) {
        Page<Challenge> challenges = challengeRepository.findByCategoryType(categoryType, pageable);
        return challenges.map(GetChallengeDto::new);
    }
}
