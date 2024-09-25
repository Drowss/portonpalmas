package com.drow.salesv.service;

import com.drow.salesv.dto.CartDto;
import com.drow.salesv.dto.Category;
import com.drow.salesv.dto.ProductDto;
import com.drow.salesv.model.Sale;
import com.drow.salesv.model.SaleInf;
import com.drow.salesv.repository.ICartAPI;
import com.drow.salesv.repository.IProductAPI;
import com.drow.salesv.repository.ISaleRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SaleServiceTest {

    @Mock
    private ICartAPI iCartAPI;

    @Mock
    private ISaleRepository iSaleRepository;

    @Mock
    private IProductAPI iProductAPI;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private SaleService saleService;

    @BeforeEach
    void setUp() {

    }

    @Test
    void testGetCartDto_CartNotEmpty() {
        // Arrange
        Long cartId = 1L;
        CartDto cartDto = new CartDto(cartId, Map.of("item1", 2), 100L);
        when(iCartAPI.findById(cartId)).thenReturn(cartDto);

        // Act
        CartDto result = saleService.getCartDto(cartId);

        // Assert
        assertNotNull(result);
        assertEquals(cartId, result.getIdCart());
        assertEquals(100L, result.getTotal());
    }

    @Test
    void testGetCartDto_CartEmpty() {
        // Arrange
        Long cartId = 2L;
        CartDto cartDto = new CartDto(cartId, Map.of(), 0L);
        when(iCartAPI.findById(cartId)).thenReturn(cartDto);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            CartDto result = saleService.getCartDto(cartId);
        });
        assertEquals("Cart is empty", exception.getMessage());
    }

    @Test
    void testGetProductsFromCart_Success() {
        // Arrange
        Map<String, Integer> items = new HashMap<>();
        items.put("Product1", 2);
        CartDto cartDto = new CartDto(1L, items, 200L);

        ProductDto productDto = new ProductDto(1L,
                "Product1",
                "description1",
                100L,
                5,
                "Description1",
                Category.ALIMENTACION,
                1);
        when(iProductAPI.getProductByName("Product1")).thenReturn(productDto);

        // Act
        List<ProductDto> result = saleService.getProductsFromCart(cartDto);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Product1", result.get(0).getNameProduct());
        assertEquals(2, result.get(0).getQuantity());
    }

    @Test
    void testGetProductsFromCart_ExceedsStock() {
        // Arrange
        Map<String, Integer> items = new HashMap<>();
        items.put("Product1", 6);
        CartDto cartDto = new CartDto(1L, items, 200L);

        ProductDto productDto = new ProductDto(1L,
                "Product1",
                "description1",
                100L,
                5,
                "Description1",
                Category.ALIMENTACION,
                1);
        when(iProductAPI.getProductByName("Product1")).thenReturn(productDto);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            saleService.getProductsFromCart(cartDto);
        });
        assertEquals("Te excediste en la cantidad de Product1 disponibles en stock", exception.getMessage());
    }

    @Test
    void testHistory_Success() {
        // Arrange
        Sale sale = Sale.builder()
                .id(1L)
                .date(LocalDate.now())
                .userEmail("user@example.com")
                .address("123 Main St")
                .dni("12345678")
                .total(100L)
                .items(Map.of("Product1", 2))
                .build();

        List<Sale> sales = List.of(sale);
        when(iSaleRepository.findAll()).thenReturn(sales);

        ProductDto productDto = ProductDto.builder()
                .nameProduct("Product1")
                .stock(10)
                .quantity(2)
                .build();
        when(iProductAPI.getProductByName("Product1")).thenReturn(productDto);

        // Act
        List<SaleInf> result = saleService.history();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        SaleInf saleInf = result.get(0);
        assertEquals(1L, saleInf.getId());
        assertEquals("user@example.com", saleInf.getUserEmail());
        assertEquals("123 Main St", saleInf.getAddress());
        assertEquals("12345678", saleInf.getDni());
        assertEquals(100L, saleInf.getTotal());
        assertEquals(1, saleInf.getItems().size());
        assertEquals("Product1", saleInf.getItems().get(0).getNameProduct());
        assertEquals(2, saleInf.getItems().get(0).getQuantity());
    }

    @Test
    void testGetTokenFromRequest_Success() {
        // Arrange
        Cookie[] cookies = { new Cookie("token", "12345") };
        when(request.getCookies()).thenReturn(cookies);

        // Act
        String token = saleService.getTokenFromRequest(request);

        // Assert
        assertEquals("12345", token);
    }

    @Test
    void testGetTokenFromRequest_NoCookies() {
        // Arrange
        when(request.getCookies()).thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            saleService.getTokenFromRequest(request);
        });
        assertEquals("No token found", exception.getMessage());
    }

    @Test
    void testGetTokenFromRequest_NoTokenCookie() {
        // Arrange
        Cookie[] cookies = { new Cookie("session", "abcde") };
        when(request.getCookies()).thenReturn(cookies);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            saleService.getTokenFromRequest(request);
        });
        assertEquals("No value present", exception.getMessage());
    }

}