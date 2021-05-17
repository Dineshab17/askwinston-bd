package com.askwinston.service.impl;

import com.askwinston.exception.AccountExistsException;
import com.askwinston.exception.UserException;
import com.askwinston.model.BillingCard;
import com.askwinston.model.Cart;
import com.askwinston.model.ShippingAddress;
import com.askwinston.model.User;
import com.askwinston.notification.NotificationEngine;
import com.askwinston.notification.NotificationEventTypeContainer;
import com.askwinston.repository.BillingCardRepository;
import com.askwinston.repository.CartRepository;
import com.askwinston.repository.ShippingAddressRepository;
import com.askwinston.repository.UserRepository;
import com.askwinston.service.EmailService;
import com.askwinston.service.TokenService;
import com.askwinston.service.UserService;
import com.askwinston.subscription.ProductSubscriptionRepository;
import com.askwinston.web.dto.GoogleLoginDto;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private static final int MIN_PASSWORD_LENGTH = 6;

    private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;
    private ShippingAddressRepository shippingAddressRepository;
    private BillingCardRepository billingCardRepository;
    private TokenService tokenService;
    private EmailService emailService;
    private NotificationEngine notificationEngine;
    private CartRepository cartRepository;
    private ProductSubscriptionRepository subscriptionRepository;

    public UserServiceImpl(PasswordEncoder passwordEncoder,
                           UserRepository userRepository,
                           ShippingAddressRepository shippingAddressRepository,
                           BillingCardRepository billingCardRepository,
                           TokenService tokenService,
                           EmailService emailService,
                           NotificationEngine notificationEngine,
                           CartRepository cartRepository,
                           ProductSubscriptionRepository subscriptionRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.shippingAddressRepository = shippingAddressRepository;
        this.billingCardRepository = billingCardRepository;
        this.tokenService = tokenService;
        this.emailService = emailService;
        this.notificationEngine = notificationEngine;
        this.cartRepository = cartRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    /**
     * @param email
     * @return boolean
     * To check whether the email id of the user is already exists
     */
    @Override
    public boolean userEmailExists(String email) {
        return !userRepository.findByEmail(email).isEmpty();
    }

    /**
     * @param id
     * @return User
     * To get the user details by user id
     */
    @Override
    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        );
    }

    /**
     * @param user
     * @return User
     * To create a new user
     */
    @Override
    public User create(User user) {
        Cart cart = new Cart();
        cart = cartRepository.save(cart);
        User newPatient = User.builder()
                .email(user.getEmail())
                .password(passwordEncoder.encode(user.getPassword()))
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .birthday(user.getBirthday())
                .utmSource(user.getUtmSource())
                .authority(User.Authority.PATIENT)
                .shippingAddresses(new ArrayList<>())
                .billingCards(new ArrayList<>())
                .province(user.getProvince())
                .timezone("America/Toronto")
                .loginType(user.getLoginType())
                .registrationDate(Date.from(Instant.now()))
                .cart(cart)
                .build();

        newPatient = userRepository.save(newPatient);

        notificationEngine.notify(NotificationEventTypeContainer.REGISTRATION, newPatient);
        log.info("New User created with user id: {}", newPatient.getId());
        return newPatient;
    }

    /**
     * @param user
     * @param shippingAddress
     * @return ShippingAddress
     * To create new shipping address for the patient
     */
    @Override
    public ShippingAddress createShippingAddress(User user, ShippingAddress shippingAddress) {
        boolean isPrimary = false;
        if (user.getShippingAddresses().isEmpty()) {
            isPrimary = true;
        }
        ShippingAddress newShippingAddress = ShippingAddress.builder()
                .addressLine1(shippingAddress.getAddressLine1())
                .addressLine2(shippingAddress.getAddressLine2())
                .addressCity(shippingAddress.getAddressCity())
                .addressCountry(shippingAddress.getAddressCountry())
                .addressProvince(shippingAddress.getAddressProvince())
                .addressPostalCode(shippingAddress.getAddressPostalCode())
                .user(user)
                .isPrimary(isPrimary)
                .build();
        return shippingAddressRepository.save(newShippingAddress);
    }

    /**
     * @param user
     * @param dto
     * @return ShippingAddress
     * To update existing shipping address of the patient
     */
    @Override
    public ShippingAddress updateShippingAddress(User user, ShippingAddress dto) {
        ShippingAddress shippingAddress = shippingAddressRepository.findById(dto.getId())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Shipping address not found"));
        if (!shippingAddress.getUser().equals(user)) {
            log.error("Provided Shipping Address is not belongs to the patient with id {}", user.getId());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This shipping address is not yours");
        }
        if (dto.getAddressProvince() != null) {
            shippingAddress.setAddressProvince(dto.getAddressProvince());
        }
        if (dto.getAddressCity() != null) {
            shippingAddress.setAddressCity(dto.getAddressCity());
        }
        if (dto.getAddressCountry() != null) {
            shippingAddress.setAddressCountry(dto.getAddressCountry());
        }
        if (dto.getAddressLine1() != null) {
            shippingAddress.setAddressLine1(dto.getAddressLine1());
        }
        if (dto.getAddressLine2() != null) {
            shippingAddress.setAddressLine2(dto.getAddressLine2());
        }
        if (dto.getAddressPostalCode() != null) {
            shippingAddress.setAddressPostalCode(dto.getAddressPostalCode());
        }
        return shippingAddressRepository.save(shippingAddress);
    }

    /**
     * @param id
     * To delete provider shipping address of the patient
     */
    @Override
    public void deleteShippingAddress(Long id) {
        ShippingAddress shippingAddress = shippingAddressRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Shipping address not found"));
        if (shippingAddress.isPrimary()) {
            log.error("Cannot delete primary shipping with id {}", id);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete primary shipping address.");
        }
        shippingAddressRepository.deleteById(id);
    }

    /**
     * @param user
     * @param billingCard
     * @return BillingCard
     * To add new billing card for the patient
     */
    @Override
    public BillingCard addBillingCard(User user, BillingCard billingCard) {
        boolean isPrimary;
        if (user.getBillingCards().isEmpty()) {
            isPrimary = true;
        } else {
            isPrimary = user.getBillingCards().stream()
                    .filter(BillingCard::getIsValid)
                    .noneMatch(BillingCard::isPrimary);
            if (isPrimary)
                resetPrimaryBillingCards(user.getId());
        }
        billingCard.setUser(user);
        billingCard.setPrimary(isPrimary);
        return billingCardRepository.save(billingCard);
    }

    /**
     * @param userId
     * @param id
     * To delete provided billing card for the patient
     */
    @Override
    public void deleteBillingCard(Long userId, String id) {
        List<BillingCard> cards = billingCardRepository.findByUserId(userId);
        cards.forEach(card -> {
            if (card.getId().equals(id)) {
                if (card.isPrimary()) {
                    log.error("Can't delete primary billing card for the user with id {}", userId);
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete primary billing card.");
                }
                billingCardRepository.deleteById(id);
                log.info("Billing card with id {} deleted for the user with id {}", id, userId);
            }
        });
    }

    /**
     * @param userId
     * @param id
     * @return List<BillingCard>
     * To set primary billing card for the patient
     */
    @Transactional
    @Override
    public List<BillingCard> setPrimaryBillingCard(Long userId, String id) {
        List<BillingCard> cards = billingCardRepository.findByUserId(userId);
        Optional<BillingCard> cardOptional = cards.stream()
                .filter(card -> Objects.equals(card.getId(), id))
                .findAny();
        if (cardOptional.isPresent()) {
            BillingCard card = cardOptional.get();
            if (Boolean.TRUE.equals(card.getIsValid())) {
                resetPrimaryBillingCards(userId);
                card.setPrimary(true);
                billingCardRepository.save(card);
                return cards;
            } else {
                log.error("Card is invalid for the user with id: {}", userId);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Card is invalid. Please select another card");
            }
        } else {
            log.error("User with id {} doesn't have billing card with id: {}", userId, id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User haven't billing card with id " + id);
        }
    }

    /**
     * @param userId
     * To reset primary billing card of the user
     */
    private void resetPrimaryBillingCards(Long userId) {
        List<BillingCard> cards = billingCardRepository.findByUserId(userId);
        cards.stream()
                .filter(BillingCard::isPrimary)
                .forEach(card -> {
                    card.setPrimary(false);
                    billingCardRepository.save(card);
                });
    }

    /**
     * @param userId
     * To reset primary shipping address of the user
     */
    private void resetPrimaryShippingAddresses(Long userId) {
        List<ShippingAddress> addresses = shippingAddressRepository.findByUserId(userId);
        addresses.stream()
                .filter(ShippingAddress::isPrimary)
                .forEach(address -> {
                    address.setPrimary(false);
                    shippingAddressRepository.save(address);
                });
        log.info("Primary shipping address has been reset for the user with id {}", userId);
    }

    /**
     * @param userId
     * @param id
     * @return List<ShippingAddress>
     * To set primary shipping address for the patient
     */
    @Transactional
    @Override
    public List<ShippingAddress> setPrimaryShippingAddress(Long userId, long id) {
        List<ShippingAddress> addresses = shippingAddressRepository.findByUserId(userId);
        log.info("Getting shipping address with id: {}", id);
        Optional<ShippingAddress> addressOptional = addresses.stream()
                .filter(address -> Objects.equals(address.getId(), id))
                .findAny();
        if (addressOptional.isPresent()) {
            resetPrimaryShippingAddresses(userId);
            ShippingAddress address = addressOptional.get();
            address.setPrimary(true);
            shippingAddressRepository.save(address);
            log.info("Shipping Address with id {} set as primary shipping address for the patient with id {}", id, userId);
            return addresses;
        } else {
            log.error("Patient with id {} haven't shipping address with id {}", userId, id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User haven't shipping address with id " + id);
        }
    }

    /**
     * @param userId
     * @param oldPassword
     * @param newPassword
     * To change the password of the user
     */
    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = getById(userId);
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            log.error("Current password is incorrect for the user with id {}", userId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong current password");
        }
        if (newPassword.length() < MIN_PASSWORD_LENGTH) {
            log.error("New Password is too short for the user with id {}", userId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New password is too short");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Password has been changed for the user with id {}", userId);
        notificationEngine.notify(NotificationEventTypeContainer.PASSWORD_RESET, user);
    }

    /**
     * @param userId
     * @param newPassword
     * To reset password of the user
     */
    @Override
    public void resetPassword(Long userId, String newPassword) {
        User user = getById(userId);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Password has been reset for the user with id {}", userId);
        notificationEngine.notify(NotificationEventTypeContainer.PASSWORD_RESET, user);
    }

    /**
     * @param user
     * @return User
     * To update User's profile information
     */
    @Override
    public User update(User user) {
        User updatedUser = getById(user.getId());
        if (user.getFirstName() != null) {
            updatedUser.setFirstName(user.getFirstName());
        }
        if (user.getLastName() != null) {
            updatedUser.setLastName(user.getLastName());
        }
        if (user.getPhone() != null) {
            updatedUser.setPhone(user.getPhone());
        }
        if (user.getBirthday() != null) {
            updatedUser.setBirthday(user.getBirthday());
        }
        if (user.getProvince() != null) {
            updatedUser.setProvince(user.getProvince());
        }
        if (user.getTimezone() != null) {
            updatedUser.setTimezone(user.getTimezone());
        }
        if(user.getUtmSource() != null){
            updatedUser.setUtmSource(user.getUtmSource());
        }
        log.info("Updating profile of user with user id: {}", user.getId());
        return userRepository.save(updatedUser);
    }

    /**
     * @param authority
     * @return List<User>
     * To get the user based on authority
     */
    @Override
    public List<User> getByAuthority(User.Authority authority) {
        return userRepository.findByAuthority(authority);
    }

    /**
     * @return List<User>
     * To get all the patients without subscriptions
     */
    @Override
    public List<User> getForAdmin() {
        List<User> users = new LinkedList<>();
        userRepository.findAll().forEach(users::add);
        return users.stream()
                .filter(user -> user.getAuthority().equals(User.Authority.PATIENT))
                .filter(user -> subscriptionRepository.findAllByUser(user).isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * @return User
     * To get all the doctors details
     */
    @Override
    public User getDefaultDoctor() {
        List<User> doctors = userRepository.findByAuthority(User.Authority.DOCTOR);
        if (doctors.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There are no doctors in the system");
        }
        return doctors.get(0);
    }

    /**
     * @param email
     * To send reset password email to the user
     */
    @Override
    public void sendResetPasswordEmail(String email) {
        List<User> users = userRepository.findByEmail(email);
        if (users.isEmpty()) {
            log.error("Email id is not associated with this account {}", email);
            throw new UserException("Your email address may not be associated with an account, please try again.");
        }
        User user = users.get(0);
        String token = tokenService.createResetPasswordToken(user.getId());
        log.info("Token generated for reset password for user with id {}", user.getId());
        emailService.sendResetPasswordEmail(email, token);
    }

    /**
     * @param googleLoginDto
     * @return
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public User addGoogleUser(GoogleLoginDto googleLoginDto, boolean isLogin) throws GeneralSecurityException, IOException {

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
                // Specify the CLIENT_ID of the app that accesses the backend:
                .setAudience(Collections.singletonList("352488626350-84em0ce1n2d5kujjbtseid3l4koas88p.apps.googleusercontent.com"))
                // Or, if multiple clients access the backend:
                //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
                .build();


        GoogleIdToken idToken = verifier.verify(googleLoginDto.getTokenId());
        GoogleIdToken.Payload payload = idToken.getPayload();
        // Print user identifier
        String userId = payload.getSubject();
        log.info("Google User ID: " + userId);
        // Get profile information from payload
        User user = this.userRepository.findByEmailAndLoginTypeIn(payload.getEmail(), Arrays.asList(User.LoginType.GOOGLE,User.LoginType.CUSTOM_GOOGLE));
        if(user!=null && isLogin){
            return user;
        }
        else if(user!=null && !isLogin && user.getLoginType().equals(User.LoginType.GOOGLE)){
            log.error("Patient is already registered with this email {} via google signup", payload.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Use Google Login Since you have already registered with Google Signup");
        }
        else if (this.userEmailExists(payload.getEmail())) {
            log.error("Patient is already registered with this email {} via custom login", payload.getEmail());
            throw new AccountExistsException("Your Email id has been already registered with Askwinston", payload.getEmail());
        } else
        {
            user = new User();
            user.setEmail(payload.getEmail());
            user.setLoginType(User.LoginType.GOOGLE);
            user.setPassword("");
            user.setUtmSource(googleLoginDto.getUtmSource());
            return  this.create(user);
        }
    }
}