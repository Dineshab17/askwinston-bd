package com.askwinston.web.controller;

import com.askwinston.helper.HttpHelper;
import com.askwinston.model.DocumentResource;
import com.askwinston.service.StatisticsService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDate;


@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    private StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/shipped")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<InputStreamResource> getShippedOrdersReport(@RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate from, @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate to) {
        return HttpHelper.fileDownload(new DocumentResource("Shipped_orders_report.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", statisticsService.generateShippedOrdersReportXLSX(from, to)));
    }

    @GetMapping("/new-users")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<InputStreamResource> getNewUsersReport() {
        return HttpHelper.fileDownload(new DocumentResource("New_users_report.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", statisticsService.generateNewUsersReportXLSX()));
    }
}
