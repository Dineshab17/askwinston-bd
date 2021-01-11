package com.askwinston.service.impl;

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
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
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

    @Override
    public boolean userEmailExists(String email) {
        return !userRepository.findByEmail(email).isEmpty();
    }

    @Override
    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        );
    }

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
                .authority(User.Authority.PATIENT)
                .shippingAddresses(new ArrayList<>())
                .billingCards(new ArrayList<>())
                .province(user.getProvince())
                .timezone("America/Toronto")
                .registrationDate(Date.from(Instant.now()))
                .cart(cart)
                .build();

        newPatient = userRepository.save(newPatient);

        notificationEngine.notify(NotificationEventTypeContainer.REGISTRATION, newPatient);

        return newPatient;
    }

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

    @Override
    public ShippingAddress updateShippingAddress(User user, ShippingAddress dto) {
        ShippingAddress shippingAddress = shippingAddressRepository.findById(dto.getId())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Shipping address not found"));
        if (!shippingAddress.getUser().equals(user)) {
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

    @Override
    public void deleteShippingAddress(Long id) {
        ShippingAddress shippingAddress = shippingAddressRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Shipping address not found"));
        if (shippingAddress.isPrimary()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete primary shipping address.");
        }
        shippingAddressRepository.deleteById(id);
    }

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

    @Override
    public void deleteBillingCard(Long userId, String id) {
        List<BillingCard> cards = billingCardRepository.findByUserId(userId);
        cards.forEach(card -> {
            if (card.getId().equals(id)) {
                if (card.isPrimary()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete primary billing card.");
                }
                billingCardRepository.deleteById(id);
            }
        });
    }

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
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Card is invalid. Please select another card");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User haven't billing card with id " + id);
        }
    }

    private void resetPrimaryBillingCards(Long userId) {
        List<BillingCard> cards = billingCardRepository.findByUserId(userId);
        cards.stream()
                .filter(BillingCard::isPrimary)
                .forEach(card -> {
                    card.setPrimary(false);
                    billingCardRepository.save(card);
                });
    }

    private void resetPrimaryShippingAddresses(Long userId) {
        List<ShippingAddress> addresses = shippingAddressRepository.findByUserId(userId);
        addresses.stream()
                .filter(ShippingAddress::isPrimary)
                .forEach(address -> {
                    address.setPrimary(false);
                    shippingAddressRepository.save(address);
                });
    }

    @Transactional
    @Override
    public List<ShippingAddress> setPrimaryShippingAddress(Long userId, long id) {
        List<ShippingAddress> addresses = shippingAddressRepository.findByUserId(userId);
        Optional<ShippingAddress> addressOptional = addresses.stream()
                .filter(address -> Objects.equals(address.getId(), id))
                .findAny();
        if (addressOptional.isPresent()) {
            resetPrimaryShippingAddresses(userId);
            ShippingAddress address = addressOptional.get();
            address.setPrimary(true);
            shippingAddressRepository.save(address);
            return addresses;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User haven't shipping address with id " + id);
        }
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = getById(userId);
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong current password");
        }
        if (newPassword.length() < MIN_PASSWORD_LENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New password is too short");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        notificationEngine.notify(NotificationEventTypeContainer.PASSWORD_RESET, user);
    }

    @Override
    public void resetPassword(Long userId, String newPassword) {
        User user = getById(userId);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        notificationEngine.notify(NotificationEventTypeContainer.PASSWORD_RESET, user);
    }

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
        return userRepository.save(updatedUser);
    }

    @Override
    public List<User> getByAuthority(User.Authority authority) {
        return userRepository.findByAuthority(authority);
    }

    @Override
    public List<User> getForAdmin() {
        List<User> users = new LinkedList<>();
        userRepository.findAll().forEach(users::add);
        return users.stream()
                .filter(user -> user.getAuthority().equals(User.Authority.PATIENT))
                .filter(user -> subscriptionRepository.findAllByUser(user).isEmpty())
                .collect(Collectors.toList());
    }

    @Override
    public User getDefaultDoctor() {
        List<User> doctors = userRepository.findByAuthority(User.Authority.DOCTOR);
        if (doctors.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There are no doctors in the system");
        }
        return doctors.get(0);
    }

    @Override
    public void sendResetPasswordEmail(String email) {
        List<User> users = userRepository.findByEmail(email);
        if (users.isEmpty()) {
            throw new UserException("Your email address may not be associated with an account, please try again.");
        }
        User user = users.get(0);
        String token = tokenService.createResetPasswordToken(user.getId());
        emailService.sendResetPasswordEmail(email, token);
    }
}