package com.example.account.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InvalidPaymentException extends AbstractException {
    private final String detailMessage;

    public InvalidPaymentException(String detailMessage, String s) {
        this.detailMessage = detailMessage;
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @Override
    public String getMessage() {
        return "유효하지 않은 결제 정보입니다.";
    }

}