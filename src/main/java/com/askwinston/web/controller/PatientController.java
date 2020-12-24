package com.askwinston.web.controller;

import com.askwinston.exception.PaymentException;
import com.askwinston.helper.ParsingHelper;
import com.askwinston.model.BillingCard;
import com.askwinston.model.Document;
import com.askwinston.model.ShippingAddress;
import com.askwinston.model.User;
import com.askwinston.service.PaymentService;
import com.askwinston.service.UserService;
import com.askwinston.service.impl.DocumentService;
import com.askwinston.web.dto.*;
import com.askwinston.web.secuity.AwUserPrincipal;
import com.askwinston.web.secuity.JwtService;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/patient")
public class PatientController {
    private final UserService userService;
    private JwtService jwtService;
    private ParsingHelper parsingHelper;
    private DocumentService documentService;
    private PaymentService paymentService;

    @Autowired
    public PatientController(UserService userService,
                             JwtService jwtService,
                             ParsingHelper parsingHelper,
                             DocumentService documentService,
                             PaymentService paymentService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.parsingHelper = parsingHelper;
        this.documentService = documentService;
        this.paymentService = paymentService;
    }

    @PostMapping
    public TokenDto create(@Validated(UserDto.CreatePatientValidation.class) @RequestBody UserDto userDto) {
        if (userService.userEmailExists(userDto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This email is already registered");
        }
        User newUser = userService.create(parsingHelper.mapObject(userDto, User.class));
        String token = jwtService.createToken(newUser.getId(), newUser.getEmail(), newUser.getAuthority());
        userDto = parsingHelper.mapObject(newUser, UserDto.class);
        return new TokenDto(token, userDto);
    }

    @PostMapping("/shipping-address")
    @PreAuthorize("hasAnyAuthority('PATIENT')")
    @JsonView(DtoView.PatientVisibility.class)
    public ShippingAddressDto createShippingAddress(@RequestBody ShippingAddressDto shippingAddressDto,
                                                    @AuthenticationPrincipal AwUserPrincipal principal) {
        User user = userService.getById(principal.getId());
        ShippingAddress newShippingAddress = userService.createShippingAddress(user,
                parsingHelper.mapObject(shippingAddressDto, ShippingAddress.class));
        return parsingHelper.mapObject(newShippingAddress, ShippingAddressDto.class);
    }

    @PutMapping("/shipping-address")
    @PreAuthorize("hasAnyAuthority('PATIENT')")
    @JsonView(DtoView.PatientVisibility.class)
    public ShippingAddressDto updateShippingAddress(@RequestBody ShippingAddressDto shippingAddressDto,
                                                    @AuthenticationPrincipal AwUserPrincipal principal) {
        User user = userService.getById(principal.getId());
        ShippingAddress newShippingAddress = userService.updateShippingAddress(user,
                parsingHelper.mapObject(shippingAddressDto, ShippingAddress.class));
        return parsingHelper.mapObject(newShippingAddress, ShippingAddressDto.class);
    }

    @DeleteMapping("/shipping-address/{id}")
    @PreAuthorize("hasAnyAuthority('PATIENT')")
    public void deleteShippingAddress(@PathVariable("id") Long id, @AuthenticationPrincipal AwUserPrincipal principal) {
        User user = userService.getById(principal.getId());
        if (user.getShippingAddresses().stream().noneMatch(shippingAddress -> shippingAddress.getId().equals(id))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        userService.deleteShippingAddress(id);
    }

    @PutMapping("/shipping-address/primary/{id}")
    @PreAuthorize("hasAnyAuthority('PATIENT')")
    public List<ShippingAddressDto> setPrimaryShippingAddress(@PathVariable("id") long id,
                                                              @AuthenticationPrincipal AwUserPrincipal principal) {
        List<ShippingAddress> addresses = userService.setPrimaryShippingAddress(principal.getId(), id);
        return parsingHelper.mapObjects(addresses, ShippingAddressDto.class);
    }

    @PutMapping("/profile")
    @PreAuthorize("hasAnyAuthority('PATIENT')")
    @JsonView(DtoView.PatientVisibility.class)
    public UserDto updateMyProfile(@Validated(UserDto.UpdatePatientValidation.class) @RequestBody UserDto userDto,
                                   @AuthenticationPrincipal AwUserPrincipal principal) {
        userDto.setId(principal.getId());
        User updatedUser = userService.update(parsingHelper.mapObject(userDto, User.class));

        return parsingHelper.mapObject(updatedUser, UserDto.class);
    }

    @PutMapping(value = "/upload/id", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('PATIENT')")
    public Long uploadId(@AuthenticationPrincipal AwUserPrincipal principal,
                         @RequestParam MultipartFile file) throws IOException {
        Document document = documentService.saveIdDocument(principal.getId(), file.getOriginalFilename(), file.getContentType(), file.getBytes());
        return document.getId();
    }

    @PutMapping(value = "/upload/insurance", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('PATIENT')")
    public Long uploadInsurance(@AuthenticationPrincipal AwUserPrincipal principal,
                                @RequestParam MultipartFile file) throws IOException {
        Document document = documentService.saveInsuranceDocument(principal.getId(), file.getOriginalFilename(), file.getContentType(), file.getBytes());
        return document.getId();
    }

    @PostMapping("/billing-card")
    @PreAuthorize("hasAnyAuthority('PATIENT')")
    public BillingCardDto createBillingCard(@RequestBody BillingCardDto billingCardDto,
                                            @AuthenticationPrincipal AwUserPrincipal principal) {
        BillingCard newBillingCard = null;
        try {
            newBillingCard = paymentService.saveBillingCard(principal.getId(), billingCardDto);
        } catch (PaymentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        return parsingHelper.mapObject(newBillingCard, BillingCardDto.class);
    }

    @PutMapping("/billing-card/primary/{id}")
    @PreAuthorize("hasAnyAuthority('PATIENT')")
    public List<BillingCardDto> setPrimaryBillingCard(@PathVariable("id") String id,
                                                      @AuthenticationPrincipal AwUserPrincipal principal) {
        List<BillingCard> cards = userService.setPrimaryBillingCard(principal.getId(), id);
        return parsingHelper.mapObjects(cards, BillingCardDto.class);
    }

    @DeleteMapping("/billing-card/{id}")
    @PreAuthorize("hasAnyAuthority('PATIENT')")
    public void deleteBillingCard(@PathVariable("id") String id, @AuthenticationPrincipal AwUserPrincipal principal) {
        userService.deleteBillingCard(principal.getId(), id);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('DOCTOR', 'PHARMACIST')")
    @JsonView(DtoView.DoctorVisibility.class)
    public List<UserDto> getPatientsWithCompletedQuestionnaire() {
        List<User> users = userService.getByAuthority(User.Authority.PATIENT);
        return parsingHelper.mapObjects(users, UserDto.class);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @JsonView(DtoView.AdminVisibility.class)
    public List<UserDto> getUsersForAdmin() {
        List<User> users = userService.getForAdmin();
        return parsingHelper.mapObjects(users, UserDto.class);
    }
}
