package com.portondelapalma.CartService.controller;

import com.portondelapalma.CartService.dto.CartDto;
import com.portondelapalma.CartService.model.Cart;
import com.portondelapalma.CartService.service.ICartService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("cart/v1")
public class CartController {

    @Autowired
    private ICartService iCartService;

    @PutMapping("/add-product")
    public void addProductCookie(@RequestParam Long idProduct, HttpServletRequest request) {
        iCartService.addProductCookie(idProduct, request);
    }

    @PutMapping("/delete-product")
    public void deleteProductFromCart(@RequestParam String nameProduct,HttpServletRequest request) {
        iCartService.deleteProductCookie(nameProduct, request);
    }

    @PostMapping("/create")
    public Long createCart(@RequestBody Cart cart) {
        return iCartService.createCart(cart);
    }

    @GetMapping("/get-cart")
    public CartDto getCart(@RequestParam Long idCart) {
        return iCartService.findCart(idCart);
    }

    @PutMapping("/empty-cart")
    public void emptyCart(@RequestParam String token) {
        iCartService.emptyCart(token);
    }
}
