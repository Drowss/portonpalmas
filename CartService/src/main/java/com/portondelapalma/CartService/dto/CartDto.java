package com.portondelapalma.CartService.dto;

import com.portondelapalma.CartService.model.Cart;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Builder
@Data
public class CartDto {
    private Long idCart;
    private Map<String, Integer> items;
    private Long total;

    public Cart toEntity() {
        return Cart.builder()
                .id(idCart)
                .items(items)
                .total(total)
                .build();
    }

    public void addProductToCart(String productName, Long productPrice) {
        this.items.put(productName, this.items.getOrDefault(productName, 0) + 1);
        this.total += productPrice;
    }

    public void deleteProductFromCart(String nameProduct, Long productPrice) {
        Integer count = this.items.get(nameProduct);
        if (count == null) {
            throw new IllegalArgumentException("Product not in cart: " + nameProduct);
        } else if (count > 1) {
            this.items.put(nameProduct, count - 1);
        } else {
            this.items.remove(nameProduct);
        }
        this.total -= productPrice;
    }
}
