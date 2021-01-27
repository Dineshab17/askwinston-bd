package com.askwinston.service.impl;

import com.askwinston.exception.ShoppingCartException;
import com.askwinston.model.Cart;
import com.askwinston.model.CartItem;
import com.askwinston.model.User;
import com.askwinston.repository.CartItemRepository;
import com.askwinston.repository.CartRepository;
import com.askwinston.repository.UserRepository;
import com.askwinston.service.CartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;

    private static final String USER_NOT_FOUND_MESSAGE = "User not found";

    public CartServiceImpl(CartRepository cartRepository,
                           UserRepository userRepository,
                           CartItemRepository cartItemRepository) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.cartItemRepository = cartItemRepository;
    }

    /**
     * @param userId
     * @return Cart
     * To get details of items in the cart for the patient
     */
    @Override
    @Transactional
    public Cart getCartForUser(long userId) {
        log.info("Getting cart(products in cart) details for the patient with user id: {}", userId);
        User patient = userRepository.findById(userId).orElseThrow(
                () -> new ShoppingCartException(USER_NOT_FOUND_MESSAGE)
        );
        return patient.getCart();
    }

    /**
     * @param userId
     * @param cartItems
     * @return Cart
     * To update the items in the cart to new items for the patient
     */
    @Override
    @Transactional
    public Cart updateCart(long userId, List<CartItem> cartItems) {
        log.info("Getting cart information of the patient with user id: {}", userId);
        User patient = userRepository.findById(userId).orElseThrow(
                () -> new ShoppingCartException(USER_NOT_FOUND_MESSAGE)
        );
        Cart cart = patient.getCart();
        log.info("Updating new cartItems to the cart of the patient with user id: {}", userId);
        cartItems.forEach(cartItem -> addOrUpdate(cart, cartItem));
        return cartRepository.save(cart);
    }

    /**
     * @param cart
     * @param cartItem
     * To add or update items to the cart for the patient
     */
    private void addOrUpdate(Cart cart, CartItem cartItem) {
        cartItem.setCart(cart);
        List<CartItem> cartItems = cart.getItems();
        Optional<CartItem> cartItemOptional = cartItems.stream().filter(cartItem1 -> Objects.equals(cartItem1.getProductId(), cartItem.getProductId())).findAny();
        if (cartItemOptional.isPresent()) {
            cartItemOptional.get().setProductQuantityId(cartItem.getProductQuantityId());
            cartItemOptional.get().setProductCount(cartItem.getProductCount());
        } else {
            cart.getItems().add(cartItem);
        }
    }

    /**
     * @param userId
     * @param cartItem
     * @return Cart
     * To delete selected cart item from patient's cart
     */
    @Override
    @Transactional
    public Cart deleteFromCart(long userId, CartItem cartItem) {
        User patient = userRepository.findById(userId).orElseThrow(
                () -> new ShoppingCartException(USER_NOT_FOUND_MESSAGE)
        );
        Cart cart = patient.getCart();
        Optional<CartItem> cartItemOptional = cart.getItems().stream()
                .filter(cartItem1 -> Objects.equals(cartItem1.getProductId(), cartItem.getProductId()))
                .findFirst();
        log.info("Check the selected cart item present in the patient's cart with user id: {}", userId);
        if (cartItemOptional.isPresent()) {
            cartItemRepository.delete(cartItemOptional.get());
            cart.getItems().remove(cartItemOptional.get());
            log.info("Cart item with product id {} removed from cart of the patient with user id: {}", cartItem.getProductId(), userId);
            return cartRepository.save(cart);
        } else {
            log.error("Cart item with product id {} is not found ", cartItem.getProductId());
            throw new ShoppingCartException("Cart item with productId " + cartItem.getProductId() + " not found");
        }
    }

    /**
     * @param userId
     * @return Cart
     * To Clear all the item from the cart for the patient
     */
    @Override
    public Cart clearCart(long userId) {
        User patient = userRepository.findById(userId).orElseThrow(
                () -> new ShoppingCartException(USER_NOT_FOUND_MESSAGE)
        );
        Cart cart = patient.getCart();
        log.info("Clearing all the item from the cart of the patient with id: {}", userId);
        cart.getItems().forEach(cartItemRepository::delete);
        cart.getItems().clear();
        return cartRepository.save(cart);
    }

}
