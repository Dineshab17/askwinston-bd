package com.askwinston.service;

import com.askwinston.model.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface StatisticsService {

    byte[] generateShippedOrdersReportXLSX(LocalDate from, LocalDate to);

    byte[] generateNewUsersReportXLSX();

    List<OrderReportRecord> getShippedOrdersReport(LocalDate from, LocalDate to);

    List<UserReportRecord> getUserReport();

    List<StatisticsCreatedAnAccountRecord> getCreatedAnAccountStatistics();

    List<StatisticsCustomerRecord> getCustomersStatistics();

    List<StayConnectedRecord> getStayConnectedStatistics();

    List<ContactUsRecord> getContactUsStatistics();

    byte[] generateSubscriptionExpiringReportXLSX(Date from, Date to);

    byte[] generateSubscriptionRenewedReportXLSX(Date from, Date to);
}
