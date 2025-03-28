package com.zerobase.challengeproject.challenge.domain.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class ChallengeForm {

    @NotBlank(message = "어떤 게시물인지 선택하세요.")
    private Long id;

    @NotBlank(message = "제목은 필수 항목입니다.")
    @Size(min = 3, max = 100, message = "제목은 3자 이상, 100자 이하로 입력해야 합니다.")
    private String title;

    private String img;

    private Integer participant;

    @NotBlank(message = "내용은 필수 항목입니다.")
    @Size(min = 10, max = 500, message = "내용은 10자 이상, 500자 이하로 입력해야 합니다.")
    private String description;


    @NotBlank
    private Integer min_deposit;

    @NotBlank
    private Integer max_deposit;

    @NotBlank
    private String standard;


    @NotBlank
    private LocalDateTime startDate;

    @NotBlank
    private LocalDateTime endDate;

}
