package com.portondelapalma.CartService.service;

import com.portondelapalma.CartService.dto.CartDto;
import com.portondelapalma.CartService.dto.ProductDto;
import com.portondelapalma.CartService.jwt.JwtUtils;
import com.portondelapalma.CartService.model.Cart;
import com.portondelapalma.CartService.repository.ICartRepository;
import com.portondelapalma.CartService.repository.IProductAPI;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.List;
import java.util.logging.Logger;

@Service
public class CartService implements ICartService {

    @Autowired
    private ICartRepository iCartRepository;

    @Autowired
    private IProductAPI iProductAPI;

    @Autowired
    private JwtUtils jwtUtils;

    private final Logger logger = Logger.getLogger(CartService.class.getName());


    @Override
    public Long createCart(Cart cart) {
        Cart cart2 = iCartRepository.save(cart);
        logger.info("Carrito creado");
        return cart2.getId();
    }

    @Override
    public CartDto findCart(Long idcart) {
        return iCartRepository.findById(idcart)
                .orElseThrow(() -> new RuntimeException("Cart not found"))
                .toDto();
    }

    public void addProductCookie(Long idProduct, HttpServletRequest request) {
        Cookie[] cookieArray = request.getCookies();
        if (cookieArray == null) {
            throw new RuntimeException("Cookies not found");
        }
        List<Cookie> cookies = List.of(cookieArray);
        String token = cookies.stream()
                .filter(cookie -> cookie.getName().equals("token"))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new RuntimeException("Token not found"));
        if (jwtUtils.isExpired(token)) {
            throw new RuntimeException("Token expired");
        }
        CartDto cart = findCart(jwtUtils.getCartFromToken(token));
        ProductDto productDto = iProductAPI.getProductById(idProduct);
        cart.addProductToCart(productDto.getNameProduct(), productDto.getPrice());
        this.createCart(cart.toEntity());
        logger.info("Product added to cart");
    }

    public void deleteProductCookie(String nameProduct, HttpServletRequest request) {
        Cookie[] cookieArray = request.getCookies();
        if (cookieArray == null) {
            throw new RuntimeException("Cookies not found");
        }
        List<Cookie> cookies = List.of(cookieArray);
        String token = cookies.stream()
                .filter(cookie -> cookie.getName().equals("token"))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new RuntimeException("Token not found"));
        if (jwtUtils.isExpired(token)) {
            throw new RuntimeException("Token expired");
        }
        CartDto cart = findCart(jwtUtils.getCartFromToken(token));
        ProductDto productDto = iProductAPI.getProductByName(nameProduct);
        cart.deleteProductFromCart(productDto.getNameProduct(), productDto.getPrice());
        this.createCart(cart.toEntity());
        logger.info("Product deleted from cart");
    }

    public void emptyCart(String token) {
        if (jwtUtils.isExpired(token)) {
            throw new RuntimeException("Token expired");
        }
        CartDto cart = findCart(jwtUtils.getCartFromToken(token));
        cart.emptyCart();
        this.createCart(cart.toEntity());
        logger.info("Cart emptied");
    }
}
