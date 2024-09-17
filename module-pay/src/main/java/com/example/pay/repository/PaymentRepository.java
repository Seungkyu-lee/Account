package com.example.pay.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.pay.domain.PaymentEntity;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

	Optional<PaymentEntity> findByOrderId(String orderId);

	Optional<PaymentEntity> findByPaymentKey(String paymentKey);
}
