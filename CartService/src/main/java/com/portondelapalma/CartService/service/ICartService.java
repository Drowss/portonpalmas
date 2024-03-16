package com.portondelapalma.CartService.service;

import com.portondelapalma.CartService.dto.CartDto;
import com.portondelapalma.CartService.model.Cart;

public interface ICartService {
    Long createCart(Cart cart);
    CartDto findCart(Long idcart);
    void addProductToCart(Long idCart, Long idProduct);
    void deleteProductFromCart(Long idCart, String nameProduct);
}
