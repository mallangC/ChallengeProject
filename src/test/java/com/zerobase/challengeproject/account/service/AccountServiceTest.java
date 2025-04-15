package com.zerobase.challengeproject.account.service;

import com.github.javafaker.Faker;
import com.zerobase.challengeproject.account.domain.dto.AccountDetailDto;
import com.zerobase.challengeproject.account.domain.dto.RefundDto;
import com.zerobase.challengeproject.account.domain.request.AccountAddRequest;
import com.zerobase.challengeproject.account.domain.request.RefundAddRequest;
import com.zerobase.challengeproject.account.domain.request.RefundUpdateRequest;
import com.zerobase.challengeproject.account.entity.AccountDetail;
import com.zerobase.challengeproject.account.entity.Refund;
import com.zerobase.challengeproject.account.repository.AccountDetailRepository;
import com.zerobase.challengeproject.account.repository.RefundRepository;
import com.zerobase.challengeproject.exception.CustomException;
import com.zerobase.challengeproject.member.components.jwt.UserDetailsImpl;
import com.zerobase.challengeproject.member.entity.Member;
import com.zerobase.challengeproject.member.repository.MemberRepository;
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

  private final Faker faker = new Faker();

  Member memberBase = Member.builder()
          .id(1L)
          .loginId(faker.name().username())
          .account(10000L)
          .accountDetails(List.of())
          .build();

  Member memberSearch = Member.builder()
          .id(1L)
          .loginId(faker.name().username())
          .account(10000L)
          .accountDetails(List.of(AccountDetail.builder()
                  .id(1L)
                  .amount(5000L)
                  .accountType(AccountType.CHARGE)
                  .isRefunded(false)
                  .build()))
          .build();

  AccountAddRequest accountAddForm = AccountAddRequest.builder()
          .chargeAmount(5000L)
          .build();

  RefundAddRequest refundAddRequest = RefundAddRequest.builder()
          .accountId(1L)
          .content("환불 사유")
          .build();

  UserDetailsImpl userDetails = new UserDetailsImpl(memberBase);

  @Test
  @DisplayName("금액 충전 성공")
  void addAmount() {
    //given
    //when
    AccountDetailDto result = accountService.addAmount(accountAddForm, memberBase);

    //then
    assertEquals(10000L, result.getPreAmount());
    assertEquals(15000L, result.getCurAmount());
    assertEquals(5000L, result.getAmount());
    assertEquals(AccountType.CHARGE, result.getAccountType());
    assertFalse(result.isRefunded());
    verify(accountDetailRepository, times(1)).save(any());
  }


  @Test
  @DisplayName("환불 신청 성공")
  void addRefund() {
    //given
    given(refundRepository.existsByAccountDetail_Id(anyLong()))
            .willReturn(false);
    given(memberRepository.searchByLoginIdAndAccountDetailId(anyString(), anyLong()))
            .willReturn(Optional.ofNullable(memberSearch));

    //when
    RefundDto result =
            accountService.addRefund(refundAddRequest, userDetails.getUsername());

    //then
    assertEquals(1L, result.getAccountDetailId());
    assertEquals("환불 사유", result.getMemberContent());
    assertNull(result.getAdminContent());
    assertFalse(result.isDone());
    assertFalse(result.isRefunded());
    verify(refundRepository, times(1)).save(any());
  }


  @Test
  @DisplayName("환불 신청 실패(이미 있는 환불 신청)")
  void addRefundFailure1() {
    //given
    given(refundRepository.existsByAccountDetail_Id(anyLong()))
            .willReturn(true);

    //when
    CustomException exception = assertThrows(CustomException.class, () ->
            accountService.addRefund(refundAddRequest, userDetails.getUsername()));
    //then
    assertEquals(ALREADY_REFUND_REQUEST, exception.getErrorCode());
    verify(refundRepository, times(0)).save(any());

  }


  @Test
  @DisplayName("회원 환불 신청 확인 성공(1개 조회)")
  void getAllMyRefund() {
    Pageable pageable = PageRequest.of(0, 20);
    List<Refund> refunds = List.of(Refund.builder()
            .id(1L)
            .accountDetail(AccountDetail.builder()
                    .id(1L)
                    .accountType(AccountType.CHARGE)
                    .isRefunded(false)
                    .preAmount(5000L)
                    .curAmount(5000L)
                    .build())
            .memberContent("환불 사유")
            .adminContent(null)
            .isDone(false)
            .isRefunded(false)
            .build());


    Page<Refund> pageRefundDtos = new PageImpl<>(refunds, pageable, 2L);

    //given
    given(refundRepository.searchAllMyRefund(anyInt(), anyString()))
            .willReturn(pageRefundDtos);
    //when
    Page<RefundDto> result = accountService.getAllRefund(1, userDetails.getUsername());

    //then
    assertEquals(1L, result.getContent().get(0).getId());
    assertEquals(1L, result.getContent().get(0).getAccountDetailId());
    assertEquals("환불 사유", result.getContent().get(0).getMemberContent());
    assertNull(result.getContent().get(0).getAdminContent());
    assertFalse(result.getContent().get(0).isDone());
    assertFalse(result.getContent().get(0).isRefunded());
  }


  @Test
  @DisplayName("회원 환불 신청 취소 성공")
  void cancelRefund() {
    //given
    given(refundRepository.searchRefundByIdAndLoginId(anyLong(), anyString()))
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
    RefundDto result = accountService.cancelRefund(1L, "test");
    //then
    assertEquals(1L, result.getId());
    assertEquals(1L, result.getAccountDetailId());
    assertEquals("환불 사유", result.getMemberContent());
    assertNull(result.getAdminContent());
    assertFalse(result.isDone());
    assertFalse(result.isRefunded());
    verify(refundRepository, times(1)).delete(any());
  }


  @Test
  @DisplayName("회원 환불 신청 취소 실패(환불 신청을 찾을 수 없음)")
  void cancelRefundFailure1() {
    //given
    given(refundRepository.searchRefundByIdAndLoginId(anyLong(), anyString()))
            .willReturn(Optional.empty());
    //when
    CustomException exception = assertThrows(CustomException.class, () ->
            accountService.cancelRefund(1L, "test"));
    //then
    assertEquals(NOT_FOUND_REFUND, exception.getErrorCode());
    verify(refundRepository, times(0)).delete(any());

  }


  @Test
  @DisplayName("회원 환불 신청 취소 실패(환불 신청을 찾을 수 없음)")
  void cancelRefundFailure2() {
    //given
    given(refundRepository.searchRefundByIdAndLoginId(anyLong(), anyString()))
            .willReturn(Optional.of(Refund.builder()
                    .id(1L)
                    .accountDetail(memberSearch.getAccountDetails().get(0))
                    .member(memberSearch)
                    .memberContent("환불 사유")
                    .adminContent(null)
                    .isDone(true)
                    .isRefunded(false)
                    .build()));
    //when
    CustomException exception = assertThrows(CustomException.class, () ->
            accountService.cancelRefund(1L, "test"));
    //then
    assertEquals(ALREADY_DONE, exception.getErrorCode());
    verify(refundRepository, times(0)).delete(any());
  }


  @Test
  @DisplayName("환불 승인 성공")
  void refundDecision1() {
    //given
    given(refundRepository.searchRefundById(anyLong()))
            .willReturn(Optional.ofNullable(Refund.builder()
                    .id(1L)
                    .isDone(false)
                    .isRefunded(false)
                    .memberContent("환불 사유")
                    .adminContent(null)
                    .member(memberSearch)
                    .accountDetail(memberSearch.getAccountDetails().get(0))
                    .build()));

    given(memberRepository.searchByLoginIdAndAccountDetailsToDate(anyString(), any()))
            .willReturn(Optional.ofNullable(memberSearch));

    RefundUpdateRequest refundUpdateRequest = RefundUpdateRequest.builder()
            .refundId(1L)
            .content("환불 완료")
            .build();

    //when
    RefundDto result = accountService.refundDecision(true, refundUpdateRequest);

    //then
    assertEquals(1L, result.getId());
    assertEquals(1L, result.getAccountDetailId());
    assertEquals("환불 완료", result.getAdminContent());
    assertEquals("환불 사유", result.getMemberContent());
    assertTrue(result.isDone());
    assertTrue(result.isRefunded());
  }

  @Test
  @DisplayName("환불 비승인 성공")
  void refundDecision2() {
    //given
    given(refundRepository.searchRefundById(anyLong()))
            .willReturn(Optional.ofNullable(Refund.builder()
                    .id(1L)
                    .isDone(false)
                    .isRefunded(false)
                    .memberContent("환불 사유")
                    .adminContent(null)
                    .member(memberSearch)
                    .accountDetail(memberSearch.getAccountDetails().get(0))
                    .build()));

    RefundUpdateRequest refundUpdateRequest = RefundUpdateRequest.builder()
            .refundId(1L)
            .content("이미 사용한 금액은 환불할 수 없습니다.")
            .build();

    //when
    RefundDto result = accountService.refundDecision(false, refundUpdateRequest);

    //then
    assertEquals(1L, result.getId());
    assertEquals(1L, result.getAccountDetailId());
    assertEquals("이미 사용한 금액은 환불할 수 없습니다.", result.getAdminContent());
    assertEquals("환불 사유", result.getMemberContent());
    assertTrue(result.isDone());
    assertFalse(result.isRefunded());
  }


  @Test
  @DisplayName("환불 승인 실패(충전 내역이 아님(잘못된 환불 신청 내역))")
  void refundDecisionFailure() {
    //given
    given(refundRepository.searchRefundById(anyLong()))
            .willReturn(Optional.ofNullable(Refund.builder()
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
                    .build()));

    RefundUpdateRequest refundUpdateRequest = RefundUpdateRequest.builder()
            .refundId(1L)
            .content("이미 사용한 금액은 환불할 수 없습니다.")
            .build();

    //when
    CustomException exception = assertThrows(CustomException.class, () ->
            accountService.refundDecision(false, refundUpdateRequest));

    //then
    assertEquals(NOT_CHARGE_DETAIL, exception.getErrorCode());
    verify(accountDetailRepository, times(0)).save(any());

  }


}