package com.portondelapalma.CartService.service;

import com.portondelapalma.CartService.dto.CartDto;
import com.portondelapalma.CartService.dto.ProductDto;
import com.portondelapalma.CartService.jwt.JwtUtils;
import com.portondelapalma.CartService.model.Cart;
import com.portondelapalma.CartService.repository.ICartRepository;
import com.portondelapalma.CartService.repository.IProductAPI;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {
    @Mock
    private ICartRepository iCartRepository;

    @Mock
    private IProductAPI iProductAPI;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private CartService cartService;

    @Test
    void testCreateCart() {
        Cart cart = new Cart();
        cart.setId(1L);
        when(iCartRepository.save(cart)).thenReturn(cart);

        Long cartId = cartService.createCart(cart);

        assertEquals(1L, cartId);
        verify(iCartRepository, times(1)).save(cart);
    }

    @Test
    void testFindCart_Success() {
        // Arrange
        Cart cart = new Cart();
        cart.setId(1L);
        when(iCartRepository.findById(1L)).thenReturn(Optional.of(cart));

        // Act
        CartDto cartDto = cartService.findCart(1L);

        // Assert
        assertNotNull(cartDto);
        assertEquals(1L, cartDto.getIdCart());
    }

    @Test
    void testFindCart_NotFound() {
        when(iCartRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cartService.findCart(1L);
        });
        assertEquals("Cart not found", exception.getMessage());
    }

    @Test
    void testAddProductCookie() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Cookie[] cookies = { new Cookie("token", "12345") };
        when(request.getCookies()).thenReturn(cookies);
        when(jwtUtils.isExpired("12345")).thenReturn(false);
        when(jwtUtils.getCartFromToken("12345")).thenReturn(1L);

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setItems(new HashMap<>());
        cart.setTotal(0L);
        when(iCartRepository.findById(1L)).thenReturn(Optional.of(cart));

        ProductDto productDto = new ProductDto("Product1", 100L, 10);
        when(iProductAPI.getProductById(1L)).thenReturn(productDto);
        when(iCartRepository.save(any(Cart.class))).thenReturn(cart);

        cartService.addProductCookie(1L, request);

        verify(iCartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void testEmptyCart() {
        // Arrange
        String token = "12345";
        when(jwtUtils.isExpired(token)).thenReturn(false);
        when(jwtUtils.getCartFromToken(token)).thenReturn(1L);

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setItems(new HashMap<>());
        cart.setTotal(0L);
        when(iCartRepository.findById(1L)).thenReturn(Optional.of(cart));
        when(iCartRepository.save(any(Cart.class))).thenReturn(cart);

        cartService.emptyCart(token);

        verify(iCartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void testGetCartFromCookie_Success() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Cookie[] cookies = { new Cookie("token", "12345") };
        when(request.getCookies()).thenReturn(cookies);
        when(jwtUtils.isExpired("12345")).thenReturn(false);
        when(jwtUtils.getCartFromToken("12345")).thenReturn(1L);

        Cart cart = new Cart();
        cart.setId(1L);
        when(iCartRepository.findById(1L)).thenReturn(Optional.of(cart));

        CartDto cartDto = cartService.getCartFromCookie(request);

        assertNotNull(cartDto);
        assertEquals(1L, cartDto.getIdCart());
    }

    @Test
    void testGetCartFromCookie_NoCookies() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getCookies()).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cartService.getCartFromCookie(request);
        });
        assertEquals("Cookies not found", exception.getMessage());
    }

    @Test
    void testGetCartFromCookie_TokenExpired() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Cookie[] cookies = { new Cookie("token", "12345") };
        when(request.getCookies()).thenReturn(cookies);
        when(jwtUtils.isExpired("12345")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cartService.getCartFromCookie(request);
        });
        assertEquals("Token expired", exception.getMessage());
    }
}