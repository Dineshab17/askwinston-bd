package com.askwinston.service;

import com.askwinston.model.BillingCard;
import com.askwinston.model.ShippingAddress;
import com.askwinston.model.User;
import com.askwinston.web.dto.GoogleLoginDto;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public interface UserService {

    User getById(Long id);

    boolean userEmailExists(String email);

    User create(User user);

    ShippingAddress createShippingAddress(User user, ShippingAddress shippingAddress);

    void deleteShippingAddress(Long id);

    User update(User user);

    BillingCard addBillingCard(User user, BillingCard billingCard);

    void deleteBillingCard(Long userId, String id);

    List<User> getByAuthority(User.Authority authority);

    List<User> getForAdmin();

    User getDefaultDoctor();

    void sendResetPasswordEmail(String email);

    List<BillingCard> setPrimaryBillingCard(Long userId, String id);

    List<ShippingAddress> setPrimaryShippingAddress(Long userId, long id);

    void changePassword(Long userId, String oldPassword, String newPassword);

    void resetPassword(Long userId, String newPassword);

    ShippingAddress updateShippingAddress(User user, ShippingAddress dto);

    User addGoogleUser(GoogleLoginDto googleLoginDto, boolean isLogin) throws GeneralSecurityException, IOException;
}
