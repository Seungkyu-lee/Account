package com.example.account.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // 일반적인 에러
    INVALID_REQUEST("잘못된 요청입니다"),
    INTERNAL_SERVER_ERROR("서버 내부 오류가 발생했습니다"),

    // 결제 관련 에러
    PAYMENT_NOT_FOUND("결제 정보를 찾을 수 없습니다"),
    INVALID_PAYMENT_AMOUNT("유효하지 않은 결제 금액입니다"),
    INVALID_ORDER_ID("유효하지 않은 주문 ID입니다"),
    INVALID_PAYMENT_KEY("유효하지 않은 결제 키입니다"),
    PAYMENT_ALREADY_PROCESSED("이미 처리된 결제입니다"),
    PAYMENT_PROCESSING_ERROR("결제 처리 중 오류가 발생했습니다"),

    // 외부 결제 시스템 관련 에러
    EXTERNAL_PAYMENT_SYSTEM_ERROR("외부 결제 시스템 오류"),
    PAYMENT_DECLINED("결제가 거절되었습니다"),

    // 계정 관련 에러
    INSUFFICIENT_BALANCE("잔액이 부족합니다"),
    ACCOUNT_NOT_FOUND("계정을 찾을 수 없습니다"),
    ACCOUNT_IS_ALREADY("이미 존재하는 사용자명입니다."),
    PASSWORD_NOT_MATCHED("비밀번호가 일치하지 않습니다"),

    // 보안 관련 에러
    UNAUTHORIZED("인증되지 않은 요청입니다"),
    FORBIDDEN("접근 권한이 없습니다");

    private final String description;
}