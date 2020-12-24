package com.askwinston.web.controller;

import com.askwinston.exception.PaymentException;
import com.askwinston.helper.HttpHelper;
import com.askwinston.helper.ParsingHelper;
import com.askwinston.model.Courier;
import com.askwinston.model.DocumentResource;
import com.askwinston.model.PurchaseOrder;
import com.askwinston.order.OrderEngine;
import com.askwinston.subscription.ProductSubscription;
import com.askwinston.subscription.SubscriptionEngine;
import com.askwinston.web.dto.*;
import com.askwinston.web.secuity.AwUserPrincipal;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/order")
public class OrderController {

    private ParsingHelper parsingHelper;
    private SubscriptionEngine subscriptionEngine;
    private OrderEngine orderEngine;

    public OrderController(ParsingHelper parsingHelper,
                           OrderEngine orderEngine,
                           SubscriptionEngine subscriptionEngine) {
        this.parsingHelper = parsingHelper;
        this.orderEngine = orderEngine;
        this.subscriptionEngine = subscriptionEngine;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('PATIENT')")
    @JsonView(DtoView.PatientVisibility.class)
    public List<PurchaseOrderDto> getOrders(@AuthenticationPrincipal AwUserPrincipal principal) {
        Long userId = principal.getId();
        List<PurchaseOrder> purchaseOrders = orderEngine.getAll(userId).stream()
                .filter(order -> order.getSubscription() != null)
                .filter(order -> !order.getStatus().equals(PurchaseOrder.Status.CANCELLED))
                .collect(Collectors.toList());
        return parsingHelper.mapObjects(purchaseOrders, PurchaseOrderDto.class);
    }

    @GetMapping("/user/{id}")
    @PreAuthorize("hasAnyAuthority('DOCTOR')")
    @JsonView(DtoView.DoctorVisibility.class)
    public List<PurchaseOrderDto> getUserOrders(@PathVariable("id") Long userId) {
        List<PurchaseOrder> purchaseOrders = orderEngine.getAll(userId).stream()
                .filter(order -> order.getSubscription() != null)
                .filter(order -> !order.getStatus().equals(PurchaseOrder.Status.CANCELLED))
                .collect(Collectors.toList());
        return parsingHelper.mapObjects(purchaseOrders, PurchaseOrderDto.class);
    }

    @GetMapping("/waiting-doctor")
    @PreAuthorize("hasAnyAuthority('DOCTOR')")
    @JsonView(DtoView.DoctorVisibility.class)
    public List<PurchaseOrderDto> getWaitingDoctorOrders() {
        List<PurchaseOrder> purchaseOrders = orderEngine.getOrdersByStatus(PurchaseOrder.Status.WAITING_DOCTOR)
                .stream().filter(order -> order.getSubscription() != null).collect(Collectors.toList());
        return parsingHelper.mapObjects(purchaseOrders, PurchaseOrderDto.class);
    }

    @GetMapping("/waiting-pharmacist-and-packaging")
    @PreAuthorize("hasAnyAuthority('PHARMACIST')")
    @JsonView(DtoView.PharmacistVisibility.class)
    public List<PurchaseOrderDto> getWaitingPharmacistAndPackagingOrders() {
        List<PurchaseOrder> waitingPharmacistOrders = orderEngine
                .getOrdersByStatus(PurchaseOrder.Status.WAITING_PHARMACIST);
        waitingPharmacistOrders.addAll(orderEngine.getOrdersByStatus(PurchaseOrder.Status.WAITING_PHARMACY_RX_CHECK));
        waitingPharmacistOrders.addAll(orderEngine.getOrdersByStatus(PurchaseOrder.Status.PACKAGING));
        return parsingHelper.mapObjects(waitingPharmacistOrders, PurchaseOrderDto.class);
    }

    @PutMapping("/{id}/packaging")
    @PreAuthorize("hasAnyAuthority('PHARMACIST')")
    public String packagingOrder(@PathVariable("id") Long id, @RequestBody PackagingOrderDto dto) {
        PurchaseOrder purchaseOrder = orderEngine.getById(id);
        subscriptionEngine.setPharmacyId(purchaseOrder.getSubscription(), dto.getText());
        try {
            subscriptionEngine.performOrder(purchaseOrder, dto.getAmount());
            return "OK";
        } catch (PaymentException e) {
            return "PAUSED";
        }
    }

    @PutMapping("/{id}/packaging-rx")
    @PreAuthorize("hasAnyAuthority('PHARMACIST')")
    public String packagingRxTransferOrder(@PathVariable("id") Long id, @RequestBody PackagingOrderDto dto) {
        PurchaseOrder purchaseOrder = orderEngine.getById(id);
        subscriptionEngine.setPharmacyId(purchaseOrder.getSubscription(), dto.getRxNumber());
        subscriptionEngine.updateSubscriptionStatus(purchaseOrder.getSubscription().getId(), ProductSubscription.Status.ACTIVE);
        try {
            subscriptionEngine.performOrderWithRxTransfer(purchaseOrder, dto.getAmount(), dto.getRefillsLeft(), dto.getToDate());
            return "OK";
        } catch (PaymentException e) {
            return "PAUSED";
        }
    }

    @PutMapping("/{id}/delivering")
    @PreAuthorize("hasAnyAuthority('PHARMACIST')")
    public void deliveringOrder(@PathVariable("id") Long id, @RequestBody OrderDeliveringDto deliveringInfo) {
        PurchaseOrder purchaseOrder = orderEngine.getById(id);
        orderEngine.deliveringOrder(purchaseOrder, Courier.getByName(deliveringInfo.getCourier()), deliveringInfo.getTrackingNumber());
    }

    @GetMapping("/couriers")
    @PreAuthorize("hasAnyAuthority('PHARMACIST')")
    public List<String> getAvailableCouriers() {
        return Arrays.stream(Courier.values())
                .map(Courier::getName)
                .collect(Collectors.toList());
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasAnyAuthority('DOCTOR', 'PHARMACIST')")
    public void rejectOrder(@PathVariable("id") Long id, @RequestBody TextDto rejectionNotes) {
        PurchaseOrder purchaseOrder = orderEngine.getById(id);
        if (purchaseOrder.getStatus().equals(PurchaseOrder.Status.WAITING_PHARMACY_RX_CHECK)) {
            subscriptionEngine.rejectSubscription(purchaseOrder.getSubscription().getId(), rejectionNotes.getText());
        }
        orderEngine.rejectOrder(id);
    }

    @GetMapping("/{id}/prescription")
    public ResponseEntity<InputStreamResource> download(@PathVariable Long id) {
        return HttpHelper.fileDownload(new DocumentResource("Order_" + id + "_prescription.pdf", "application/pdf", orderEngine.generatePrescriptionPdf(id)));
    }

}
