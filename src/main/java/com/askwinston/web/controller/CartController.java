package com.askwinston.web.controller;

import com.askwinston.helper.ParsingHelper;
import com.askwinston.model.Cart;
import com.askwinston.model.CartItem;
import com.askwinston.service.CartService;
import com.askwinston.web.dto.CartDto;
import com.askwinston.web.dto.CartItemDto;
import com.askwinston.web.dto.DtoView;
import com.askwinston.web.secuity.AwUserPrincipal;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user-cart")
public class CartController {

    private final CartService cartService;
    private ParsingHelper parsingHelper;

    public CartController(CartService cartService, ParsingHelper parsingHelper) {
        this.cartService = cartService;
        this.parsingHelper = parsingHelper;
    }

    /**
     * @param principal
     * @return CartDto
     * To get items in the cart of the patient
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('PATIENT')")
    @JsonView(DtoView.PatientVisibility.class)
    public CartDto getCart(@AuthenticationPrincipal AwUserPrincipal principal) {
        return parsingHelper.mapObject(cartService.getCartForUser(principal.getId()), CartDto.class);
    }

    /**
     * @param cartItemDtoList
     * @param principal
     * @return CartDto
     * To update items in the cart for the patient
     */
    @PutMapping
    @PreAuthorize("hasAnyAuthority('PATIENT')")
    @JsonView(DtoView.PatientVisibility.class)
    public CartDto updateCart(@RequestBody List<CartItemDto> cartItemDtoList,
                              @AuthenticationPrincipal AwUserPrincipal principal) {
        long id = principal.getId();
        Cart updatedCart = cartService.updateCart(id, parsingHelper.mapObjects(cartItemDtoList, CartItem.class));
        return parsingHelper.mapObject(updatedCart, CartDto.class);
    }

    /**
     * @param cartItemDto
     * @param principal
     * @return CartDto
     * To delete particular item from cart for patient
     */
    @DeleteMapping
    @PreAuthorize("hasAnyAuthority('PATIENT')")
    @JsonView(DtoView.PatientVisibility.class)
    public CartDto deleteFromCart(@RequestBody CartItemDto cartItemDto,
                                  @AuthenticationPrincipal AwUserPrincipal principal) {
        Long id = principal.getId();
        Cart updatedCart = cartService.deleteFromCart(id, parsingHelper.mapObject(cartItemDto, CartItem.class));
        return parsingHelper.mapObject(updatedCart, CartDto.class);
    }

    /**
     * @param principal
     * @return CartDto
     * To Clear all items in the cart for the patient
     */
    @PostMapping("/clear")
    @PreAuthorize("hasAnyAuthority('PATIENT')")
    @JsonView(DtoView.PatientVisibility.class)
    public CartDto clearCart(@AuthenticationPrincipal AwUserPrincipal principal) {
        return parsingHelper.mapObject(cartService.clearCart(principal.getId()), CartDto.class);
    }
}
