package com.askwinston.subscription;

import com.askwinston.exception.PaymentException;
import com.askwinston.exception.ShoppingCartException;
import com.askwinston.model.*;
import com.askwinston.notification.NotificationEngine;
import com.askwinston.notification.NotificationEventTypeContainer;
import com.askwinston.order.OrderEngine;
import com.askwinston.repository.*;
import com.askwinston.service.PaymentService;
import com.askwinston.service.PromoCodeService;
import com.askwinston.service.RxTransferService;
import com.askwinston.service.UserService;
import com.askwinston.web.dto.RxTransferSubscriptionDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static com.askwinston.notification.NotificationEventTypeContainer.PHARMACIST_APPROVED_RX;
import static java.time.LocalDate.now;

/*
    Class containing all the logic related to subscriptions
    - create
    - transfer
    - pause (by patient / because of payment failure)
    - resume (by patient / by admin)
    - process
 */

@Slf4j
@Service
@EnableScheduling
public class SubscriptionEngine {

    private static final int DELIVERY_DAYS = 7; //Order is being passed to the pharmacy seven days before the next refill date
    private static final int DAYS_BEFORE_ORDER = 3; //Refill reminder is being sent three days before the next order date

    private PurchaseOrderRepository purchaseOrderRepository;
    private ProductRepository productRepository;
    private ProductQuantityRepository productQuantityRepository;
    private UserRepository userRepository;
    private PaymentService paymentService;
    private UserService userService;
    private MdPostConsultNoteRepository mdPostConsultNoteRepository;
    private PrescriptionRepository prescriptionRepository;
    private PromoCodeService promoCodeService;
    private EntityManager entityManager;
    private CartRepository cartRepository;
    private NotificationEngine notificationEngine;
    private ProductSubscriptionRepository subscriptionRepository;
    private ProductSubscriptionItemRepository productSubscriptionItemRepository;
    private CartItemRepository cartItemRepository;
    private OrderEngine orderEngine;
    private DocumentRepository documentRepository;
    private RxTransferService rxTransferService;
    private Random random;

    private static final String PRODUCT_SUBSCRIPTION_TABLE_NAME = "product_subscription";
    private static final String NO_REFILLS_LEFT_MESSAGE = "No refills left";

    @Autowired
    public void setOrderEngine(OrderEngine orderEngine) {
        this.orderEngine = orderEngine;
    }

    public SubscriptionEngine(PurchaseOrderRepository purchaseOrderRepository,
                              ProductRepository productRepository,
                              ProductQuantityRepository productQuantityRepository,
                              UserRepository userRepository,
                              PaymentService paymentService,
                              UserService userService,
                              NotificationEngine notificationEngine,
                              MdPostConsultNoteRepository mdPostConsultNoteRepository,
                              PrescriptionRepository prescriptionRepository,
                              PromoCodeService promoCodeService,
                              EntityManager entityManager,
                              CartRepository cartRepository,
                              ProductSubscriptionRepository subscriptionRepository,
                              ProductSubscriptionItemRepository productSubscriptionItemRepository,
                              CartItemRepository cartItemRepository,
                              DocumentRepository documentRepository,
                              RxTransferService rxTransferService) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.productRepository = productRepository;
        this.productQuantityRepository = productQuantityRepository;
        this.userRepository = userRepository;
        this.paymentService = paymentService;
        this.notificationEngine = notificationEngine;
        this.userService = userService;
        this.mdPostConsultNoteRepository = mdPostConsultNoteRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.promoCodeService = promoCodeService;
        this.entityManager = entityManager;
        this.cartRepository = cartRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.productSubscriptionItemRepository = productSubscriptionItemRepository;
        this.cartItemRepository = cartItemRepository;
        this.documentRepository = documentRepository;
        this.rxTransferService = rxTransferService;
        random = new Random();
    }

    /**
     * Scheduled job to check next order date of the subscriptions
     * and perform the order
     */
    @Scheduled(cron = "${notification.cron:0 0 12 * * *}")
    public void checkSubscriptions() {
        List<ProductSubscription> subscriptions = subscriptionRepository.findAllByStatusIn(ProductSubscription.Status.ACTIVE, ProductSubscription.Status.PAUSED, ProductSubscription.Status.PAUSED_BY_PATIENT);
        subscriptions.stream()
                .filter(s -> s.getStatus().equals(ProductSubscription.Status.ACTIVE)
                        || s.getStatus().equals(ProductSubscription.Status.PAUSED)
                        || s.getStatus().equals(ProductSubscription.Status.PAUSED_BY_PATIENT))
                .forEach(this::checkPrescriptionDate);
        subscriptions = subscriptions.stream()
                .filter(s -> s.getStatus().equals(ProductSubscription.Status.ACTIVE))
                .collect(Collectors.toList());
        subscriptions.stream().filter(s -> now().isAfter(s.getNextOrderDate().minusDays(DELIVERY_DAYS))).forEach(this::processSubscription);
        subscriptions.stream().filter(s -> now().equals(s.getNextOrderDate().minusDays(DELIVERY_DAYS).minusDays(DAYS_BEFORE_ORDER))).forEach(this::sendSoonRefillNotification);
        log.info("Check subscriptions performed");
    }

    /**
     * @param subscription
     * To send notification to the user before the refill date
     */
    private void sendSoonRefillNotification(ProductSubscription subscription) {
        if (subscription.getStatus().equals(ProductSubscription.Status.ACTIVE)) {
            notificationEngine.notify(NotificationEventTypeContainer.REFILL_SOON_REMINDER, subscription);
            log.info("Refill soon remainder notification sent to the user with id {}", subscription.getUser().getId());
        }
    }

    /**
     * @param subscription
     * To check whether the prescription date is expired
     * and if its' expired update the status of subscription as finished
     */
    private void checkPrescriptionDate(ProductSubscription subscription) {
        LocalDate prescriptionToDate = convertToLocalDateViaInstant(subscription.getPrescription().getToDate());
        if (prescriptionToDate.isBefore(now())) {
            subscription.setFinishNotes("Prescription is expired");
            updateStatusAndSave(subscription, ProductSubscription.Status.FINISHED);
        }
    }

    /**
     * @param subscription
     * To process the subscription
     */
    protected void processSubscription(ProductSubscription subscription) {
        processSubscription(subscription, false);
    }

    /**
     * @param subscription
     * @param earlyRefill
     * To check the subscription status
     * and process the subscription, if the subscription is active
     * set the status as completed, if the there is no refill left to process
     * and send the notification to the user, if the refill left date is sooner
     */
    protected void processSubscription(ProductSubscription subscription, boolean earlyRefill) {
        checkPrescriptionDate(subscription);
        if (subscription.getStatus().equals(ProductSubscription.Status.ACTIVE)) {
            sendOrderToPharmacy(subscription.getOrders().stream()
                    .filter(order -> order.getStatus().equals(PurchaseOrder.Status.IN_PROGRESS))
                    .findAny()
                    .orElseGet(() -> createOrderToSubscription(subscription)));
            ProductSubscription updatedSubscription = getById(subscription.getId());
            Prescription prescription = updatedSubscription.getPrescription();
            if (!subscription.getStatus().equals(ProductSubscription.Status.PAUSED) && prescription.getRefillsLeft() == 0) {
                updatedSubscription.setStatus(ProductSubscription.Status.COMPLETED);
                updatedSubscription.setFinishNotes(NO_REFILLS_LEFT_MESSAGE);
                subscriptionRepository.save(updatedSubscription);
                log.info("Subscription with id {} has been completed", updatedSubscription.getId());
                return;
            }
            updatedSubscription.setNextOrderDate(now().plusMonths(updatedSubscription.getPeriod()).plusDays(DELIVERY_DAYS));
            subscriptionRepository.save(updatedSubscription);

            if (earlyRefill) {
                notificationEngine.notify(NotificationEventTypeContainer.EARLY_REFILL_SUBMITTED, updatedSubscription);
            }
        } else {
            log.error("Subscription with id {} is inactive", subscription.getId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Subscription " + subscription.getId() + " inactive");
        }
    }

    public LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    /**
     * @param patientId
     * @param promoCodeString
     * @return List<ProductSubscription>
     * To checkout product items from cart and
     * add those product items in subscription
     * and remove from patient's cart repo
     */
    @Transactional
    public List<ProductSubscription> checkoutCart(Long patientId, String promoCodeString, String utmSource, boolean isExpressShipping) {
        PromoCode promoCode = null;
        if (promoCodeString != null && !promoCodeString.isEmpty()) {
            promoCode = promoCodeService.getByCode(promoCodeString);
        }
        User patient = userRepository.findById(patientId).orElseThrow(
                () -> new ShoppingCartException("User not found")
        );
        List<CartItem> cartItems = patient.getCart().getItems();
        if (cartItems.isEmpty())
            throw new ShoppingCartException("Cart is empty");
        List<ProductSubscription> subscriptions = new ArrayList<>();
        PromoCode finalPromoCode = promoCode;
        cartItems.forEach(cartItem -> subscriptions.add(createSubscription(patient, cartItem, finalPromoCode, utmSource, isExpressShipping)));
        cartItemRepository.deleteAll(cartItems);
        patient.getCart().getItems().clear();
        cartRepository.save(patient.getCart());
        return subscriptions;
    }

    /**
     * @param patient
     * @param cartItem
     * @param promoCode
     * @return ProductSubscription
     * To create new subscription for the patient from the cart items
     */
    protected ProductSubscription createSubscription(User patient, CartItem cartItem, PromoCode promoCode, String utmSource, boolean isExpressShipping) {
        ProductSubscription subscription = new ProductSubscription();
        subscription.setCreationDate(LocalDateTime.now(ZoneId.of("Canada/Eastern")));
        ProductSubscriptionItem productSubscriptionItem;
        subscription.setUser(patient);
        subscription.setUtmSource(utmSource);
        subscription.setDoctor(userService.getDefaultDoctor());
        subscription.setStatus(ProductSubscription.Status.WAITING_DOCTOR);
        subscription.setItems(new ArrayList<>());
        Product product = productRepository.findById(cartItem.getProductId()).orElseThrow(
                () -> new ShoppingCartException("Product not found")
        );
        ProductQuantity productQuantity = productQuantityRepository.findById(cartItem.getProductQuantityId()).orElseThrow(
                () -> new ShoppingCartException("Product quantity not found")
        );
        subscription.setPeriod(productQuantity.getSupply().getRefillPeriod());
        subscription.setTotalRefills(productQuantity.getSupply().getTotalRefills());
        productSubscriptionItem = ProductSubscriptionItem.builder()
                .productCount(cartItem.getProductCount())
                .productPrice(productQuantity.getPrice())
                .subscription(subscription)
                .productDosage(productQuantity.getDosage())
                .productName(product.getName())
                .build();
        long cartPrice = productQuantity.getPrice() * cartItem.getProductCount();
        // Calc discount
        long discount = 0;
        if (promoCode != null) {
            if (promoCode.getType().equals(PromoCode.Type.AMOUNT)) {
                discount = promoCode.getValue();
            } else if (promoCode.getType().equals(PromoCode.Type.PERCENT)) {
                discount = cartPrice * promoCode.getValue() / 100;
            } else {
                if (promoCode.getProblemCategory().contains(product.getProblemCategory())) {
                    subscription.setPromoCode(promoCode);
                }
            }
        }


        subscription.setExpressShipping(isExpressShipping);
        subscription.setDiscount(discount);
        subscription.setOrderPrice(cartPrice);

        productSubscriptionItemRepository.save(productSubscriptionItem);
        subscription.getItems().add(productSubscriptionItem);
        userRepository.save(patient);
        subscriptionRepository.save(subscription);
        notificationEngine.notify(NotificationEventTypeContainer.NEW_SUBSCRIPTION, subscription);
        return subscription;
    }

    /**
     * @param patientId
     * @param dto
     * @return ProductSubscription
     * To create product subscriptions with Prescription number
     */
    public ProductSubscription createSubscriptionWithRxNumber(Long patientId, RxTransferSubscriptionDto dto) {
        PromoCode promoCode = null;
        if (dto.getPromoCode() != null && !dto.getPromoCode().isEmpty()) {
            promoCode = promoCodeService.getByCode(dto.getPromoCode());
        }
        User patient = userRepository.findById(patientId).orElseThrow(
                () -> new ShoppingCartException("User not found")
        );
        ProductSubscription subscription = new ProductSubscription();
        subscription.setCreationDate(LocalDateTime.now(ZoneId.of("Canada/Eastern")));
        ProductSubscriptionItem productSubscriptionItem;
        subscription.setUser(patient);
        subscription.setDoctor(userService.getDefaultDoctor());
        subscription.setItems(new ArrayList<>());

        Product product = productRepository.findById(dto.getProductId()).orElseThrow(
                () -> new ShoppingCartException("Product not found")
        );
        ProductQuantity productQuantity = productQuantityRepository.findById(dto.getProductQuantityId()).orElseThrow(
                () -> new ShoppingCartException("Product quantity not found")
        );
        subscription.setPeriod(productQuantity.getSupply().getRefillPeriod());
        subscription.setTotalRefills(productQuantity.getSupply().getTotalRefills());
        productSubscriptionItem = ProductSubscriptionItem.builder()
                .productCount(1)
                .productPrice(productQuantity.getPrice())
                .subscription(subscription)
                .productDosage(productQuantity.getDosage())
                .productName(product.getName())
                .build();
        long cartPrice = productQuantity.getPrice();
        // Calc discount
        long discount = 0;
        if (promoCode != null) {
            if (promoCode.getType().equals(PromoCode.Type.AMOUNT)) {
                discount = promoCode.getValue();
            } else {
                discount = cartPrice * promoCode.getValue() / 100;
            }
        }
        subscription.setDiscount(discount);
        subscription.setOrderPrice(cartPrice);
        productSubscriptionItemRepository.save(productSubscriptionItem);
        subscription.getItems().add(productSubscriptionItem);
        userRepository.save(patient);
        subscription = transferPrescription(subscription, dto.getRxDocumentId(), dto.getRxNumber(), dto.getPharmacyNameAndAddress(), dto.getPharmacyPhone());

        notificationEngine.notify(NotificationEventTypeContainer.NEW_SUBSCRIPTION, subscription);
        notificationEngine.notify(NotificationEventTypeContainer.TRANSFER_RX_SUBMITTED, subscription);

        sendTransferRxOrderToPharmacy(createOrderToSubscription(subscription));

        return subscriptionRepository.save(subscription);
    }

    /**
     * @param subscription
     * @param rxDocumentId
     * @param rxNumber
     * @param pharmacyNameAndAddress
     * @param pharmacyPhone
     * @return ProductSubscription
     * to generate the prescription for the user
     */
    protected ProductSubscription transferPrescription(ProductSubscription subscription, Long rxDocumentId, String rxNumber, String pharmacyNameAndAddress,
                                                       String pharmacyPhone) {
        Date nowDate = Date.from(Instant.now());
        Document rxDocument = documentRepository.findById(rxDocumentId).orElseThrow(
                () -> new ShoppingCartException("Document with id " + rxDocumentId + " not found")
        );
        Prescription prescription = Prescription.builder()
                .date(nowDate)
                .rxDocument(rxDocument)
                .rxTransferNumber(rxNumber)
                .pharmacyNameAndAddress(pharmacyNameAndAddress)
                .pharmacyPhone(pharmacyPhone)
                .refills(subscription.getTotalRefills())
                .build();
        Prescription savedPrescription = prescriptionRepository.save(prescription);
        log.info("Prescription has been generated for the user with id {}", subscription.getUser().getId());
        rxTransferService.deleteRxTransferStateByUser(subscription.getUser().getId());
        subscription.setPrescription(savedPrescription);
        subscription.setStatus(ProductSubscription.Status.WAITING_PHARMACY_RX_CHECK);
        return subscriptionRepository.save(subscription);
    }

    /**
     * @param subscription
     * @return PurchaseOrder
     * To create new order for the subscription
     */
    public PurchaseOrder createOrderToSubscription(ProductSubscription subscription) {
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setSubscription(subscription);
        List<PurchaseOrderItem> orderItems = new ArrayList<>();
        ProductSubscriptionItem existingItem = subscription.getItems().get(0);
        orderItems.add(PurchaseOrderItem.builder()
                .productCount(existingItem.getProductCount())
                .productPrice(existingItem.getProductPrice())
                .productDosage(existingItem.getProductDosage())
                .purchaseOrder(purchaseOrder)
                .productName(existingItem.getProductName())
                .build());
        long cartPrice = subscription.getOrderPrice();

        purchaseOrder.setSalt(random.nextInt(123456789));
        purchaseOrder.setItems(orderItems);
        purchaseOrder.setUser(subscription.getUser());
        purchaseOrder.setDoctor(subscription.getDoctor());
        purchaseOrder.setDate(Date.from(Instant.now()));
        ShippingAddress shippingAddress = subscription.getShippingAddress();
        purchaseOrder.setShippingAddressLine1(shippingAddress.getAddressLine1());
        purchaseOrder.setShippingAddressLine2(shippingAddress.getAddressLine2());
        purchaseOrder.setShippingAddressCity(shippingAddress.getAddressCity());
        purchaseOrder.setShippingAddressProvince(shippingAddress.getAddressProvince());
        purchaseOrder.setShippingAddressPostalCode(shippingAddress.getAddressPostalCode());
        purchaseOrder.setShippingAddressCountry(shippingAddress.getAddressCountry());

        PromoCode promoCode = subscription.getPromoCode();
        if (promoCode != null && promoCode.getType().equals(PromoCode.Type.GIFT)) {
            purchaseOrder.setPromoCode(promoCode.getCode());
            subscription.setPromoCode(null);
        }

        purchaseOrder.setCartPrice(cartPrice);
        // Free shipping
        purchaseOrder.setShippingPrice(0);

        //Express Shipping
        if(subscription.isExpressShipping()){
            purchaseOrder.setShippingPrice(999);
        }

        // No taxes
        purchaseOrder.setTaxes(0);
        purchaseOrder.setPrescription(subscription.getPrescription());
        purchaseOrder.setStatus(PurchaseOrder.Status.IN_PROGRESS);

        purchaseOrder.setDiscount(subscription.getDiscount() == null ? 0L : subscription.getDiscount());
        purchaseOrder.setOrderPrice(
                purchaseOrder.getCartPrice() + purchaseOrder.getShippingPrice()
                        + purchaseOrder.getTaxes() - purchaseOrder.getDiscount());
        if (subscription.getDiscount() == null || subscription.getDiscount() != 0L) {
            subscription.setDiscount(0L);
        }
        purchaseOrder.setNumber(subscription.getId());
        purchaseOrder.setSubNumber(subscription.getLastOrderSubNumber());
        subscriptionRepository.save(subscription);

        log.info("Subscription with id {} has been updated with new purchase order for the user with id {}",
                subscription.getId(), subscription.getUser().getId());
        return purchaseOrderRepository.save(purchaseOrder);

    }

    /**
     * @param purchaseOrder
     * Perform payment or lock amount for the purhcase order
     */
    private void performPayment(PurchaseOrder purchaseOrder) {
        ProductSubscription subscription = purchaseOrder.getSubscription();
        // Checkout in Moneris
        String transactionId = null;
        try {
            purchaseOrder = purchaseOrderRepository.save(purchaseOrder);
            orderEngine.fillOrderBillingInfo(purchaseOrder);
            transactionId = paymentService.lockAmount(orderEngine.getOrderPaymentId(purchaseOrder), subscription.getBillingCard(), purchaseOrder.getOrderPrice());
            log.info("Amount has been locked for the order: {}, for the user: {}", purchaseOrder.getId(), purchaseOrder.getUser().getId());
            purchaseOrder.setTransactionId(transactionId);
            purchaseOrder = purchaseOrderRepository.save(purchaseOrder);
            subscription.getOrders().add(purchaseOrder);
            notificationEngine.notify(NotificationEventTypeContainer.ORDER_REFILL, purchaseOrder);
            subscriptionRepository.save(subscription);
        } catch (PaymentException e) {
            purchaseOrder = purchaseOrderRepository.save(purchaseOrder);
            subscription.getOrders().add(purchaseOrder);
            subscription = subscriptionRepository.save(subscription);
            pauseSubscription(subscription, e.getMessage());
            log.warn(e.getMessage());
        }
    }

    /**
     * @param purchaseOrder
     * @return PurchaseOrder
     * To send order to pharmacy (set status of the purchase order to waiting for pharmacist)
     * and update the prescription with left refill value
     */
    public PurchaseOrder sendOrderToPharmacy(PurchaseOrder purchaseOrder) {
        ProductSubscription subscription = purchaseOrder.getSubscription();

        Prescription prescription = subscription.getPrescription();
        prescription.setRefillsLeft(prescription.getRefillsLeft() - 1);
        prescriptionRepository.save(prescription);
        purchaseOrder.setStatus(PurchaseOrder.Status.WAITING_PHARMACIST);
        purchaseOrderRepository.save(purchaseOrder);
        performPayment(purchaseOrder);
        return orderEngine.getById(purchaseOrder.getId());
    }

    /**
     * @param purchaseOrder
     * @return PurchaseOrder
     * To set status of the order to waiting to pharmacy rx check
     */
    @Transactional
    public PurchaseOrder sendTransferRxOrderToPharmacy(PurchaseOrder purchaseOrder) {
        purchaseOrder.setStatus(PurchaseOrder.Status.WAITING_PHARMACY_RX_CHECK);
        performPayment(purchaseOrder);
        return purchaseOrderRepository.save(purchaseOrder);
    }

    /**
     * @param purchaseOrder
     * @param amount
     * @throws PaymentException
     * To validate the amount
     * and to validate the billing card information
     * and to check whether the payment done
     * and set the status of the order as packaging
     * If the billing card is not valid or error in payment, pause the order
     */
    @Transactional
    public void performOrder(PurchaseOrder purchaseOrder, long amount) throws PaymentException {
        if (amount > purchaseOrder.getOrderPrice()) {
            log.error("Provider amount is greater than order total amount for order with id {}", purchaseOrder.getId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount cannot be greater than order total price");
        }
        try {
            if (purchaseOrder.getSubscription().getBillingCard() == null || !purchaseOrder.getSubscription().getBillingCard().getIsValid()) {
                log.error("Billing card information in not valid for the order with id {}", purchaseOrder.getId());
                throw new PaymentException("User have no valid billing card");
            }
            purchaseOrder.setTransactionId(paymentService.capturePayment(orderEngine.getOrderPaymentId(purchaseOrder), purchaseOrder.getTransactionId(), amount));
            purchaseOrder.setCoPay(amount);
            orderEngine.updateOrderStatus(purchaseOrder.getId(), PurchaseOrder.Status.PACKAGING);
            log.info("Purchase order with id {} is ready for packaging", purchaseOrder.getId());
        } catch (Exception e) {
            pauseSubscription(purchaseOrder.getSubscription(), e.getMessage());
            log.error("[ERROR] Order " + purchaseOrder.getNumber() + "-" + purchaseOrder.getSubNumber() + " failed. " + e.getMessage());
            throw new PaymentException("User have no valid billing card");
        }
    }

    /**
     * @param purchaseOrder
     * @param amount
     * @param refillsLeft
     * @param toDate
     * @throws PaymentException
     * To set the status of the product subscription as completed with complete message
     */
    @Transactional
    public void performOrderWithRxTransfer(PurchaseOrder purchaseOrder, long amount, int refillsLeft, LocalDate toDate) throws PaymentException {
        ProductSubscription subscription = purchaseOrder.getSubscription();
        Prescription prescription = subscription.getPrescription();
        prescription.setRefillsLeft(refillsLeft);
        prescription.setToDate(java.sql.Date.valueOf(toDate));
        if (prescription.getRefillsLeft() == 0) {
            subscription.setStatus(ProductSubscription.Status.COMPLETED);
            subscription.setFinishNotes(NO_REFILLS_LEFT_MESSAGE);
            subscriptionRepository.save(subscription);
        }
        notificationEngine.notify(PHARMACIST_APPROVED_RX, purchaseOrder);
        performOrder(purchaseOrder, amount);
    }

    /**
     * @param subscription
     * @param mdPostConsultNote
     * To create prescription after the doctor consultation with doctor notes
     * and send notification of subscription approval to the patient
     */
    @Transactional
    public void createPrescription(ProductSubscription subscription, MdPostConsultNote mdPostConsultNote) {
        mdPostConsultNote.setDoctorsFullName(subscription.getDoctor().getFirstName() + " " + subscription.getDoctor().getLastName());
        mdPostConsultNote.setDoctorsLicense(subscription.getDoctor().getDoctorsLicenseNumber());
        mdPostConsultNote.setDate(Date.from(Instant.now()));
        MdPostConsultNote savedNote = mdPostConsultNoteRepository.save(mdPostConsultNote);

        Date nowDate = Date.from(Instant.now());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(nowDate);
        calendar.add(Calendar.MONTH, Prescription.DEFAULT_MONTHS_VALID);
        Date toDate = calendar.getTime();

        Prescription prescription = Prescription.builder()
                .mdPostConsultNote(savedNote)
                .date(nowDate)
                .toDate(toDate)
                .refills(subscription.getTotalRefills())
                .refillsLeft(subscription.getTotalRefills())
                .build();
        Prescription savedPrescription = prescriptionRepository.save(prescription);

        subscription.setPrescription(savedPrescription);
        subscriptionRepository.save(subscription);
        log.info("Prescription for product subscription with id {} for the user with id {} created", subscription.getId(), subscription.getUser().getId());
        notificationEngine.notify(NotificationEventTypeContainer.SUBSCRIPTION_APPROVAL, subscription);
    }

    /**
     * @param subscriptionId
     * @param status
     * To update the product subscription status
     */
    @Transactional
    public void updateSubscriptionStatus(Long subscriptionId, ProductSubscription.Status status) {
        ProductSubscription subscription = this.getById(subscriptionId);
        updateStatusAndSave(subscription, status);
    }

    /**
     * @param subscription
     * @param pharmacyId
     * To set pharmacy id for the product subscription
     */
    @Transactional
    public void setPharmacyId(ProductSubscription subscription, String pharmacyId) {
        subscription.setPharmacyId(pharmacyId);
        subscriptionRepository.save(subscription);
    }

    public ProductSubscription getById(Long id) {
        return subscriptionRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription not found")
        );
    }

    /**
     * @param subscription
     * @param notes
     * @return ProductSubscription
     * To set pause notes for the subscription
     */
    public ProductSubscription setPauseNotes(ProductSubscription subscription, String notes) {
        subscription.setNotes(notes);
        return subscriptionRepository.save(subscription);
    }

    /**
     * @param subscription
     * @return ProductSubscription
     * To Pause the subscription by the patient (to update the subscription status as paused by patient)
     */
    public ProductSubscription pauseSubscriptionByPatient(ProductSubscription subscription) {
        if (subscription.getOrders().stream().anyMatch(o -> o.getStatus().equals(PurchaseOrder.Status.IN_PROGRESS))) {
            subscription.getOrders().stream()
                    .filter(o -> o.getStatus().equals(PurchaseOrder.Status.IN_PROGRESS))
                    .forEach(o -> orderEngine.pauseOrderByPatient(o));
            subscription.setPauseDate(now());
            updateStatusAndSave(subscription, ProductSubscription.Status.PAUSED_BY_PATIENT);
            notificationEngine.notify(NotificationEventTypeContainer.SUBSCRIPTION_PAUSED_BY_USER, subscription);
            return subscription;
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only subscription with 'In progress' order can be paused.");
        }
    }

    /**
     * @param subscription
     * @return ProductSubscription
     * To Resume the paused product subscription by patient
     */
    public ProductSubscription resumeSubscriptionPausedByPatient(ProductSubscription subscription) {
        subscription.setNotes("");
        subscription.setPauseDate(null);
        subscription.getOrders().stream()
                .filter(o -> o.getStatus().equals(PurchaseOrder.Status.PAUSED_BY_PATIENT))
                .forEach(o -> orderEngine.resumeOrderPausedByPatient(o));
        updateStatusAndSave(subscription, ProductSubscription.Status.ACTIVE);
        if (now().isAfter(subscription.getNextOrderDate().minusDays(DELIVERY_DAYS))) {
            processSubscription(subscription);
        }
        notificationEngine.notify(NotificationEventTypeContainer.SUBSCRIPTION_PAUSED_BY_USER_RESUMED, subscription);
        return subscription;
    }

    /**
     * @param subscription
     * @param notes
     * @return ProductSubscription
     * To pause the product subscription based on the status of the order
     */
    public ProductSubscription pauseSubscription(ProductSubscription subscription, String notes) {
        subscription.setNotes(notes);
        subscription.setPauseDate(now());
        if (subscription.getStatus().equals(ProductSubscription.Status.WAITING_PHARMACY_RX_CHECK)) {
            updateStatusAndSave(subscription, ProductSubscription.Status.PAUSED_RX_TRANSFER);
            log.info("Product Subscription with id {} is paused with status {}", subscription.getId(),
                    ProductSubscription.Status.PAUSED_RX_TRANSFER);
        } else {
            updateStatusAndSave(subscription, ProductSubscription.Status.PAUSED);
            log.info("Product Subscription with id {} is paused with status {}", subscription.getId(),
                    ProductSubscription.Status.PAUSED);
        }
        Map<String, String> additionalParams = new HashMap<>();
        additionalParams.put("notes", notes);
        notificationEngine.notify(NotificationEventTypeContainer.SUBSCRIPTION_PAUSED, subscription, additionalParams);
        subscription.getOrders().stream()
                .filter(o -> o.getStatus().equals(PurchaseOrder.Status.WAITING_PHARMACIST) || o.getStatus().equals(PurchaseOrder.Status.WAITING_PHARMACY_RX_CHECK))
                .forEach(o -> orderEngine.pauseOrder(o));
        return subscription;
    }

    /**
     * @param subscription
     * @return ProductSubscription
     * To resume the paused product subscription
     * and update the status of the subscription based on the previous subscription status
     */
    public ProductSubscription resumeSubscription(ProductSubscription subscription) {
        subscription.setNotes("");
        subscription.setPauseDate(null);
        if (subscription.getStatus().equals(ProductSubscription.Status.PAUSED_RX_TRANSFER)) {
            updateStatusAndSave(subscription, ProductSubscription.Status.WAITING_PHARMACY_RX_CHECK);
        } else if (subscription.getStatus().equals(ProductSubscription.Status.PAUSED)) {
            updateStatusAndSave(subscription, ProductSubscription.Status.ACTIVE);
        }
        notificationEngine.notify(NotificationEventTypeContainer.SUBSCRIPTION_RESUMED, subscription);
        subscription.getOrders().stream()
                .filter(o -> o.getStatus().equals(PurchaseOrder.Status.PAUSED) || o.getStatus().equals(PurchaseOrder.Status.PAUSED_RX_TRANSFER))
                .forEach(o -> orderEngine.resumeOrder(o));
        log.info("Product subscription with id {} resumed and notification sent to the user with id {}", subscription.getId(), subscription.getUser().getId());
        if (!subscription.getStatus().equals(ProductSubscription.Status.PAUSED)
                && !subscription.getStatus().equals(ProductSubscription.Status.PAUSED_RX_TRANSFER)
                && !subscription.getStatus().equals(ProductSubscription.Status.WAITING_PHARMACY_RX_CHECK)
                && subscription.getPrescription().getRefillsLeft() == 0) {
            subscription.setFinishNotes(NO_REFILLS_LEFT_MESSAGE);
            updateStatusAndSave(subscription, ProductSubscription.Status.COMPLETED);
            log.info("Product Subscription with id {} is completed and no refills left for the user with id {}", subscription.getId(), subscription.getUser().getId());
        }
        return subscription;
    }

    /**
     * @param subscription
     * @return ProductSubscription
     * To Cancel the product subscription
     * and send notification to the user
     */
    public ProductSubscription cancelSubscription(ProductSubscription subscription) {
        updateStatusAndSave(subscription, ProductSubscription.Status.CANCELLED);
        subscription.getOrders().stream()
                .filter(o -> o.getStatus().equals(PurchaseOrder.Status.IN_PROGRESS))
                .forEach(o -> orderEngine.updateOrderStatus(o.getId(), PurchaseOrder.Status.CANCELLED));

        notificationEngine.notify(NotificationEventTypeContainer.SUBSCRIPTION_CANCELED, subscription);
        log.info("Product subscription with id {} has been cancelled and notification sent to the user with id {}",
                subscription.getId(), subscription.getUser().getId());
        return subscription;
    }

    /**
     * @param id
     * @param rejectionNotes
     * @return ProductSubscription
     * To set the status of the purchase order or subscription to rejected
     */
    public ProductSubscription rejectSubscription(Long id, String rejectionNotes) {
        ProductSubscription subscription = this.getById(id);
        subscription.setNotes(rejectionNotes);
        updateStatusAndSave(subscription, ProductSubscription.Status.REJECTED);
        return subscription;
    }

    public void updateSubscriptionsStatus(Long userId, ProductSubscription.Status oldStatus, ProductSubscription.Status newStatus) {
        List<ProductSubscription> subscriptions = subscriptionRepository.findAllByUserAndStatus(userService.getById(userId), oldStatus);
        subscriptions.forEach(subscription -> updateStatusAndSave(subscription, newStatus));
    }

    /**
     * @param id
     * @return ProductSubscription
     * To skip the next refill of the user
     */
    public ProductSubscription skipNextOrder(Long id) {
        ProductSubscription subscription = this.getById(id);
        if (!subscription.getNextOrderDate().plusMonths(1).isAfter(convertToLocalDateViaInstant(subscription.getPrescription().getToDate()))) {
            subscription.setNextOrderDate(subscription.getNextOrderDate().plusMonths(1));
            subscription = subscriptionRepository.save(subscription);
            notificationEngine.notify(NotificationEventTypeContainer.AUTO_REFILL_SKIP, subscription);
        }
        return subscription;
    }

    /**
     * @param subscription
     * @param newStatus
     * To update the status of the product subscription to new given status
     */
    @Transactional
    protected void updateStatusAndSave(ProductSubscription subscription, ProductSubscription.Status newStatus) {
        ProductSubscription.Status oldStatus = subscription.getStatus();
        subscription.setStatus(newStatus);
        if (newStatus.equals(ProductSubscription.Status.ACTIVE)) {
            if (oldStatus.equals(ProductSubscription.Status.PAUSED)) {
                notificationEngine.notify(NotificationEventTypeContainer.SUBSCRIPTION_RESUMED, subscription);
            } else if (oldStatus.equals(ProductSubscription.Status.WAITING_PHARMACY_RX_CHECK)) {
                subscription.setNextOrderDate(now().plusMonths(subscription.getPeriod()).plusDays(DELIVERY_DAYS));
            } else if (!oldStatus.equals(ProductSubscription.Status.PAUSED_BY_PATIENT)) {
                sendOrderToPharmacy(createOrderToSubscription(subscription));
                subscription.setNextOrderDate(now().plusMonths(subscription.getPeriod()).plusDays(DELIVERY_DAYS));
            }
        }
        if (newStatus.equals(ProductSubscription.Status.REJECTED)) {
            String emailRejectionNotes = subscription.getNotes();
            if (!StringUtils.hasText(emailRejectionNotes)) {
                if (oldStatus.equals(ProductSubscription.Status.WAITING_DOCTOR)) {
                    emailRejectionNotes = "Our physician rejected the subscription based on an analysis of your medical history";
                } else {
                    emailRejectionNotes = "";
                }
            }
            Map<String, String> additionalParams = new HashMap<>();
            additionalParams.put("notes", emailRejectionNotes);
            notificationEngine.notify(NotificationEventTypeContainer.SUBSCRIPTION_REJECTION, subscription, additionalParams);
        }
        subscriptionRepository.save(subscription);
    }

    @Transactional
    public void initSubscriptionId(Long initialValue) {
        String sqlString = "SELECT AUTO_INCREMENT FROM information_schema.TABLES WHERE TABLE_NAME = '" +
                PRODUCT_SUBSCRIPTION_TABLE_NAME + "'";
        Query readQuery = entityManager.createNativeQuery(sqlString);
        List<BigInteger> autoIncrements = readQuery.getResultList();
        if (autoIncrements.size() != 1) {
            log.warn("Skipping SubscriptionId initialization: Query [" + sqlString + "] returned [" +
                    autoIncrements.size() + "] results.");
            return;
        }

        long currentAutoIncrement;
        String currentAutoIncrementString = autoIncrements.get(0).toString();
        try {
            currentAutoIncrement = Long.parseLong(currentAutoIncrementString);
        } catch (NumberFormatException e) {
            log.warn("Skipping SubscriptionId initialization: Query [" + sqlString + "] returned [" +
                    currentAutoIncrementString + "] and can'd be parsed as long.");
            return;
        }

        if (currentAutoIncrement < initialValue) {
            Query updateQuery = entityManager.createNativeQuery("ALTER TABLE " +
                    PRODUCT_SUBSCRIPTION_TABLE_NAME + " AUTO_INCREMENT=" + initialValue + ";");
            updateQuery.executeUpdate();
            log.info("SubscriptionId initialization: set AUTO_INCREMENT to [" + initialValue + "]");
        } else {
            log.info("Skipping SubscriptionId initialization: Query [" + sqlString + "] returned [" +
                    currentAutoIncrementString + "] and is already >= " + initialValue);
        }
    }

    public static Integer getDaysBeforeOrder() {
        return DAYS_BEFORE_ORDER;
    }

    public static Integer getDeliveryDays() {
        return DELIVERY_DAYS;
    }
}
