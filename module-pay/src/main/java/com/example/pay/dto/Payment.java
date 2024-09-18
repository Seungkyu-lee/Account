package com.example.pay.dto;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Payment {

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Request {
		@NotBlank(message = "Payment key is required")
		private String paymentKey;

		@NotBlank(message = "Order ID is required")
		private String orderId;

		@NotNull(message = "Amount is required")
		@Positive(message = "Amount must be positive")
		private Integer amount;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Response {
		@JsonProperty("mId")
		private String mId;
		private String version;
		private String paymentKey;
		private String orderId;
		private String orderName;
		private String currency;
		private String method;
		private Integer totalAmount;
		private Integer balanceAmount;
		private String status;
		private OffsetDateTime requestedAt;
		private OffsetDateTime approvedAt;

		private Boolean useEscrow;
		private String lastTransactionKey;
		private Integer suppliedAmount;
		private Integer vat;
		private Boolean cultureExpense;
		private Integer taxFreeAmount;
		private Integer taxExemptionAmount;
		private Card card;
		private VirtualAccount virtualAccount;
		private String secret;
		private String type;
		private EasyPay easyPay;
		private String country;
		private Failure failure;
		private Boolean isPartialCancelable;
		private Receipt receipt;
		private Checkout checkout;
		private CashReceipt cashReceipt;
		private Discount discount;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class StatusResponse {
		private String mId;
		private String version;
		private String paymentKey;
		private String orderId;
		private String status;

		private OffsetDateTime requestedAt;

		private OffsetDateTime approvedAt;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Card {
		private String company;
		private String number;
		private String installmentPlanMonths;
		private String isInterestFree;
		private String approveNo;
		private String useCardPoint;
		private String cardType;
		private String ownerType;
		private String acquireStatus;
		private String receiptUrl;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class VirtualAccount {
		private String accountType;
		private String accountNumber;
		private String bankCode;
		private String customerName;

		private OffsetDateTime dueDate;

		private String refundStatus;
		private Boolean expired;
		private String settlementStatus;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class EasyPay {
		private String provider;
		private Integer amount;
		private Integer discountAmount;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Failure {
		private String code;
		private String message;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CanceledAmount {
		private Integer total;
		private Integer taxFree;
		private Integer vat;
		private Integer point;
		private Integer discount;
		private Integer greenDeposit;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Receipt {
		private String url;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Checkout {
		private String url;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CashReceipt {
		private String type;
		private String receiptKey;
		private String issueNumber;
		private String receiptUrl;
		private Integer amount;
		private Integer taxFreeAmount;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Discount {
		private Integer amount;
	}
}
