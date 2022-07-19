package com.askwinston.service.impl;

import com.askwinston.model.*;
import com.askwinston.repository.*;
import com.askwinston.service.StatisticsService;
import com.askwinston.subscription.ProductSubscription;
import com.askwinston.subscription.ProductSubscriptionRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StatisticsServiceImpl implements StatisticsService {

    private ProductSubscriptionRepository subscriptionRepository;
    private UserRepository userRepository;
    private StayConnectedRecordRepository stayConnectedRecordRepository;
    private ContactUsRecordRepository contactUsRecordRepository;
    private PurchaseOrderRepository orderRepository;
    private ProductSubscriptionRepository productSubscriptionRepository;
    private PrescriptionRepository prescriptionRepository;
    private PurchaseOrderRepository purchaseOrderRepository;
    private PurchaseOrderItemRepository purchaseOrderItemRepository;

    public StatisticsServiceImpl(ProductSubscriptionRepository subscriptionRepository,
                                 UserRepository userRepository,
                                 StayConnectedRecordRepository stayConnectedRecordRepository,
                                 ContactUsRecordRepository contactUsRecordRepository,
                                 PurchaseOrderRepository orderRepository,
                                 ProductSubscriptionRepository productSubscriptionRepository,
                                 PrescriptionRepository prescriptionRepository,
                                 PurchaseOrderRepository purchaseOrderRepository,
                                 PurchaseOrderItemRepository purchaseOrderItemRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
        this.stayConnectedRecordRepository = stayConnectedRecordRepository;
        this.contactUsRecordRepository = contactUsRecordRepository;
        this.orderRepository = orderRepository;
        this.productSubscriptionRepository = productSubscriptionRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.purchaseOrderItemRepository = purchaseOrderItemRepository;
    }

    /**
     * @param from
     * @param to
     * @return byte[]
     * To generate Excel sheet with shipped order details
     */
    @Override
    public byte[] generateShippedOrdersReportXLSX(LocalDate from, LocalDate to) {
        try(Workbook book = new XSSFWorkbook()){
        Sheet sheet = book.createSheet("Shipped orders report");
        final int[] rows = {0};
        Row row = sheet.createRow(rows[0]++);
        row.createCell(0).setCellValue("ID");
        row.createCell(1).setCellValue("Customer Name");
        row.createCell(2).setCellValue("Date of Birth");
        row.createCell(3).setCellValue("Email");
        row.createCell(4).setCellValue("Moneris ID");
        row.createCell(5).setCellValue("Phone Number");
        row.createCell(6).setCellValue("Order ID");
        row.createCell(7).setCellValue("Product + Dosage");
        row.createCell(8).setCellValue("Amount");
        row.createCell(9).setCellValue("Co-pay");
        row.createCell(10).setCellValue("Transaction ID");
        row.createCell(11).setCellValue("Has Insurance");
        row.createCell(12).setCellValue("Order Date");
        row.createCell(13).setCellValue("Shipping Date");
        row.createCell(14).setCellValue("Shipping Address");
        row.createCell(15).setCellValue("Tracking Number");
        row.createCell(16).setCellValue("Promo Code");
        row.createCell(17).setCellValue("UTM Source");

        List<OrderReportRecord> records = getShippedOrdersReport(from, to);
        records.forEach(r -> {
            Row row1 = sheet.createRow(rows[0]++);
            row1.createCell(0).setCellValue(r.getId());
            row1.createCell(1).setCellValue(r.getCustomerName());
            row1.createCell(2).setCellValue(r.getDateOfBirth());
            row1.createCell(3).setCellValue(r.getEmail());
            row1.createCell(4).setCellValue(r.getBillingCard());
            row1.createCell(5).setCellValue(r.getPhone());
            row1.createCell(6).setCellValue(r.getOrderNumber());
            row1.createCell(7).setCellValue(r.getProductAndDosage());
            row1.createCell(8).setCellValue(r.getAmount());
            row1.createCell(9).setCellValue(r.getCoPay());
            row1.createCell(10).setCellValue(r.getTransactionId());
            row1.createCell(11).setCellValue(r.isHasInsurance() ? "Yes" : "No");
            row1.createCell(12).setCellValue(r.getOrderDate());
            row1.createCell(13).setCellValue(r.getShippingDate());
            row1.createCell(14).setCellValue(r.getShippingAddress());
            row1.createCell(15).setCellValue(r.getTrackingNumber());
            row1.createCell(16).setCellValue(r.getPromoCode());
            row1.createCell(17).setCellValue(r.getUtmSource());
        });
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            book.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    /**
     * @return byte[]
     * To generate Excel sheet with newly registered user's data
     */
    @Override
    public byte[] generateNewUsersReportXLSX() {
        try(Workbook book = new XSSFWorkbook()){
        Sheet sheet = book.createSheet("Users without orders report");
        final int[] rows = {0};
        Row row = sheet.createRow(rows[0]++);
        row.createCell(0).setCellValue("Customer Name");
        row.createCell(1).setCellValue("Email");
        row.createCell(2).setCellValue("Phone Number");
        row.createCell(3).setCellValue("Province");
        row.createCell(4).setCellValue("Date of registration");
        row.createCell(5).setCellValue("UTM Source");
        List<UserReportRecord> records = getUserReport();
        records.forEach(r -> {
            Row row1 = sheet.createRow(rows[0]++);
            row1.createCell(0).setCellValue(r.getName()); //Customer full name, email address, phone number, province, date of registration
            row1.createCell(1).setCellValue(r.getEmail());
            row1.createCell(2).setCellValue(r.getPhone());
            row1.createCell(3).setCellValue(r.getProvince());
            row1.createCell(4).setCellValue(r.getRegistrationDate());
            row1.createCell(5).setCellValue(r.getUtmSource());
        });
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            book.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    /**
     * @return List<UserReportRecord>
     * To get all the user's details
     */
    @Override
    public List<UserReportRecord> getUserReport() {
        Set<UserReportRecord> resultSet = new HashSet<>();
        List<ContactUsRecord> contactUsRecords = getContactUsStatistics();
        List<StayConnectedRecord> stayConnectedRecords = getStayConnectedStatistics();
        List<StatisticsCreatedAnAccountRecord> createdAnAccountRecords = getCreatedAnAccountStatistics();
        stayConnectedRecords.forEach(r -> {
            UserReportRecord newRecord = UserReportRecord.builder()
                    .email(r.getEmail())
                    .name(r.getName())
                    .phone("-")
                    .province("-")
                    .registrationDate("-")
                    .utmSource(r.getUtmSource())
                    .build();
            resultSet.add(newRecord);
        });
        contactUsRecords.forEach(r -> {
            UserReportRecord newRecord = UserReportRecord.builder()
                    .email(r.getEmail())
                    .name(r.getName())
                    .phone(r.getPhone())
                    .province("-")
                    .registrationDate("-")
                    .utmSource(r.getUtmSource())
                    .build();
            resultSet.add(newRecord);
        });
        createdAnAccountRecords.forEach(r -> {
            UserReportRecord newRecord = UserReportRecord.builder()
                    .email(r.getUserEmail())
                    .name(r.getUserName())
                    .phone(r.getUserPhone())
                    .registrationDate(r.getUserRegistrationDate())
                    .province(r.getUserProvince())
                    .utmSource(r.getUtmSource())
                    .build();
            resultSet.add(newRecord);
        });
        return resultSet.stream()
                .sorted(Comparator.comparing(UserReportRecord::getEmail))
                .collect(Collectors.toList());
    }

    /**
     * @param from
     * @param to
     * @return List<OrderReportRecord>
     * To get shipping order report from given time period
     */
    @Override
    public List<OrderReportRecord> getShippedOrdersReport(LocalDate from, LocalDate to) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.CANADA);
        DecimalFormat decimalFormat = (DecimalFormat) numberFormat;
        decimalFormat.applyLocalizedPattern("###.00");
        log.info("Getting shipping report from {} to {}", from, to);
        List<PurchaseOrder> orders = orderRepository.findByStatusAndShippingDateBetween(PurchaseOrder.Status.DELIVERING, from, to);
        List<OrderReportRecord> result = new LinkedList<>();
        AtomicLong i = new AtomicLong(1);
        orders.forEach(o -> {
            User user = o.getUser();
            BillingCard billingCard = user.getPrimaryBillingCard();

            String shippingAddress = o.getShippingAddressCountry() + " " + o.getShippingAddressCity() + " " +
                    o.getShippingAddressLine1() + " " + o.getShippingAddressLine2() + " " + o.getShippingAddressPostalCode();

            OrderReportRecord record = OrderReportRecord.builder()
                    .id(i.getAndIncrement())
                    .customerName(user.getFirstName() + " " + user.getLastName())
                    .dateOfBirth(user.getBirthday())
                    .email(user.getEmail())
                    .phone(user.getPhone())
                    .orderNumber(o.getNumber() + "-" + o.getSubNumber())
                    .productAndDosage(o.getItems().get(0).getProductName() + " " + o.getItems().get(0).getProductDosage())
                    .amount(decimalFormat.format((double) o.getOrderPrice() / 100.0))
                    .coPay(o.getCoPay() == null ? decimalFormat.format((double) o.getOrderPrice() / 100.0) : decimalFormat.format((double) o.getCoPay() / 100.0))
                    .hasInsurance(user.getInsuranceDocument() != null)
                    .orderDate(o.getDate().toString())
                    .shippingDate(o.getShippingDate() == null ? "-" : o.getShippingDate().toString())
                    .shippingAddress(shippingAddress)
                    .trackingNumber(o.getShippingTrackingNumber())
                    .promoCode(o.getPromoCode() == null ? "" : o.getPromoCode())
                    .billingCard(billingCard == null ? "" :  billingCard.getId())
                    .transactionId(o.getTransactionId())
                    .utmSource(o.getSubscription().getUtmSource())
                    .build();

            result.add(record);
        });
        return result;
    }

    /**
     * @return List<StatisticsCreatedAnAccountRecord>
     * To get patients details who are all not purchase any product yet
     */
    @Override
    public List<StatisticsCreatedAnAccountRecord> getCreatedAnAccountStatistics() {
        log.info("Getting patient details..");
        List<User> users = userRepository.findByAuthority(User.Authority.PATIENT);
        List<StatisticsCreatedAnAccountRecord> result = new ArrayList<>();
        log.info("Filtering patients who are all not purchase any product..");
        users.stream()
                .filter(user -> subscriptionRepository.findAllByUser(user).isEmpty())
                .forEach(user -> {
                    StatisticsCreatedAnAccountRecord record = StatisticsCreatedAnAccountRecord.builder()
                            .userId(user.getId())
                            .userName(user.getFirstName()!=null? user.getFirstName()+ " " + user.getLastName():"")
                            .userEmail(user.getEmail())
                            .userPhone(user.getPhone())
                            .userBirthday(user.getBirthday())
                            .userRegistrationDate(user.getRegistrationDate().toString())
                            .userProvince(user.getProvince()!=null?user.getProvince().getName():null)
                            .utmSource(user.getUtmSource())
                            .build();
                    result.add(record);
                });
        return result;
    }

    /**
     * @return List<StatisticsCustomerRecord>
     * To get patients who are all purchased products
     */
    @Override
    public List<StatisticsCustomerRecord> getCustomersStatistics() {
        List<User> users = userRepository.findByAuthority(User.Authority.PATIENT);
        List<StatisticsCustomerRecord> result = new ArrayList<>();
        log.info("Filtering patients who are all customer of askwinston..");
        users.stream()
                .filter(user -> !subscriptionRepository.findAllByUser(user).isEmpty())
                .forEach(user -> {
                    StatisticsCustomerRecord record = StatisticsCustomerRecord.builder()
                            .userId(user.getId())
                            .userName(user.getFirstName() + " " + user.getLastName())
                            .userEmail(user.getEmail())
                            .userPhone(user.getPhone())
                            .userBirthday(user.getBirthday())
                            .subscriptions(getSubscriptionsRecords(user))
                            .hasValidCard(user.getBillingCards().stream()
                                    .anyMatch(BillingCard::getIsValid))
                            .build();
                    result.add(record);
                });
        return result;
    }

    /**
     * @return List<StayConnectedRecord>
     * To get stay connected user details
     */
    @Override
    public List<StayConnectedRecord> getStayConnectedStatistics() {
        log.info("Getting stay connected contacts..");
        List<StayConnectedRecord> result = new ArrayList<>();
        stayConnectedRecordRepository.findAll().forEach(result::add);
        return result;
    }

    /**
     * @return List<ContactUsRecord>
     * To get contact us user details
     */
    @Override
    public List<ContactUsRecord> getContactUsStatistics() {
        List<ContactUsRecord> result = new ArrayList<>();
        contactUsRecordRepository.findAll().forEach(result::add);
        return result;
    }

    /**
     * @param user
     * @return List<StatisticsSubscriptionRecord>
     * To get subscription details of given user
     */
    private List<StatisticsSubscriptionRecord> getSubscriptionsRecords(User user) {
        List<StatisticsSubscriptionRecord> result = new ArrayList<>();
        log.info("Filtering subscription details of user with id {}", user.getId());
        subscriptionRepository.findAllByUser(user).forEach(s -> {
            String nextOrderDate = s.getNextOrderDate() == null ? "" : s.getNextOrderDate().toString();
            StatisticsSubscriptionRecord subscriptionRecord = StatisticsSubscriptionRecord.builder()
                    .subscriptionId(s.getId())
                    .subscriptionStatus(s.getStatus().toString())
                    .nextOrderDate(nextOrderDate)
                    .orders(getOrdersRecords(s))
                    .build();
            result.add(subscriptionRecord);
        });
        return result;
    }

    /**
     * @param subscription
     * @return List<StatisticsOrderRecord>
     * To get order details of given subscription
     */
    private List<StatisticsOrderRecord> getOrdersRecords(ProductSubscription subscription) {
        List<StatisticsOrderRecord> result = new ArrayList<>();
        subscription.getOrders()
                .forEach(o -> {
                    StatisticsOrderRecord orderRecord = StatisticsOrderRecord.builder()
                            .orderNumber(o.getNumber() + "-" + o.getSubNumber())
                            .orderStatus(o.getStatus().toString())
                            .build();
                    result.add(orderRecord);
                });
        return result;
    }

    /**
     * @return byte[]
     * To generate Excel sheet with user's product subscription expiring data
     */
    @Override
    public byte[] generateSubscriptionExpiringReportXLSX(Date from, Date to) {
        try(Workbook book = new XSSFWorkbook()) {
            Sheet sheet = book.createSheet("Subscription Expiring Report");
            final int[] rows = {0};
            Row row = sheet.createRow(rows[0]++);
            row.createCell(0).setCellValue("Customer Id");
            row.createCell(1).setCellValue("Customer Name");
            row.createCell(2).setCellValue("Email");
            row.createCell(3).setCellValue("Phone Number");
            row.createCell(4).setCellValue("Address");
            row.createCell(5).setCellValue("Product");
            row.createCell(6).setCellValue("Subscription Id");
            row.createCell(7).setCellValue("Subscription Date");
            row.createCell(8).setCellValue("Total Refills");
            row.createCell(9).setCellValue("Refills Left");
            row.createCell(10).setCellValue("Subscription Expiry Date");
            row.createCell(11).setCellValue("Subscription Expiration Status");
            List<SubscriptionExpiringReport> subscriptionExpiringReportList = getSubscriptionExpiringReport(from,to);
            subscriptionExpiringReportList.forEach(report -> {
                Row row1 = sheet.createRow(rows[0]++);
                row1.createCell(0).setCellValue(report.getUserId());
                row1.createCell(1).setCellValue(report.getUserName());
                row1.createCell(2).setCellValue(report.getEmail());
                row1.createCell(3).setCellValue(report.getPhoneNumber());
                row1.createCell(4).setCellValue(report.getAddress());
                row1.createCell(5).setCellValue(report.getProduct());
                row1.createCell(6).setCellValue(report.getSubscriptionId());
                row1.createCell(7).setCellValue(report.getSubscriptionDate());
                row1.createCell(8).setCellValue(report.getTotalRefills());
                row1.createCell(9).setCellValue(report.getRefillsLeft());
                row1.createCell(10).setCellValue(report.getSubscriptionExpiryDate());
                row1.createCell(11).setCellValue(report.getExpiring());
            });
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            book.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    /**
     * @return List<SubscriptionExpiringReport>
     * This method to get user's product subscription expiring data within 30 days from today
     */
    private List<SubscriptionExpiringReport> getSubscriptionExpiringReport(Date from, Date to) {
        log.info("Getting user's product subscription expiring...");
        List<SubscriptionExpiringReport> subscriptionExpiringReports = new ArrayList<>();
        List<Prescription> prescriptions = this.prescriptionRepository.findByToDateBetween(from,to);
        prescriptions.forEach(prescription -> {
            SubscriptionExpiringReport subscriptionExpiringReport = new SubscriptionExpiringReport();
            ProductSubscription subscription = this.productSubscriptionRepository.findByPrescriptionId(prescription.getId());
            if (subscription!=null) {
                List<PurchaseOrder> purchaseOrders = this.purchaseOrderRepository.findBySubscriptionId(subscription.getId());
                purchaseOrders.forEach(purchaseOrder -> {
                    log.info("purchase order with id {}", purchaseOrder.getId());
                    PurchaseOrderItem purchaseOrderItem = this.purchaseOrderItemRepository.findByPurchaseOrderId(purchaseOrder.getId());
                    if (purchaseOrderItem != null) {
                        subscriptionExpiringReport.setProduct(purchaseOrderItem.getProductName());
                    }
                });
                subscriptionExpiringReport.setSubscriptionId(subscription.getId());
                subscriptionExpiringReport.setSubscriptionDate(prescription.getDate() != null ? prescription.getDate().toString() : "");
                subscriptionExpiringReport.setSubscriptionExpiryDate(prescription.getToDate() != null ? prescription.getToDate().toString() : "");
                subscriptionExpiringReport.setTotalRefills(prescription.getRefills());
                subscriptionExpiringReport.setRefillsLeft(prescription.getRefillsLeft());
                Optional<User> user = this.userRepository.findById(subscription.getUser().getId());
                if (user.isPresent()) {
                    User user1 = user.get();
                    subscriptionExpiringReport.setUserId(user1.getId());
                    subscriptionExpiringReport.setUserName(user1.getFirstName() + " " + user1.getLastName());
                    subscriptionExpiringReport.setEmail(user1.getEmail());
                    subscriptionExpiringReport.setPhoneNumber(user1.getPhone());
                    List<ShippingAddress> shippingAddresses = user1.getShippingAddresses();
                    shippingAddresses.stream().filter(address -> Boolean.TRUE.equals(address.isPrimary())).forEach(address -> {
                        String customerAddress = address.getAddressLine1()+ " "+address.getAddressLine2()+" "+address.getAddressCity()+" "+address.getAddressProvince()+" "+address.getAddressCountry()+" "+address.getAddressPostalCode();
                        subscriptionExpiringReport.setAddress(customerAddress);
                    });
                }
                boolean isExpiring = this.checkExpiringDate(prescription);
                if (isExpiring) {
                    log.info("Getting user's product subscription expiring...");
                    subscriptionExpiringReport.setExpiring("Expiring");
                } else {
                    log.info("Getting user's product subscription expired...");
                    subscriptionExpiringReport.setExpiring("Expired");
                }
                subscriptionExpiringReports.add(subscriptionExpiringReport);
            }
        });
        return subscriptionExpiringReports;
    }

    /**
     * @return Boolean
     * This method to check subscription Expiry Date within subscriptionExpiryDate from today
     */
    private boolean checkExpiringDate(Prescription prescription) {
        log.info("Checking user product subscription expiring date...");
        Date subscriptionExpiryDate = prescription.getToDate();
        Date currentDate = new Date();
        boolean isExpiring = false;
        if (subscriptionExpiryDate != null) {
            isExpiring = subscriptionExpiryDate.after(currentDate);
        }
        return isExpiring;
    }


    /**
     * @return byte[]
     * To generate Excel sheet with user's product subscription renewal data
     */
    @Override
    public byte[] generateSubscriptionRenewedReportXLSX(Date from, Date to) {
        try (Workbook book = new XSSFWorkbook()) {
            Sheet sheet = book.createSheet("Subscription Renewal Report");
            final int[] rows = {0};
            Row row = sheet.createRow(rows[0]++);
            row.createCell(0).setCellValue("Customer Id");
            row.createCell(1).setCellValue("Customer Name");
            row.createCell(2).setCellValue("Email");
            row.createCell(3).setCellValue("Phone Number");
            row.createCell(4).setCellValue("Address");
            row.createCell(5).setCellValue("Product");
            row.createCell(6).setCellValue("Expired Subscription Id");
            row.createCell(7).setCellValue("Subscription Expired Date");
            row.createCell(8).setCellValue("Renewed Subscription Date");
            row.createCell(9).setCellValue("Subscription Renewal Date");
            row.createCell(10).setCellValue("Total Refills");
            row.createCell(11).setCellValue("Refills Left");
            List<SubscriptionRenewedReport> subscriptionRenewedReports = getSubscriptionRenewedReportXLSX(from, to);
            subscriptionRenewedReports.forEach(report -> {
                Row row1 = sheet.createRow(rows[0]++);
                row1.createCell(0).setCellValue(report.getUserId());
                row1.createCell(1).setCellValue(report.getUserName());
                row1.createCell(2).setCellValue(report.getEmail());
                row1.createCell(3).setCellValue(report.getPhoneNumber());
                row1.createCell(4).setCellValue(report.getAddress());
                row1.createCell(5).setCellValue(report.getProduct());
                row1.createCell(6).setCellValue(report.getExpiredSubscriptionId());
                row1.createCell(7).setCellValue(report.getSubscriptionExpiredDate());
                row1.createCell(8).setCellValue(report.getRenewedSubscriptionId());
                row1.createCell(9).setCellValue(report.getSubscriptionRenewalDate());
                row1.createCell(10).setCellValue(report.getTotalRefills());
                row1.createCell(11).setCellValue(report.getRefillsLeft());
            });
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            book.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    public List<SubscriptionRenewedReport> getSubscriptionRenewedReportXLSX(Date from, Date to) {

        List<SubscriptionExpiringReport> subscriptionExpiringReports = this.getSubscriptionExpiringReport(from, to)
                .stream().filter(report -> report.getExpiring().equals("Expired")).collect(Collectors.toList());

        List<SubscriptionRenewedReport> subscriptionRenewedReports = new ArrayList<>();

        for (SubscriptionExpiringReport subscriptionExpiringReport : subscriptionExpiringReports) {
            List<ProductSubscription> productSubscriptions = this.productSubscriptionRepository
                    .findAllByUserIdAndStatus(subscriptionExpiringReport.getUserId(), ProductSubscription.Status.ACTIVE);

            for (ProductSubscription productSubscription : productSubscriptions) {
                ProductSubscription expiredSubscription = this.productSubscriptionRepository
                        .findById(subscriptionExpiringReport.getSubscriptionId()).orElseThrow(() -> {
                            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription " + subscriptionExpiringReport.getSubscriptionId() + " is not found");
                        });
                if (productSubscription.getItems().containsAll(expiredSubscription.getItems())) {
                    SubscriptionRenewedReport subscriptionRenewedReport = new SubscriptionRenewedReport();
                    subscriptionRenewedReport.setUserId(subscriptionExpiringReport.getUserId());
                    subscriptionRenewedReport.setUserName(subscriptionExpiringReport.getUserName());
                    subscriptionRenewedReport.setEmail(subscriptionExpiringReport.getEmail());
                    subscriptionRenewedReport.setAddress(subscriptionExpiringReport.getAddress());
                    subscriptionRenewedReport.setProduct(subscriptionExpiringReport.getProduct());
                    subscriptionRenewedReport.setPhoneNumber(subscriptionExpiringReport.getPhoneNumber());
                    subscriptionRenewedReport.setExpiredSubscriptionId(subscriptionExpiringReport.getSubscriptionId());
                    subscriptionRenewedReport.setSubscriptionExpiredDate(subscriptionExpiringReport.getSubscriptionExpiryDate());
                    subscriptionRenewedReport.setRenewedSubscriptionId(productSubscription.getId());
                    subscriptionRenewedReport.setSubscriptionRenewalDate(productSubscription.getDate().toString());
                    subscriptionRenewedReport.setTotalRefills(productSubscription.getPrescription().getRefills());
                    subscriptionRenewedReport.setRefillsLeft(productSubscription.getPrescription().getRefillsLeft());
                    subscriptionRenewedReports.add(subscriptionRenewedReport);
                }
            }
        }
        return subscriptionRenewedReports;
    }
}
