package com.zerobase.challengeproject.comment.domain.dto;

import com.zerobase.challengeproject.comment.entity.CoteChallenge;
import com.zerobase.challengeproject.comment.entity.CoteComment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Getter
@Builder
public class CoteChallengeDto {
  private Long id;
  private Long challengeId;
  private String title;
  private String questionUrl;
  private LocalDateTime startAt;
  private List<CoteCommentDto> comments;

  public static CoteChallengeDto from(CoteChallenge coteChallenge) {
    List<CoteCommentDto> commentDtos = Optional.ofNullable(coteChallenge.getComments())
            .orElse(Collections.emptyList()) // null일 경우 빈 리스트
            .stream()
            .map(CoteCommentDto::from)
            .toList();

    return CoteChallengeDto.builder()
            .id(coteChallenge.getId())
            .challengeId(coteChallenge.getId())
            .title(coteChallenge.getTitle())
            .questionUrl(coteChallenge.getQuestionUrl())
            .startAt(coteChallenge.getStartAt())
            .comments(commentDtos)
            .build();
  }

  public static CoteChallengeDto fromWithoutComments(CoteChallenge coteChallenge) {
    return CoteChallengeDto.builder()
            .id(coteChallenge.getId())
            .challengeId(coteChallenge.getId())
            .title(coteChallenge.getTitle())
            .questionUrl(coteChallenge.getQuestionUrl())
            .startAt(coteChallenge.getStartAt())
            .comments(new ArrayList<>())
            .build();
  }


}
