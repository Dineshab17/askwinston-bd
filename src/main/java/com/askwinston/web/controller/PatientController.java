package com.askwinston.web.controller;

import com.askwinston.exception.PaymentException;
import com.askwinston.helper.ParsingHelper;
import com.askwinston.model.BillingCard;
import com.askwinston.model.Document;
import com.askwinston.model.ShippingAddress;
import com.askwinston.model.User;
import com.askwinston.order.OrderEngine;
import com.askwinston.service.PaymentService;
import com.askwinston.service.UserService;
import com.askwinston.service.impl.DocumentService;
import com.askwinston.web.dto.*;
import com.askwinston.web.secuity.AwUserPrincipal;
import com.askwinston.web.secuity.JwtService;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/patient")
@Slf4j
public class PatientController {
    private final UserService userService;
    private JwtService jwtService;
    private ParsingHelper parsingHelper;
    private DocumentService documentService;
    private PaymentService paymentService;
    private OrderEngine orderEngine;

    @Autowired
    public PatientController(UserService userService,
                             JwtService jwtService,
                             ParsingHelper parsingHelper,
                             DocumentService documentService,
                             PaymentService paymentService,
                             OrderEngine orderEngine) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.parsingHelper = parsingHelper;
        this.documentService = documentService;
        this.paymentService = paymentService;
        this.orderEngine = orderEngine;
    }

    /**
     * @param userDto
     * @return TokenDto
     * To create or register a new patient
     */
    @PostMapping
    public TokenDto create(@Validated(UserDto.CreatePatientValidation.class) @RequestBody UserDto userDto) {
        if (userService.userEmailExists(userDto.getEmail())) {
            log.error("Patient is already registered with this email {}", userDto.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This email is already registered");
        }
        User newUser = userService.create(parsingHelper.mapObject(userDto, User.class));
        String token = jwtService.createToken(newUser.getId(), newUser.getEmail(), newUser.getAuthority());
        userDto = parsingHelper.mapObject(newUser, UserDto.class);
        return new TokenDto(token, userDto);
    }

    /**
     * @param shippingAddressDto
     * @param principal
     * @return ShippingAddressDto
     * To create new shipping address for the patient
     */
    @PostMapping("/shipping-address")
    @PreAuthorize("hasAnyAuthority('PATIENT')")
    @JsonView(DtoView.PatientVisibility.class)
    public ShippingAddressDto createShippingAddress(@RequestBody ShippingAddressDto shippingAddressDto,
                                                    @AuthenticationPrincipal AwUserPrincipal principal) {
        User user = userService.getById(principal.getId());
        ShippingAddress newShippingAddress = userService.createShippingAddress(user,
                parsingHelper.mapObject(shippingAddressDto, ShippingAddress.class));
        this.orderEngine.updateShippingAddressToOrder(user.getId(), newShippingAddress);
        log.info("New Shipping Address created for the user with id{}", principal.getId());
        return parsingHelper.mapObject(newShippingAddress, ShippingAddressDto.class);
    }

    /**
     * @param shippingAddressDto
     * @param principal
     * @return ShippingAddressDto
     * To update existing shipping address of the patient
     */
    @PutMapping("/shipping-address")
    @PreAuthorize("hasAnyAuthority('PATIENT')")
    @JsonView(DtoView.PatientVisibility.class)
    public ShippingAddressDto updateShippingAddress(@RequestBody ShippingAddressDto shippingAddressDto,
                                                    @AuthenticationPrincipal AwUserPrincipal principal) {
        User user = userService.getById(principal.getId());
        ShippingAddress newShippingAddress = userService.updateShippingAddress(user,
                parsingHelper.mapObject(shippingAddressDto, ShippingAddress.class));
        log.info("Shipping Address is updated for the patient {}", principal.getId());
        return parsingHelper.mapObject(newShippingAddress, ShippingAddressDto.class);
    }

    /**
     * @param id
     * @param principal
     * To delete provided shipping address of the patient
     */
    @DeleteMapping("/shipping-address/{id}")
    @PreAuthorize("hasAnyAuthority('PATIENT')")
    public void deleteShippingAddress(@PathVariable("id") Long id, @AuthenticationPrincipal AwUserPrincipal principal) {
        User user = userService.getById(principal.getId());
        if (user.getShippingAddresses().stream().noneMatch(shippingAddress -> shippingAddress.getId().equals(id))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        userService.deleteShippingAddress(id);
    }

    /**
     * @param id
     * @param principal
     * @return List<ShippingAddressDto>
     * To set selected shipping address as primary Shipping Address
     */
    @PutMapping("/shipping-address/primary/{id}")
    @PreAuthorize("hasAnyAuthority('PATIENT')")
    public List<ShippingAddressDto> setPrimaryShippingAddress(@PathVariable("id") long id,
                                                              @AuthenticationPrincipal AwUserPrincipal principal) {
        List<ShippingAddress> addresses = userService.setPrimaryShippingAddress(principal.getId(), id);
        Optional<ShippingAddress> addressOptional = addresses.stream()
                .filter(address -> Objects.equals(address.getId(), id))
                .findAny();
        if (addressOptional.isPresent()) {
            this.orderEngine.updateShippingAddressToOrder(principal.getId(), addressOptional.get());
        }
        return parsingHelper.mapObjects(addresses, ShippingAddressDto.class);
    }

    /**
     * @param userDto
     * @param principal
     * @return UserDto
     * To update profile of the patient
     */
    @PutMapping("/profile")
    @PreAuthorize("hasAnyAuthority('PATIENT')")
    @JsonView(DtoView.PatientVisibility.class)
    public UserDto updateMyProfile(@Validated(UserDto.UpdatePatientValidation.class) @RequestBody UserDto userDto,
                                   @AuthenticationPrincipal AwUserPrincipal principal) {
        userDto.setId(principal.getId());
        User updatedUser = userService.update(parsingHelper.mapObject(userDto, User.class));

        return parsingHelper.mapObject(updatedUser, UserDto.class);
    }

    /**
     * @param principal
     * @param file
     * @return Long
     * @throws IOException
     * To upload Id proof document of the patient
     */
    @PutMapping(value = "/upload/id", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('PATIENT')")
    public Long uploadId(@AuthenticationPrincipal AwUserPrincipal principal,
                         @RequestParam MultipartFile file) throws IOException {
        Document document = documentService.saveIdDocument(principal.getId(), file.getOriginalFilename(), file.getContentType(), file.getBytes());
        return document.getId();
    }

    /**
     * @param principal
     * @param file
     * @return Long
     * @throws IOException
     * To upload Insurance document of the patient
     */
    @PutMapping(value = "/upload/insurance", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('PATIENT')")
    public Long uploadInsurance(@AuthenticationPrincipal AwUserPrincipal principal,
                                @RequestParam MultipartFile file) throws IOException {
        Document document = documentService.saveInsuranceDocument(principal.getId(), file.getOriginalFilename(), file.getContentType(), file.getBytes());
        return document.getId();
    }

    /**
     * @param billingCardDto
     * @param principal
     * @return BillingCardDto
     * To create new billing card information for the patient
     */
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

    /**
     * @param id
     * @param principal
     * @return List<BillingCardDto>
     * To set provided billing card as primary for the patient
     */
    @PutMapping("/billing-card/primary/{id}")
    @PreAuthorize("hasAnyAuthority('PATIENT')")
    public List<BillingCardDto> setPrimaryBillingCard(@PathVariable("id") String id,
                                                      @AuthenticationPrincipal AwUserPrincipal principal) {
        List<BillingCard> cards = userService.setPrimaryBillingCard(principal.getId(), id);
        return parsingHelper.mapObjects(cards, BillingCardDto.class);
    }

    /**
     * @param id
     * @param principal
     * To delete selected billing card for the patient
     */
    @DeleteMapping("/billing-card/{id}")
    @PreAuthorize("hasAnyAuthority('PATIENT')")
    public void deleteBillingCard(@PathVariable("id") String id, @AuthenticationPrincipal AwUserPrincipal principal) {
        userService.deleteBillingCard(principal.getId(), id);
    }

    /**
     * @return List<UserDto>
     * Get all the patient information
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('DOCTOR', 'PHARMACIST')")
    @JsonView(DtoView.DoctorVisibility.class)
    public List<UserDto> getPatientsWithCompletedQuestionnaire() {
        List<User> users = userService.getByAuthority(User.Authority.PATIENT);
        return parsingHelper.mapObjects(users, UserDto.class);
    }

    /**
     * @return List<UserDto>
     * To get patients information by admin
     */
    @GetMapping("/admin")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @JsonView(DtoView.AdminVisibility.class)
    public List<UserDto> getUsersForAdmin() {
        List<User> users = userService.getForAdmin();
        return parsingHelper.mapObjects(users, UserDto.class);
    }
}
