package com.portondelapalma.CartService.service;

import com.portondelapalma.CartService.dto.CartDto;
import com.portondelapalma.CartService.dto.ProductDto;
import com.portondelapalma.CartService.model.Cart;
import com.portondelapalma.CartService.repository.ICartRepository;
import com.portondelapalma.CartService.repository.IProductAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class CartService implements ICartService {

    @Autowired
    private ICartRepository iCartRepository;

    @Autowired
    private IProductAPI iProductAPI;

    private final Logger logger = Logger.getLogger(CartService.class.getName());


    @Override
    public Long createCart(Cart cart) {
        iCartRepository.save(cart);
        logger.info("Carrito creado");
        return cart.getId();
    }

    @Override
    public CartDto findCart(Long idcart) {
        return iCartRepository.findById(idcart)
                .orElseThrow(() -> new RuntimeException("Cart not found"))
                .toDto();
    }

    @Override
    public void addProductToCart(Long idCart, Long idProduct) {
        CartDto cart = findCart(idCart);
        ProductDto productDto = iProductAPI.getProductById(idProduct);
        cart.addProductToCart(productDto.getNameProduct(), productDto.getPrice());
        this.createCart(cart.toEntity());
        logger.info("Producto añadido al carrito");
    }

    @Override
    public void deleteProductFromCart(Long idCart, String nameProduct) {
        CartDto cart = findCart(idCart);
        ProductDto productDto = iProductAPI.getProductByName(nameProduct);
        cart.deleteProductFromCart(productDto.getNameProduct(), productDto.getPrice());
        this.createCart(cart.toEntity());
        logger.info("Producto eliminado del carrito");
    }
}
