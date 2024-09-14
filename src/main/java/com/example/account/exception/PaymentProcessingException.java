package com.example.account.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class PaymentProcessingException extends AbstractException {
    private final String detailMessage;

    public PaymentProcessingException(String detailMessage, String message) {
        this.detailMessage = detailMessage;
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.INTERNAL_SERVER_ERROR.value();
    }

    @Override
    public String getMessage() {
        return "결제 처리 중 오류가 발생했습니다.";
    }

}