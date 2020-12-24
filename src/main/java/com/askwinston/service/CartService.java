package com.askwinston.service;

import com.askwinston.model.Cart;
import com.askwinston.model.CartItem;

import java.util.List;

public interface CartService {

    Cart getCartForUser(long userId);

    Cart updateCart(long userId, List<CartItem> cartItems);

    Cart deleteFromCart(long userId, CartItem cartItem);

    Cart clearCart(long userId);
}
