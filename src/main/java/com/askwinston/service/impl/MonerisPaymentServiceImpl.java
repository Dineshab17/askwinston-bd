package com.askwinston.service.impl;

import JavaAPI.*;
import com.askwinston.exception.PaymentException;
import com.askwinston.model.BillingCard;
import com.askwinston.model.Province;
import com.askwinston.model.User;
import com.askwinston.repository.BillingCardRepository;
import com.askwinston.repository.UserRepository;
import com.askwinston.service.PaymentService;
import com.askwinston.service.UserService;
import com.askwinston.web.dto.BillingCardDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Service
@RefreshScope
public class MonerisPaymentServiceImpl implements PaymentService {

    @Value("${moneris.test-mode:true}")
    private boolean testMode;

    @Value("${moneris.country-code:CA}")
    private String countryCode;

    @Value("${moneris.store-id}")
    private String storeId;

    @Value("${moneris.api-token}")
    private String apiToken;

    private String eCommerceIndicator = "5";

    private BillingCardRepository billingCardRepository;
    private UserRepository userRepository;
    private UserService userService;

    public MonerisPaymentServiceImpl(BillingCardRepository billingCardRepository, UserRepository userRepository, UserService userService) {
        this.billingCardRepository = billingCardRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    /**
     * @param userId
     * @param dto
     * @return BillingCard
     * @throws PaymentException
     * To Save new billing card for the patient
     */
    @Override
    public BillingCard saveBillingCard(Long userId, BillingCardDto dto) throws PaymentException {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Authenticated user does not have a record"));
        ResAddToken resAddToken = new ResAddToken();
        resAddToken.setDataKey(dto.getToken());
        resAddToken.setCryptType(eCommerceIndicator); //Don't know what it is
        resAddToken.setCustId(user.getFirstName() + " " + user.getLastName());
        resAddToken.setEmail(user.getEmail());
        resAddToken.setPhone(user.getPhone());
        HttpsPostRequest mpgReq = createHttpsPostRequest(resAddToken);
        mpgReq.send();
        Receipt receipt = mpgReq.getReceipt();
        log.info("[PAYMENT]: Save card operation.");
        if (receipt.getResponseCode().equals("null") || Integer.parseInt(receipt.getResponseCode()) > 49) {
            log.error("[PAYMENT ERROR]: Save card operation error: Code: " + receipt.getResponseCode() + "; Message: " + receipt.getMessage());
            throw new PaymentException(receipt.getMessage());
        }
        BillingCard billingCard = buildBillingCard(receipt, dto);
        verifyBillingCard(billingCard);
        userService.addBillingCard(user, billingCard);
        return billingCard;
    }

    /**
     * @param card
     * @throws PaymentException
     * To delete billing card
     */
    @Override
    public void deleteBillingCard(BillingCard card) throws PaymentException {
        userService.deleteBillingCard(card.getUser().getId(), card.getId());
        ResDelete resDelete = new ResDelete(card.getId());
        HttpsPostRequest mpgReq = createHttpsPostRequest(resDelete);
        mpgReq.send();
        Receipt receipt = mpgReq.getReceipt();
        log.info("[PAYMENT]: Delete card operation.");
        if (receipt.getResponseCode().equals("null") || Integer.parseInt(receipt.getResponseCode()) > 49) {
            log.error("[PAYMENT ERROR]: Delete card operation error: Code: " + receipt.getResponseCode() + "; Message: " + receipt.getMessage());
            throw new PaymentException(receipt.getMessage());
        }
        billingCardRepository.delete(card);
    }

    /**
     * @param orderId
     * @param card
     * @param amount
     * @return
     * @throws PaymentException
     * Verifies and locks funds on the customer’s credit card. The funds are locked for a specified amount of time based on the card issuer.
     */
    @Override
    public String lockAmount(String orderId, BillingCard card, long amount) throws PaymentException {
        ResPreauthCC resPreauthCC = new ResPreauthCC();
        resPreauthCC.setData(card.getId());
        resPreauthCC.setOrderId(orderId);
        resPreauthCC.setCustId(Long.toString(card.getUser().getId()));
        String stringAmount = new BigDecimal(amount).setScale(2, RoundingMode.HALF_EVEN).divide(new BigDecimal(100), RoundingMode.HALF_EVEN).toString();
        resPreauthCC.setAmount(stringAmount);
        resPreauthCC.setCryptType(eCommerceIndicator);
        HttpsPostRequest mpgReq = createHttpsPostRequest(resPreauthCC);
        mpgReq.send();
        Receipt receipt = mpgReq.getReceipt();
        log.info("[PAYMENT]: Pre-auth operation. OrderId: " + orderId + ". Amount: " + stringAmount);
        String transactionId = receipt.getTxnNumber();
        if (receipt.getResponseCode().equals("null") || Integer.parseInt(receipt.getResponseCode()) > 49 || transactionId.equals("null")) {
            log.error("[PAYMENT ERROR]: Pre-auth operation error: Code: " + receipt.getResponseCode() + "; Message: " + receipt.getMessage());
            throw new PaymentException(receipt.getMessage());
        }
        return transactionId;
    }

    /**
     * @param orderId
     * @param transactionNumber
     * @param amount
     * @return
     * @throws PaymentException
     * Retrieves funds that have been locked and prepares them for settlement into the merchant’s account
     */
    @Override
    public String capturePayment(String orderId, String transactionNumber, long amount) throws PaymentException {
        Completion completion = new Completion();
        completion.setOrderId(orderId);
        String stringAmount = new BigDecimal(amount).setScale(2, RoundingMode.HALF_EVEN).divide(new BigDecimal(100), RoundingMode.HALF_EVEN).toString();
        completion.setCompAmount(stringAmount);
        completion.setTxnNumber(transactionNumber);
        completion.setCryptType(eCommerceIndicator);
        HttpsPostRequest mpgReq = createHttpsPostRequest(completion);
        mpgReq.send();
        Receipt receipt = mpgReq.getReceipt();
        log.info("[PAYMENT]: Capture operation. OrderId: " + orderId + ". Amount: " + stringAmount);
        String transactionId = receipt.getTxnNumber();
        if (receipt.getResponseCode().equals("null") || Integer.parseInt(receipt.getResponseCode()) > 49 || transactionId.equals("null")) {
            log.error("[PAYMENT ERROR]: Capture operation error: Code: " + receipt.getResponseCode() + "; Message: " + receipt.getMessage());
            throw new PaymentException(receipt.getMessage());
        }
        return transactionId;
    }

    /**
     * @param orderId
     * @param transactionNumber
     * @param amount
     * @throws PaymentException
     * To refund the payment amount to the patient
     */
    @Override
    public void refundPayment(String orderId, String transactionNumber, long amount) throws PaymentException {
        Refund refund = new Refund();
        refund.setTxnNumber(transactionNumber);
        refund.setOrderId(orderId);
        String stringAmount = new BigDecimal(amount).setScale(2, RoundingMode.HALF_EVEN).divide(new BigDecimal(100), RoundingMode.HALF_EVEN).toString();
        refund.setAmount(stringAmount);
        refund.setCryptType(eCommerceIndicator);
        HttpsPostRequest mpgReq = createHttpsPostRequest(refund);
        mpgReq.send();
        Receipt receipt = mpgReq.getReceipt();
        log.info("[PAYMENT]: Refund operation. OrderId: " + orderId + ". Amount: " + stringAmount);
        String transactionId = receipt.getTxnNumber();
        if (receipt.getResponseCode().equals("null") || Integer.parseInt(receipt.getResponseCode()) > 49 || transactionId.equals("null")) {
            log.error("[PAYMENT ERROR]: Refund operation error: Code: " + receipt.getResponseCode() + "; Message: " + receipt.getMessage());
            throw new PaymentException(receipt.getMessage());
        }
    }

    /**
     * @param card
     * @throws PaymentException
     * To credit card, expiry date and any additional details
     * (such as the Card Verification Digits or Address Verification details).
     * It does not verify the available amount.
     */
    @Override
    public void verifyBillingCard(BillingCard card) throws PaymentException {
        ResCardVerificationCC resCardVerificationCC = new ResCardVerificationCC();
        resCardVerificationCC.setOrderId(card.getLast4() + System.currentTimeMillis());
        resCardVerificationCC.setDataKey(card.getId());
        resCardVerificationCC.setCryptType(eCommerceIndicator);
        HttpsPostRequest mpgReq = createHttpsPostRequest(resCardVerificationCC);
        mpgReq.send();
        Receipt receipt = mpgReq.getReceipt();
        log.info("[PAYMENT]: Verification operation.");
        String transactionId = receipt.getTxnNumber();
        if (receipt.getResponseCode().equals("null") || Integer.parseInt(receipt.getResponseCode()) > 49 || transactionId.equals("null")) {
            log.error("[PAYMENT ERROR]: Verification operation error. Code: " + receipt.getResponseCode() + "; Message: " + receipt.getMessage());
            throw new PaymentException(receipt.getMessage());
        }
        card.setBrand(receipt.getCardType());
    }

    /**
     * @param transaction
     * @return HttpsPostRequest
     * To send http request to Moneris for operations related to payment
     */
    private HttpsPostRequest createHttpsPostRequest(Transaction transaction) {
        HttpsPostRequest mpgReq = new HttpsPostRequest();
        mpgReq.setProcCountryCode(countryCode);
        mpgReq.setTestMode(testMode);
        mpgReq.setStoreId(storeId);
        mpgReq.setApiToken(apiToken);
        mpgReq.setTransaction(transaction);
        mpgReq.setStatusCheck(false);
        return mpgReq;
    }

    /**
     * @param receipt
     * @param dto
     * @return BillingCard
     * To build billing card model with card information of the patient
     */
    private BillingCard buildBillingCard(Receipt receipt, BillingCardDto dto) {
        return BillingCard.builder()
                .id(receipt.getDataKey())
                .isValid(true)
                .last4(receipt.getResMaskedPan().substring(7))
                .addressLine1(dto.getAddressLine1())
                .addressLine2(dto.getAddressLine2())
                .addressCity(dto.getAddressCity())
                .addressProvince(
                        Province.valueOf(dto.getAddressProvince()))
                .addressPostalCode(dto.getAddressPostalCode())
                .addressCountry(dto.getAddressCountry())
                .brand(receipt.getCardType())
                .expYear(Long.valueOf("20" + receipt.getResExpDate().substring(0, 2)))
                .expMonth(Long.valueOf(receipt.getResExpDate().substring(2)))
                .build();
    }
}
