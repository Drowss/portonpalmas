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
        // Arrange
        Cart cart = new Cart();
        cart.setId(1L);
        when(iCartRepository.save(cart)).thenReturn(cart);

        // Act
        Long cartId = cartService.createCart(cart);

        // Assert
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
        // Arrange
        when(iCartRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cartService.findCart(1L);
        });
        assertEquals("Cart not found", exception.getMessage());
    }

    @Test
    void testAddProductCookie() {
        // Arrange
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

        // Act
        cartService.addProductCookie(1L, request);

        // Assert
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

        // Act
        cartService.emptyCart(token);

        // Assert
        verify(iCartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void testGetCartFromCookie_Success() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        Cookie[] cookies = { new Cookie("token", "12345") };
        when(request.getCookies()).thenReturn(cookies);
        when(jwtUtils.isExpired("12345")).thenReturn(false);
        when(jwtUtils.getCartFromToken("12345")).thenReturn(1L);

        Cart cart = new Cart();
        cart.setId(1L);
        when(iCartRepository.findById(1L)).thenReturn(Optional.of(cart));

        // Act
        CartDto cartDto = cartService.getCartFromCookie(request);

        // Assert
        assertNotNull(cartDto);
        assertEquals(1L, cartDto.getIdCart());
    }

    @Test
    void testGetCartFromCookie_NoCookies() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getCookies()).thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cartService.getCartFromCookie(request);
        });
        assertEquals("Cookies not found", exception.getMessage());
    }

    @Test
    void testGetCartFromCookie_TokenExpired() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        Cookie[] cookies = { new Cookie("token", "12345") };
        when(request.getCookies()).thenReturn(cookies);
        when(jwtUtils.isExpired("12345")).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cartService.getCartFromCookie(request);
        });
        assertEquals("Token expired", exception.getMessage());
    }
}