package com.askwinston.service;

import com.askwinston.exception.PaymentException;
import com.askwinston.model.BillingCard;
import com.askwinston.web.dto.BillingCardDto;

public interface PaymentService {

    BillingCard saveBillingCard(Long userId, BillingCardDto dto) throws PaymentException;

    void deleteBillingCard(BillingCard card) throws PaymentException;

    String lockAmount(String orderId, BillingCard card, long amount) throws PaymentException;

    String capturePayment(String orderId, String transactionNumber, long amount) throws PaymentException;

    void refundPayment(String orderId, String transactionNumber, long amount) throws PaymentException;

    void verifyBillingCard(BillingCard card) throws PaymentException;
}
