package com.portondelapalma.CartService.controller;

import com.portondelapalma.CartService.dto.CartDto;
import com.portondelapalma.CartService.model.Cart;
import com.portondelapalma.CartService.service.ICartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("cart/v1")
public class CartController {

    @Autowired
    private ICartService iCartService;

    @PutMapping("/add-product")
    public void addProductToCart(@RequestParam Long idCart,@RequestParam Long idProduct) {
        iCartService.addProductToCart(idCart, idProduct);
    }

    @PutMapping("/delete-product")
    public void deleteProductFromCart(@RequestParam Long idCart,@RequestParam String nameProduct) {
        iCartService.deleteProductFromCart(idCart, nameProduct);
    }

    @PostMapping("/create")
    public Long createCart(@RequestBody Cart cart) {
        return iCartService.createCart(cart);
    }

    @GetMapping("/get-cart")
    public CartDto getCart(Long idCart) {
        return iCartService.findCart(idCart);
    }
}
