package com.zerobase.challengeproject.challenge.controller;

import com.zerobase.challengeproject.challenge.domain.dto.BaseResponseDto;
import com.zerobase.challengeproject.challenge.domain.dto.GetChallengeDto;
import com.zerobase.challengeproject.challenge.service.SearchService;
import com.zerobase.challengeproject.type.CategoryType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;
    /**
     * 제목으로 챌린지 검색
     */
    @GetMapping("/title")
    public ResponseEntity<BaseResponseDto<Page<GetChallengeDto>>> searchByTitle(@RequestParam String title, Pageable pageable) {

        return searchService.searchByTitle(title, pageable);
    }

    /**
     * 카테고리로 챌린지 검색
     */
    @GetMapping("/category")
    public ResponseEntity<BaseResponseDto<Page<GetChallengeDto>>> searchByCategory(@RequestParam CategoryType categoryType, Pageable pageable) {

        return searchService.searchByCategory(categoryType, pageable);
    }
}
