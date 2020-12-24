package com.askwinston.subscription;

import com.askwinston.exception.ShoppingCartException;
import com.askwinston.helper.ParsingHelper;
import com.askwinston.helper.ScheduleService;
import com.askwinston.model.Document;
import com.askwinston.model.MdPostConsultNote;
import com.askwinston.model.User;
import com.askwinston.notification.NotificationEngine;
import com.askwinston.notification.NotificationEventTypeContainer;
import com.askwinston.service.UserService;
import com.askwinston.service.impl.DocumentService;
import com.askwinston.web.dto.DtoView;
import com.askwinston.web.dto.MdPostConsultNoteDto;
import com.askwinston.web.dto.RxTransferSubscriptionDto;
import com.askwinston.web.dto.TextDto;
import com.askwinston.web.secuity.AwUserPrincipal;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/subscription")
public class SubscriptionController {

    private static final String WRONG_SUBSCRIPTION_OWNER_ERROR_MESSAGE = "The subscription is not yours";

    private static final int ID_CHECK_DELAY_IN_MINUTES = 1; //Should be 15

    private ParsingHelper parsingHelper;
    private ProductSubscriptionRepository subscriptionRepository;
    private SubscriptionEngine subscriptionEngine;
    private UserService userService;
    private NotificationEngine notificationEngine;
    private ScheduleService scheduleService;
    private DocumentService documentService;

    public SubscriptionController(ParsingHelper parsingHelper,
                                  ProductSubscriptionRepository subscriptionRepository,
                                  SubscriptionEngine subscriptionEngine,
                                  UserService userService,
                                  NotificationEngine notificationEngine,
                                  ScheduleService scheduleService,
                                  DocumentService documentService) {
        this.parsingHelper = parsingHelper;
        this.subscriptionRepository = subscriptionRepository;
        this.subscriptionEngine = subscriptionEngine;
        this.userService = userService;
        this.notificationEngine = notificationEngine;
        this.scheduleService = scheduleService;
        this.documentService = documentService;
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('PATIENT')")
    @JsonView(DtoView.PatientVisibility.class)
    public List<ProductSubscriptionDto> createSubscription(@RequestBody TextDto textDto,
                                                           @AuthenticationPrincipal AwUserPrincipal principal) {
        Long userId = principal.getId();
        try {
            List<ProductSubscription> subscriptions = subscriptionEngine.checkoutCart(userId, textDto.getText());
            scheduleShotTermNotification(userId);
            return parsingHelper.mapObjects(subscriptions, ProductSubscriptionDto.class);
        } catch (ShoppingCartException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/with-rx")
    @PreAuthorize("hasAnyAuthority('PATIENT')")
    @JsonView(DtoView.PatientVisibility.class)
    public ProductSubscriptionDto createSubscriptionWithRx(@RequestBody RxTransferSubscriptionDto dto,
                                                           @AuthenticationPrincipal AwUserPrincipal principal) {
        Long userId = principal.getId();
        try {
            ProductSubscription subscription = subscriptionEngine.createSubscriptionWithRxNumber(userId, dto);
            scheduleShotTermNotification(userId);
            return parsingHelper.mapObject(subscription, ProductSubscriptionDto.class);
        } catch (ShoppingCartException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/{id}/start")
    @PreAuthorize("hasAnyAuthority('DOCTOR')")
    public void confirmSubscription(@PathVariable("id") Long id,
                                    @Validated @RequestBody MdPostConsultNoteDto mdPostConsultNoteDto) {
        ProductSubscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription not found"));
        try {
            MdPostConsultNote mdPostConsultNote = parsingHelper.mapObject(mdPostConsultNoteDto,
                    MdPostConsultNote.class);
            subscriptionEngine.createPrescription(subscription, mdPostConsultNote);
            subscriptionEngine.updateSubscriptionStatus(id, ProductSubscription.Status.ACTIVE);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "RX generation and sending failed. Please, try again later");
        }
    }

    @PutMapping("/{id}/pause")
    @PreAuthorize("hasAnyAuthority('PATIENT', 'ADMIN')")
    public ProductSubscriptionDto pauseSubscriptionByPatient(@AuthenticationPrincipal AwUserPrincipal principal, @PathVariable("id") Long id) {
        User user = userService.getById(principal.getId());
        ProductSubscription subscription = subscriptionEngine.getById(id);
        if (!subscription.getUser().getId().equals(principal.getId()) && !user.getAuthority().equals(User.Authority.ADMIN)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, WRONG_SUBSCRIPTION_OWNER_ERROR_MESSAGE);
        }
        return parsingHelper.mapObject(subscriptionEngine.pauseSubscriptionByPatient(subscription), ProductSubscriptionDto.class);
    }

    @PutMapping("/{id}/pause-notes")
    @PreAuthorize("hasAnyAuthority('PATIENT', 'ADMIN')")
    public ProductSubscriptionDto setPauseNotes(@PathVariable("id") Long id, @RequestBody TextDto pauseNotes) {
        ProductSubscription subscription = subscriptionEngine.getById(id);
        return parsingHelper.mapObject(subscriptionEngine.setPauseNotes(subscription, pauseNotes.getText()), ProductSubscriptionDto.class);
    }

    @PutMapping("/{id}/resume-paused-by-patient")
    @PreAuthorize("hasAnyAuthority('PATIENT', 'ADMIN')")
    public ProductSubscriptionDto resumeSubscriptionPausedByPatient(@AuthenticationPrincipal AwUserPrincipal principal, @PathVariable("id") Long id) {
        User user = userService.getById(principal.getId());
        ProductSubscription subscription = subscriptionEngine.getById(id);
        if (!subscription.getUser().getId().equals(principal.getId()) && !user.getAuthority().equals(User.Authority.ADMIN)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, WRONG_SUBSCRIPTION_OWNER_ERROR_MESSAGE);
        }
        return parsingHelper.mapObject(subscriptionEngine.resumeSubscriptionPausedByPatient(subscription), ProductSubscriptionDto.class);
    }

    @PostMapping("/{id}/send")
    @PreAuthorize("hasAnyAuthority('PATIENT')")
    @JsonView(DtoView.PatientVisibility.class)
    public ProductSubscriptionDto sendOrderNow(@PathVariable("id") Long id,
                                               @AuthenticationPrincipal AwUserPrincipal principal) {
        ProductSubscription subscription = subscriptionEngine.getById(id);
        if (!subscription.getUser().getId().equals(principal.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, WRONG_SUBSCRIPTION_OWNER_ERROR_MESSAGE);
        }
        subscriptionEngine.processSubscription(subscription);
        return parsingHelper.mapObject(subscription, ProductSubscriptionDto.class);
    }

    @GetMapping("/waiting-doctor")
    @PreAuthorize("hasAnyAuthority('DOCTOR')")
    @JsonView(DtoView.DoctorVisibility.class)
    public List<ProductSubscriptionDto> getDoctorSubscriptions() {
        List<ProductSubscription> subscriptions = subscriptionRepository
                .findAllByStatus(ProductSubscription.Status.WAITING_DOCTOR);
        return parsingHelper.mapObjects(subscriptions, ProductSubscriptionDto.class);
    }

    @GetMapping("/paused")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @JsonView(DtoView.AdminVisibility.class)
    public List<ProductSubscriptionDto> getPausedSubscriptions() {
        List<ProductSubscription> subscriptions = subscriptionRepository
                .findAllByStatus(ProductSubscription.Status.PAUSED);
        subscriptions.addAll(subscriptionRepository
                .findAllByStatus(ProductSubscription.Status.PAUSED_RX_TRANSFER));
        return parsingHelper.mapObjects(subscriptions, ProductSubscriptionDto.class);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('PATIENT')")
    @JsonView(DtoView.PatientVisibility.class)
    public List<ProductSubscriptionDto> getUserSubscriptions(@AuthenticationPrincipal AwUserPrincipal principal) {
        Long userId = principal.getId();
        User user = userService.getById(userId);
        List<ProductSubscription> subscriptions = subscriptionRepository.findAllByUser(user);
        return parsingHelper.mapObjects(subscriptions, ProductSubscriptionDto.class);
    }

    @PutMapping("/{id}/skip")
    @PreAuthorize("hasAnyAuthority('PATIENT')")
    public ProductSubscriptionDto skipRefill(@PathVariable("id") Long id) {
        ProductSubscription subscription = subscriptionEngine.skipNextOrder(id);
        return parsingHelper.mapObject(subscription, ProductSubscriptionDto.class);
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ProductSubscriptionDto cancel(@PathVariable("id") Long id) {
        ProductSubscription subscription = subscriptionEngine.getById(id);
        subscription = subscriptionEngine.cancelSubscription(subscription);
        return parsingHelper.mapObject(subscription, ProductSubscriptionDto.class);
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasAnyAuthority('DOCTOR', 'PHARMACIST')")
    public void rejectSubscription(@PathVariable("id") Long id, @RequestBody TextDto rejectionNotes) {
        subscriptionEngine.rejectSubscription(id, rejectionNotes.getText());
    }

    @PutMapping("/{id}/resume")
    @PreAuthorize("hasAnyAuthority('PATIENT', 'ADMIN')")
    @JsonView(DtoView.PatientVisibility.class)
    public ProductSubscriptionDto resumeSubscription(@PathVariable("id") Long id,
                                                     @AuthenticationPrincipal AwUserPrincipal principal) {
        User user = userService.getById(principal.getId());
        ProductSubscription subscription = subscriptionEngine.getById(id);
        if (!subscription.getUser().getId().equals(principal.getId()) && !user.getAuthority().equals(User.Authority.ADMIN)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, WRONG_SUBSCRIPTION_OWNER_ERROR_MESSAGE);
        }
        subscription = subscriptionEngine.resumeSubscription(subscription);
        return parsingHelper.mapObject(subscription, ProductSubscriptionDto.class);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @JsonView(DtoView.AdminVisibility.class)
    public List<ProductSubscriptionDto> getAllSubscriptions(@AuthenticationPrincipal AwUserPrincipal principal) {
        List<ProductSubscription> subscriptions = new LinkedList<>();
        subscriptionRepository.findAll().forEach(subscriptions::add);
        return parsingHelper.mapObjects(subscriptions, ProductSubscriptionDto.class);
    }

    @PutMapping(value = "/prescription", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('PATIENT')")
    public Long uploadPrescription(@AuthenticationPrincipal AwUserPrincipal principal,
                                   @RequestParam MultipartFile file) throws IOException {
        Document document = documentService.saveDocument(principal.getId(), file.getOriginalFilename(), file.getContentType(), file.getBytes());
        return document.getId();
    }

    private void scheduleShotTermNotification(final long userId) {
        User user = userService.getById(userId);
        Runnable task = () -> {
            boolean hasIdImg = user.getIdDocument() != null;
            log.info("User: " + user.getFirstName() + " " + user.getLastName() + "; Has ID: " + hasIdImg);
            if (!hasIdImg) {
                notificationEngine.notify(NotificationEventTypeContainer.ID_DOWNLOAD_CHECK_FAILED, user);
            }
        };
        scheduleService.scheduleTask(task, ID_CHECK_DELAY_IN_MINUTES * 60 * 1000L);
    }
}
