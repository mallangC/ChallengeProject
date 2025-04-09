package com.zerobase.challengeproject.account.service;

import com.zerobase.challengeproject.BaseResponseDto;
import com.zerobase.challengeproject.account.domain.dto.AccountDetailDto;
import com.zerobase.challengeproject.account.domain.dto.PageDto;
import com.zerobase.challengeproject.account.domain.dto.RefundDto;
import com.zerobase.challengeproject.account.domain.form.AccountAddForm;
import com.zerobase.challengeproject.account.domain.form.RefundAddForm;
import com.zerobase.challengeproject.account.domain.form.RefundUpdateForm;
import com.zerobase.challengeproject.account.entity.AccountDetail;
import com.zerobase.challengeproject.account.entity.Refund;
import com.zerobase.challengeproject.account.repository.AccountDetailRepository;
import com.zerobase.challengeproject.member.components.jwt.UserDetailsImpl;
import com.zerobase.challengeproject.member.repository.MemberRepository;
import com.zerobase.challengeproject.account.repository.RefundRepository;
import com.zerobase.challengeproject.exception.CustomException;
import com.zerobase.challengeproject.member.entity.Member;
import com.zerobase.challengeproject.type.AccountType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static com.zerobase.challengeproject.exception.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

  @Mock
  private MemberRepository memberRepository;

  @Mock
  private AccountDetailRepository accountDetailRepository;

  @Mock
  private RefundRepository refundRepository;

  @InjectMocks
  private AccountService accountService;

  Member memberBase = Member.builder()
          .id(1L)
          .memberId("test@company.com")
          .account(10000L)
          .accountDetails(List.of())
          .build();

  Member memberSearch = Member.builder()
          .id(1L)
          .memberId("test@company.com")
          .account(10000L)
          .accountDetails(List.of(AccountDetail.builder()
                  .id(1L)
                  .amount(5000L)
                  .accountType(AccountType.CHARGE)
                  .isRefunded(false)
                  .build()))
          .build();

  AccountAddForm accountAddForm = AccountAddForm.builder()
          .chargeAmount(5000L)
          .build();

  RefundAddForm refundAddForm = RefundAddForm.builder()
          .accountId(1L)
          .content("환불 사유")
          .build();

  UserDetailsImpl userDetails = new UserDetailsImpl(memberBase);

  @Test
  @DisplayName("금액 충전 성공")
  void addAmount() {
    //given
    given(memberRepository.findByMemberId(anyString()))
            .willReturn(Optional.of(memberBase));

    //when
    BaseResponseDto<AccountDetailDto> responseDto = accountService.addAmount(accountAddForm, userDetails);

    //then
    assertEquals(HttpStatus.OK, responseDto.getStatus());
    assertEquals("test@company.com", responseDto.getData().getMemberId());
    assertEquals(10000L, responseDto.getData().getPreAmount());
    assertEquals(15000L, responseDto.getData().getCurAmount());
    assertEquals(5000L, responseDto.getData().getAmount());
    assertEquals(AccountType.CHARGE, responseDto.getData().getAccountType());
    assertFalse(responseDto.getData().isRefunded());
    verify(accountDetailRepository, times(1)).save(any());
  }


  @Test
  @DisplayName("금액 충전 실패(회원을 찾을 수 없음)")
  void addAmountFailure() {
    //given
    given(memberRepository.findByMemberId(anyString()))
            .willReturn(Optional.empty());
    try {
      //when
      accountService.addAmount(accountAddForm, userDetails);
    } catch (CustomException e) {
      //then
      assertEquals(NOT_FOUND_MEMBER, e.getErrorCode());
    }
  }


  @Test
  @DisplayName("환불 신청 성공")
  void addRefund() {
    //given
    given(refundRepository.existsByAccountDetail_Id(anyLong()))
            .willReturn(false);
    given(memberRepository.searchByLoginIdAndAccountDetailId(anyString(), anyLong()))
            .willReturn(memberSearch);

    //when
    BaseResponseDto<RefundDto> result = accountService.addRefund(refundAddForm, userDetails);

    //then
    assertEquals(1L, result.getData().getAccountDetailId());
    assertEquals("환불 사유", result.getData().getMemberContent());
    assertNull(result.getData().getAdminContent());
    assertFalse(result.getData().isDone());
    assertFalse(result.getData().isRefunded());
    assertEquals(HttpStatus.OK, result.getStatus());
    assertEquals("환불 신청을 성공했습니다.", result.getMessage());
    verify(refundRepository, times(1)).save(any());
  }


  @Test
  @DisplayName("환불 신청 실패(이미 있는 환불 신청)")
  void addRefundFailure1() {
    //given
    given(refundRepository.existsByAccountDetail_Id(anyLong()))
            .willReturn(true);
    try {
      //when
      accountService.addRefund(refundAddForm, userDetails);
    } catch (CustomException e) {
      //then
      assertEquals(ALREADY_REFUND_REQUEST, e.getErrorCode());
      verify(refundRepository, times(0)).save(any());
    }
  }


  @Test
  @DisplayName("회원 환불 신청 확인 성공(1개 조회)")
  void getAllMyRefund() {
    Pageable pageable = PageRequest.of(0, 20);
    List<RefundDto> refundDtos = List.of(RefundDto.builder()
            .id(1L)
            .accountDetailId(1L)
            .memberContent("환불 사유")
            .adminContent(null)
            .isDone(false)
            .isRefunded(false)
            .build());


    Page<RefundDto> pageRefundDtos = new PageImpl<>(refundDtos, pageable, 2L);

    //given
    given(refundRepository.searchAllMyRefund(anyInt(), anyString()))
            .willReturn(pageRefundDtos);
    //when
    BaseResponseDto<PageDto<RefundDto>> result = accountService.getAllMyRefund(1, userDetails);

    //then
    assertEquals(HttpStatus.OK, result.getStatus());
    assertEquals("회원의 환불신청 조회에 성공했습니다.(1페이지)", result.getMessage());
    assertEquals(1L, result.getData().getContent().get(0).getId());
    assertEquals(1L, result.getData().getContent().get(0).getAccountDetailId());
    assertEquals("환불 사유", result.getData().getContent().get(0).getMemberContent());
    assertNull(result.getData().getContent().get(0).getAdminContent());
    assertFalse(result.getData().getContent().get(0).isDone());
    assertFalse(result.getData().getContent().get(0).isRefunded());
    assertEquals(1, result.getData().getTotalElements());
    assertEquals(0, result.getData().getNumber());
    assertEquals(20, result.getData().getSize());
  }


  @Test
  @DisplayName("회원 환불 신청 취소 성공")
  void cancelRefund() {
    //given
    given(refundRepository.findById(anyLong()))
            .willReturn(Optional.of(Refund.builder()
                    .id(1L)
                    .accountDetail(memberSearch.getAccountDetails().get(0))
                    .member(memberSearch)
                    .memberContent("환불 사유")
                    .adminContent(null)
                    .isDone(false)
                    .isRefunded(false)
                    .build()));
    //when
    BaseResponseDto<RefundDto> result = accountService.cancelRefund(1L);
    //then
    assertEquals(HttpStatus.OK, result.getStatus());
    assertEquals("환불 신청을 취소했습니다.", result.getMessage());
    assertEquals(1L, result.getData().getId());
    assertEquals(1L, result.getData().getAccountDetailId());
    assertEquals("환불 사유", result.getData().getMemberContent());
    assertNull(result.getData().getAdminContent());
    assertFalse(result.getData().isDone());
    assertFalse(result.getData().isRefunded());
    verify(refundRepository, times(1)).delete(any());
  }


  @Test
  @DisplayName("회원 환불 신청 취소 실패(환불 신청을 찾을 수 없음)")
  void cancelRefundFailure1() {
    //given
    given(refundRepository.findById(anyLong()))
            .willReturn(Optional.empty());

    try {
      //when
      accountService.cancelRefund(1L);
    } catch (CustomException e) {
      assertEquals(NOT_FOUND_REFUND, e.getErrorCode());
      verify(refundRepository, times(0)).delete(any());
    }
  }


  @Test
  @DisplayName("회원 환불 신청 취소 실패(환불 신청을 찾을 수 없음)")
  void cancelRefundFailure2() {
    //given
    given(refundRepository.findById(anyLong()))
            .willReturn(Optional.of(Refund.builder()
                    .id(1L)
                    .accountDetail(memberSearch.getAccountDetails().get(0))
                    .member(memberSearch)
                    .memberContent("환불 사유")
                    .adminContent(null)
                    .isDone(true)
                    .isRefunded(false)
                    .build()));

    try {
      //when
      accountService.cancelRefund(1L);
    } catch (CustomException e) {
      assertEquals(ALREADY_DONE, e.getErrorCode());
      verify(refundRepository, times(0)).delete(any());
    }
  }


  @Test
  @DisplayName("환불 승인 성공")
  void refundDecision1() {
    //given
    given(refundRepository.searchRefundById(anyLong()))
            .willReturn(Refund.builder()
                    .id(1L)
                    .isDone(false)
                    .isRefunded(false)
                    .memberContent("환불 사유")
                    .adminContent(null)
                    .member(memberSearch)
                    .accountDetail(memberSearch.getAccountDetails().get(0))
                    .build());

    given(memberRepository.searchByLoginIdAndAccountDetailsToDate(anyString(), any()))
            .willReturn(memberSearch);

    RefundUpdateForm refundUpdateForm = RefundUpdateForm.builder()
            .refundId(1L)
            .content("환불 완료")
            .build();

    //when
    BaseResponseDto<RefundDto> result = accountService.refundDecision(
            true, refundUpdateForm);

    //then
    assertEquals(HttpStatus.OK, result.getStatus());
    assertEquals("환불 승인을 성공했습니다.", result.getMessage());
    assertEquals(1L, result.getData().getId());
    assertEquals(1L, result.getData().getAccountDetailId());
    assertEquals("환불 완료", result.getData().getAdminContent());
    assertEquals("환불 사유", result.getData().getMemberContent());
    assertTrue(result.getData().isDone());
    assertTrue(result.getData().isRefunded());
  }

  @Test
  @DisplayName("환불 비승인 성공")
  void refundDecision2() {
    //given
    given(refundRepository.searchRefundById(anyLong()))
            .willReturn(Refund.builder()
                    .id(1L)
                    .isDone(false)
                    .isRefunded(false)
                    .memberContent("환불 사유")
                    .adminContent(null)
                    .member(memberSearch)
                    .accountDetail(memberSearch.getAccountDetails().get(0))
                    .build());

    RefundUpdateForm refundUpdateForm = RefundUpdateForm.builder()
            .refundId(1L)
            .content("이미 사용한 금액은 환불할 수 없습니다.")
            .build();

    //when
    BaseResponseDto<RefundDto> result = accountService.refundDecision(
            false, refundUpdateForm);

    //then
    assertEquals(HttpStatus.OK, result.getStatus());
    assertEquals("환불 비승인을 성공했습니다.", result.getMessage());
    assertEquals(1L, result.getData().getId());
    assertEquals(1L, result.getData().getAccountDetailId());
    assertEquals("이미 사용한 금액은 환불할 수 없습니다.", result.getData().getAdminContent());
    assertEquals("환불 사유", result.getData().getMemberContent());
    assertTrue(result.getData().isDone());
    assertFalse(result.getData().isRefunded());
  }


  @Test
  @DisplayName("환불 승인 실패(충전 내역이 아님(잘못된 환불 신청 내역))")
  void refundDecisionFailure() {
    //given
    given(refundRepository.searchRefundById(anyLong()))
            .willReturn(Refund.builder()
                    .id(1L)
                    .isDone(false)
                    .isRefunded(false)
                    .memberContent("환불 사유")
                    .adminContent(null)
                    .member(memberSearch)
                    .accountDetail(AccountDetail.builder()
                            .id(1L)
                            .accountType(AccountType.REFUND)
                            .build())
                    .build());

    RefundUpdateForm refundUpdateForm = RefundUpdateForm.builder()
            .refundId(1L)
            .content("이미 사용한 금액은 환불할 수 없습니다.")
            .build();

    try {
      //when
      accountService.refundDecision(false, refundUpdateForm);
    } catch (CustomException e) {
      //then
      assertEquals(NOT_CHARGE_DETAIL, e.getErrorCode());
      verify(accountDetailRepository, times(0)).save(any());
    }
  }


}