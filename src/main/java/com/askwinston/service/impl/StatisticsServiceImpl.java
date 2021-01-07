package com.askwinston.service.impl;

import com.askwinston.model.*;
import com.askwinston.repository.ContactUsRecordRepository;
import com.askwinston.repository.PurchaseOrderRepository;
import com.askwinston.repository.StayConnectedRecordRepository;
import com.askwinston.repository.UserRepository;
import com.askwinston.service.StatisticsService;
import com.askwinston.subscription.ProductSubscription;
import com.askwinston.subscription.ProductSubscriptionRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private ProductSubscriptionRepository subscriptionRepository;
    private UserRepository userRepository;
    private StayConnectedRecordRepository stayConnectedRecordRepository;
    private ContactUsRecordRepository contactUsRecordRepository;
    private PurchaseOrderRepository orderRepository;

    public StatisticsServiceImpl(ProductSubscriptionRepository subscriptionRepository,
                                 UserRepository userRepository,
                                 StayConnectedRecordRepository stayConnectedRecordRepository,
                                 ContactUsRecordRepository contactUsRecordRepository,
                                 PurchaseOrderRepository orderRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
        this.stayConnectedRecordRepository = stayConnectedRecordRepository;
        this.contactUsRecordRepository = contactUsRecordRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public byte[] generateShippedOrdersReportXLSX(LocalDate from, LocalDate to) {
        Workbook book = new XSSFWorkbook();
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
        });
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            book.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    @Override
    public byte[] generateNewUsersReportXLSX() {
        Workbook book = new XSSFWorkbook();
        Sheet sheet = book.createSheet("Users without orders report");
        final int[] rows = {0};
        Row row = sheet.createRow(rows[0]++);
        row.createCell(0).setCellValue("Customer Name");
        row.createCell(1).setCellValue("Email");
        row.createCell(2).setCellValue("Phone Number");
        row.createCell(3).setCellValue("Province");
        row.createCell(4).setCellValue("Date of registration");
        List<UserReportRecord> records = getUserReport();
        records.forEach(r -> {
            Row row1 = sheet.createRow(rows[0]++);
            row1.createCell(0).setCellValue(r.getName()); //Customer full name, email address, phone number, province, date of registration
            row1.createCell(1).setCellValue(r.getEmail());
            row1.createCell(2).setCellValue(r.getPhone());
            row1.createCell(3).setCellValue(r.getProvince());
            row1.createCell(4).setCellValue(r.getRegistrationDate());
        });
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            book.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

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
                    .build();
            resultSet.add(newRecord);
        });
        return resultSet.stream()
                .sorted(Comparator.comparing(UserReportRecord::getName))
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderReportRecord> getShippedOrdersReport(LocalDate from, LocalDate to) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.CANADA);
        DecimalFormat decimalFormat = (DecimalFormat) numberFormat;
        decimalFormat.applyLocalizedPattern("###.00");
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
                    .build();

            result.add(record);
        });
        return result;
    }

    @Override
    public List<StatisticsCreatedAnAccountRecord> getCreatedAnAccountStatistics() {
        List<User> users = userRepository.findByAuthority(User.Authority.PATIENT);
        List<StatisticsCreatedAnAccountRecord> result = new ArrayList<>();
        users.stream()
                .filter(user -> subscriptionRepository.findAllByUser(user).isEmpty())
                .forEach(user -> {
                    StatisticsCreatedAnAccountRecord record = StatisticsCreatedAnAccountRecord.builder()
                            .userId(user.getId())
                            .userName(user.getFirstName() + " " + user.getLastName())
                            .userEmail(user.getEmail())
                            .userPhone(user.getPhone())
                            .userBirthday(user.getBirthday())
                            .userRegistrationDate(user.getRegistrationDate().toString())
                            .userProvince(user.getProvince().getName())
                            .build();
                    result.add(record);
                });
        return result;
    }

    @Override
    public List<StatisticsCustomerRecord> getCustomersStatistics() {
        List<User> users = userRepository.findByAuthority(User.Authority.PATIENT);
        List<StatisticsCustomerRecord> result = new ArrayList<>();
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

    @Override
    public List<StayConnectedRecord> getStayConnectedStatistics() {
        List<StayConnectedRecord> result = new ArrayList<>();
        stayConnectedRecordRepository.findAll().forEach(result::add);
        return result;
    }

    @Override
    public List<ContactUsRecord> getContactUsStatistics() {
        List<ContactUsRecord> result = new ArrayList<>();
        contactUsRecordRepository.findAll().forEach(result::add);
        return result;
    }

    private List<StatisticsSubscriptionRecord> getSubscriptionsRecords(User user) {
        List<StatisticsSubscriptionRecord> result = new ArrayList<>();
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
}
