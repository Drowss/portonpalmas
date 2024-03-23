package com.portondelapalma.CartService.service;

import com.portondelapalma.CartService.dto.CartDto;
import com.portondelapalma.CartService.model.Cart;
import jakarta.servlet.http.HttpServletRequest;

public interface ICartService {
    Long createCart(Cart cart);
    CartDto findCart(Long idCart);

    void addProductCookie(Long idProduct, HttpServletRequest request);
    void deleteProductCookie(String name, HttpServletRequest request);
    void emptyCart(String token);
    CartDto getCartFromCookie(HttpServletRequest request);
}
