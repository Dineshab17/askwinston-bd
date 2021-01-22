package com.askwinston.order;

import com.askwinston.exception.PaymentException;
import com.askwinston.model.BillingCard;
import com.askwinston.model.Courier;
import com.askwinston.model.NotificationModel;
import com.askwinston.model.PurchaseOrder;
import com.askwinston.notification.NotificationEngine;
import com.askwinston.notification.NotificationEventTypeContainer;
import com.askwinston.notification.mapper.EntityModelMapper;
import com.askwinston.repository.PurchaseOrderRepository;
import com.askwinston.service.PaymentService;
import com.askwinston.subscription.ProductSubscription;
import com.askwinston.subscription.ProductSubscriptionRepository;
import com.askwinston.subscription.SubscriptionEngine;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.openhtmltopdf.slf4j.Slf4jLogger;
import com.openhtmltopdf.svgsupport.BatikSVGDrawer;
import com.openhtmltopdf.util.XRLog;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/*
    Class containing logic related to orders
    - update status
    - pause (by patient / because of payment failure)
    - resume (by patient / by admin)
    - generate payment ID
 */

@Slf4j
@Service
public class OrderEngine {


    private PurchaseOrderRepository purchaseOrderRepository;
    private PaymentService paymentService;
    private NotificationEngine notificationEngine;
    private SubscriptionEngine subscriptionEngine;
    private ProductSubscriptionRepository subscriptionRepository;
    private Random random;

    @Autowired
    public void setSubscriptionEngine(SubscriptionEngine subscriptionEngine) {
        this.subscriptionEngine = subscriptionEngine;
    }

    public OrderEngine(PurchaseOrderRepository purchaseOrderRepository,
                       PaymentService paymentService,
                       NotificationEngine notificationEngine,
                       ProductSubscriptionRepository subscriptionRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.paymentService = paymentService;
        this.notificationEngine = notificationEngine;
        this.subscriptionRepository = subscriptionRepository;
        random = new Random();
    }

    /**
     * @param userId
     * @return List<PurchaseOrder>
     * To get all the purchase orders of the patient
     */
    public List<PurchaseOrder> getAll(Long userId) {
        log.info("Getting all the purchase order of the patient with id: {}", userId);
        return purchaseOrderRepository.findByUserId(userId);
    }

    /**
     * @param id
     * @return PurchaseOrder
     * To get Purchase order with id
     */
    public PurchaseOrder getById(Long id) {
        return purchaseOrderRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found")
        );
    }

    /**
     * @param status
     * @return List<PurchaseOrder>
     * To get purchase order by status of order
     */
    public List<PurchaseOrder> getOrdersByStatus(PurchaseOrder.Status status) {
        return purchaseOrderRepository.findByStatus(status);
    }

    /**
     * @param orderId
     * To mark the order as rejected
     */
    public void rejectOrder(Long orderId) {
        PurchaseOrder order = this.getById(orderId);
        try {
            paymentService.capturePayment(getOrderPaymentId(order), order.getTransactionId(), 0L);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        updateStatusAndSave(order, PurchaseOrder.Status.REJECTED);
    }

    /**
     * @param orderId
     * @param status
     * To update the status of the order
     */
    public void updateOrderStatus(Long orderId, PurchaseOrder.Status status) {
        PurchaseOrder order = this.getById(orderId);
        updateStatusAndSave(order, status);
    }

    /**
     * @param order
     * @param courier
     * @param trackingNumber
     * To set the status of the order as delivering with tracking number and courier service information
     */
    public void deliveringOrder(PurchaseOrder order, Courier courier, String trackingNumber) {
        order.setShippingTrackingNumber(trackingNumber);
        order.setCourier(courier);
        updateStatusAndSave(order, PurchaseOrder.Status.DELIVERING);
    }

    /**
     * @param order
     * To update order status as paused
     */
    public void pauseOrderByPatient(PurchaseOrder order) {
        updateStatusAndSave(order, PurchaseOrder.Status.PAUSED_BY_PATIENT);
    }

    /**
     * @param order
     * To update the order status as in progress
     */
    public void resumeOrderPausedByPatient(PurchaseOrder order) {
        updateStatusAndSave(order, PurchaseOrder.Status.IN_PROGRESS);
    }

    /**
     * @param order
     * To set the status of the order as paused
     */
    public void pauseOrder(PurchaseOrder order) {
        String transactionId = order.getTransactionId();
        order.setTransactionId(null);
        if (order.getStatus().equals(PurchaseOrder.Status.WAITING_PHARMACY_RX_CHECK)) {
            updateStatusAndSave(order, PurchaseOrder.Status.PAUSED_RX_TRANSFER);
        } else {
            updateStatusAndSave(order, PurchaseOrder.Status.PAUSED);
        }
        if (transactionId != null) {
            try {
                paymentService.capturePayment(getOrderPaymentId(order), transactionId, 0L);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    /**
     * @param order
     * To resume the paused order
     * and update the status of the order based on the previous state
     * and pre authorize the payment
     */
    public void resumeOrder(PurchaseOrder order) {
        if (order.getStatus().equals(PurchaseOrder.Status.PAUSED_RX_TRANSFER)) {
            log.info("Updating the order with id {} status as {}", order.getId(), PurchaseOrder.Status.WAITING_PHARMACY_RX_CHECK);
            updateStatusAndSave(order, PurchaseOrder.Status.WAITING_PHARMACY_RX_CHECK);
        } else {
            log.info("Updating the order with id {} status as {}", order.getId(), PurchaseOrder.Status.WAITING_PHARMACIST);
            updateStatusAndSave(order, PurchaseOrder.Status.WAITING_PHARMACIST);
        }
        ProductSubscription subscription = order.getSubscription();
        order.setSalt(random.nextInt(123456789));
        order = purchaseOrderRepository.save(order);
        String transactionId = null;
        try {
            fillOrderBillingInfo(order);
            transactionId = paymentService.lockAmount(getOrderPaymentId(order), subscription.getBillingCard(), order.getOrderPrice());
        } catch (PaymentException e) {
            subscriptionEngine.pauseSubscription(subscription, e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        order.setTransactionId(transactionId);
        order = purchaseOrderRepository.save(order);
        notificationEngine.notify(NotificationEventTypeContainer.ORDER_REFILL, order);
    }

    /**
     * @param order
     * @throws PaymentException
     * To update billing information like billing card details and billing address details into the order details
     */
    public void fillOrderBillingInfo(PurchaseOrder order) throws PaymentException {
        if (order.getSubscription().getBillingCard() == null || !order.getSubscription().getBillingCard().getIsValid()) {
            throw new PaymentException("User have no valid billing card");
        } else {
            BillingCard billingCard = order.getSubscription().getBillingCard();

            order.setBillingAddressLine1(billingCard.getAddressLine1());
            order.setBillingAddressLine2(billingCard.getAddressLine2());
            order.setBillingAddressCity(billingCard.getAddressCity());
            order.setBillingAddressProvince(billingCard.getAddressProvince());
            order.setBillingAddressPostalCode(billingCard.getAddressPostalCode());
            order.setBillingAddressCountry(billingCard.getAddressCountry());
            order.setBillingInfo("**** **** **** " + billingCard.getLast4());
        }
    }

    /**
     * @param order
     * @param newStatus
     * To update the status of the purchase order
     */
    protected void updateStatusAndSave(PurchaseOrder order, PurchaseOrder.Status newStatus) {
        order.setStatus(newStatus);
        if (newStatus.equals(PurchaseOrder.Status.DELIVERING)) {
            ProductSubscription subscription = order.getSubscription();
            order.setShippingDate(LocalDate.now());
            if (subscription.getPrescription().getRefillsLeft() > 0) {
                subscriptionEngine.createOrderToSubscription(order.getSubscription());
            }
            if (subscription.getStatus().equals(ProductSubscription.Status.COMPLETED)) {
                subscription.setStatus(ProductSubscription.Status.FINISHED);
                subscriptionRepository.save(subscription);
            }
            notificationEngine.notify(NotificationEventTypeContainer.ORDER_CHECKOUT, order);
        }
        if (newStatus.equals(PurchaseOrder.Status.DELIVERING)) {
            notificationEngine.notify(NotificationEventTypeContainer.RECEIPT, order);
        }
        purchaseOrderRepository.save(order);
    }

    /**
     * @param orderId
     * @return byte[]
     * To generate prescription for the order as pdf
     */
    @SneakyThrows
    public byte[] generatePrescriptionPdf(long orderId) {
        try {
            PurchaseOrder order = getById(orderId);

            XRLog.setLoggingEnabled(true);
            XRLog.setLoggerImpl(new Slf4jLogger());

            ClassPathResource resource = new ClassPathResource("rx/receipt.html");

            String template = StreamUtils.copyToString(resource.getInputStream(), Charset.defaultCharset());
            String rxInHtml = substituteOrderValuesForPrescriptionPdf(template, order);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();

            builder.useSVGDrawer(new BatikSVGDrawer());
            builder.withHtmlContent(rxInHtml, "");
            builder.toStream(baos);
            builder.run();

            return baos.toByteArray();
        } catch (IOException e) {
            log.error("PDF generation failed", e);
            throw new IOException(e);
        }
    }

    /**
     * @param template
     * @param order
     * @return String
     * Substitute the order values into prescription template
     */
    private String substituteOrderValuesForPrescriptionPdf(String template, PurchaseOrder order) {
        EntityModelMapper<PurchaseOrder> mapper = notificationEngine.getEntityMapper(PurchaseOrder.class);
        NotificationModel model = new NotificationModel();
        mapper.doMap(order, model, new HashMap<>());
        for (Map.Entry<String, String> entry : model.getMap().entrySet()) {
            template = template.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return template;

    }

    /**
     * @param order
     * @return String
     * To generate order payment id
     */
    public String getOrderPaymentId(PurchaseOrder order) {
        BigDecimal bdOrderId = new BigDecimal(order.getOrderPrice()).add(new BigDecimal(order.getSalt()));
        bdOrderId = bdOrderId.multiply(new BigDecimal(15556 * 322));
        return order.getNumber() + order.getSubNumber() + bdOrderId.toString();
    }

}
