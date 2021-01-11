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
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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

    /**
     * @param principal
     * @return List<PurchaseOrderDto>
     * To get purchase order details of the patient
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('PATIENT')")
    @JsonView(DtoView.PatientVisibility.class)
    public List<PurchaseOrderDto> getOrders(@AuthenticationPrincipal AwUserPrincipal principal) {
        Long userId = principal.getId();
        log.info("Getting purchase orders and filter only active orders of patient with id: {}", userId);
        List<PurchaseOrder> purchaseOrders = orderEngine.getAll(userId).stream()
                .filter(order -> order.getSubscription() != null)
                .filter(order -> !order.getStatus().equals(PurchaseOrder.Status.CANCELLED))
                .collect(Collectors.toList());
        return parsingHelper.mapObjects(purchaseOrders, PurchaseOrderDto.class);
    }

    /**
     * @param userId
     * @return List<PurchaseOrderDto>
     * To get all the active orders of the user
     */
    @GetMapping("/user/{id}")
    @PreAuthorize("hasAnyAuthority('DOCTOR')")
    @JsonView(DtoView.DoctorVisibility.class)
    public List<PurchaseOrderDto> getUserOrders(@PathVariable("id") Long userId) {
        log.info("Getting purchase orders of the patient with id {} by the doctor", userId);
        List<PurchaseOrder> purchaseOrders = orderEngine.getAll(userId).stream()
                .filter(order -> order.getSubscription() != null)
                .filter(order -> !order.getStatus().equals(PurchaseOrder.Status.CANCELLED))
                .collect(Collectors.toList());
        return parsingHelper.mapObjects(purchaseOrders, PurchaseOrderDto.class);
    }

    /**
     * @return List<PurchaseOrderDto>
     * To get purchase orders those are waiting for doctors review
     */
    @GetMapping("/waiting-doctor")
    @PreAuthorize("hasAnyAuthority('DOCTOR')")
    @JsonView(DtoView.DoctorVisibility.class)
    public List<PurchaseOrderDto> getWaitingDoctorOrders() {
        log.info("Getting purchase order with status {}", PurchaseOrder.Status.WAITING_DOCTOR);
        List<PurchaseOrder> purchaseOrders = orderEngine.getOrdersByStatus(PurchaseOrder.Status.WAITING_DOCTOR)
                .stream().filter(order -> order.getSubscription() != null).collect(Collectors.toList());
        return parsingHelper.mapObjects(purchaseOrders, PurchaseOrderDto.class);
    }

    /**
     * @return List<PurchaseOrder>
     * To get purchase orders those are waiting for pharmacist and packaging
     */
    @GetMapping("/waiting-pharmacist-and-packaging")
    @PreAuthorize("hasAnyAuthority('PHARMACIST')")
    @JsonView(DtoView.PharmacistVisibility.class)
    public List<PurchaseOrderDto> getWaitingPharmacistAndPackagingOrders() {
        log.info("Getting purchase order with status {}", PurchaseOrder.Status.WAITING_PHARMACIST);
        List<PurchaseOrder> waitingPharmacistOrders = orderEngine
                .getOrdersByStatus(PurchaseOrder.Status.WAITING_PHARMACIST);
        log.info("Getting purchase order with status {}", PurchaseOrder.Status.WAITING_PHARMACY_RX_CHECK);
        waitingPharmacistOrders.addAll(orderEngine.getOrdersByStatus(PurchaseOrder.Status.WAITING_PHARMACY_RX_CHECK));
        log.info("Getting purchase order with status {}", PurchaseOrder.Status.PACKAGING);
        waitingPharmacistOrders.addAll(orderEngine.getOrdersByStatus(PurchaseOrder.Status.PACKAGING));
        return parsingHelper.mapObjects(waitingPharmacistOrders, PurchaseOrderDto.class);
    }

    /**
     * @param id
     * @param dto
     * @return String
     * To set the pharmacy id for the purchase order subscription
     * and check whether payment done and perform the packaging
     */
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

    /**
     * @param id
     * @param dto
     * @return String
     * To set the product subscription status as active and perform the packaging
     */
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

    /**
     * @param id
     * @param deliveringInfo
     * To update the status of order as delivering with tracking number and courier service information
     */
    @PutMapping("/{id}/delivering")
    @PreAuthorize("hasAnyAuthority('PHARMACIST')")
    public void deliveringOrder(@PathVariable("id") Long id, @RequestBody OrderDeliveringDto deliveringInfo) {
        PurchaseOrder purchaseOrder = orderEngine.getById(id);
        orderEngine.deliveringOrder(purchaseOrder, Courier.getByName(deliveringInfo.getCourier()), deliveringInfo.getTrackingNumber());
    }

    /**
     * @return List<String>
     * To get available courier services connected with askwinston
     */
    @GetMapping("/couriers")
    @PreAuthorize("hasAnyAuthority('PHARMACIST')")
    public List<String> getAvailableCouriers() {
        return Arrays.stream(Courier.values())
                .map(Courier::getName)
                .collect(Collectors.toList());
    }


    /**
     * @param id
     * @param rejectionNotes
     * To reject the purchase order by doctor or pharmacist
     */
    @PutMapping("/{id}/reject")
    @PreAuthorize("hasAnyAuthority('DOCTOR', 'PHARMACIST')")
    public void rejectOrder(@PathVariable("id") Long id, @RequestBody TextDto rejectionNotes) {
        PurchaseOrder purchaseOrder = orderEngine.getById(id);
        if (purchaseOrder.getStatus().equals(PurchaseOrder.Status.WAITING_PHARMACY_RX_CHECK)) {
            subscriptionEngine.rejectSubscription(purchaseOrder.getSubscription().getId(), rejectionNotes.getText());
            log.info("Purchase order with id {} has been rejected", id);
        }
        orderEngine.rejectOrder(id);
    }

    /**
     * @param id
     * @return ResponseEntity<InputStreamResource>
     * To download prescription for a patient
     */
    @GetMapping("/{id}/prescription")
    public ResponseEntity<InputStreamResource> download(@PathVariable Long id) {
        return HttpHelper.fileDownload(new DocumentResource("Order_" + id + "_prescription.pdf", "application/pdf", orderEngine.generatePrescriptionPdf(id)));
    }

}
