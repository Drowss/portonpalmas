package com.portondelapalma.productsv.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portondelapalma.productsv.dto.ProductDto;
import com.portondelapalma.productsv.model.Category;
import com.portondelapalma.productsv.model.Product;
import com.portondelapalma.productsv.repository.IProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.web.multipart.MultipartFile;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private IProductRepository iProductRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private S3Service s3;

    @InjectMocks
    private ProductService productService;

    @Mock
    private ModelMapper modelMapper;

    private Product product;
    private ProductDto productDto;
    private Product product1;
    private Product product2;
    private ProductDto productDto1;
    private ProductDto productDto2;

    @BeforeEach
    public void setUp() {
        product = Product.builder()
                .idProduct(1L)
                .nameProduct("Test Product")
                .description("Test Description")
                .price(100L)
                .stock(10)
                .imagePath("old/path/to/image.jpg")
                .category(Category.ALIMENTACION)
                .build();

        productDto = ProductDto.builder()
                .idProduct(1L)
                .nameProduct("Updated Product")
                .description("Updated Description")
                .price(200L)
                .stock(20)
                .category(Category.ALIMENTACION)
                .build();

        product1 = Product.builder()
                .idProduct(1L)
                .nameProduct("Product 1")
                .description("Description 1")
                .price(100L)
                .stock(10)
                .category(Category.ALIMENTACION)
                .build();

        product2 = Product.builder()
                .idProduct(2L)
                .nameProduct("Product 2")
                .description("Description 2")
                .price(200L)
                .stock(20)
                .category(Category.ALIMENTACION)
                .build();

        productDto1 = ProductDto.builder()
                .idProduct(1L)
                .nameProduct("Product 1")
                .description("Description 1")
                .price(100L)
                .stock(10)
                .category(Category.ALIMENTACION)
                .build();

        productDto2 = ProductDto.builder()
                .idProduct(2L)
                .nameProduct("Product 2")
                .description("Description 2")
                .price(200L)
                .stock(20)
                .category(Category.ALIMENTACION)
                .build();
    }

    @Test
    public void testCreateProduct_Success() throws JsonProcessingException {
        MultipartFile multipartFile = mock(MultipartFile.class);
        String productJson = "{\"idProduct\":1,\"nameProduct\":\"Updated Product\",\"description\":\"Updated Description\",\"price\":200,\"stock\":20,\"category\":\"ALIMENTACION\"}";
        when(objectMapper.readValue(productJson, ProductDto.class)).thenReturn(productDto);
        when(s3.saveFile(multipartFile)).thenReturn("path/to/image.jpg");

        ProductDto result = productService.createProduct(multipartFile, productJson);

        assertEquals("Updated Product", result.getNameProduct());
        assertEquals("path/to/image.jpg", result.getImagePath());
        verify(iProductRepository).save(any(Product.class));
    }

    @Test
    public void testCreateProduct_JsonProcessingException() throws JsonProcessingException {
        MultipartFile multipartFile = mock(MultipartFile.class);
        String productJson = "{\"nameProduct\":\"Test Product\",\"description\":\"Test Description\",\"price\":100,\"stock\":10,\"category\":\"ELECTRONICS\"}";

        when(objectMapper.readValue(productJson, ProductDto.class)).thenThrow(new JsonProcessingException("Error parsing JSON") {});

        JsonProcessingException exception = assertThrows(JsonProcessingException.class, () -> {
            productService.createProduct(multipartFile, productJson);
        });

        assertEquals("Error parsing JSON", exception.getMessage());
    }

    @Test
    public void testGetAllProducts_Success() {
        when(iProductRepository.findAll()).thenReturn(Arrays.asList(product1, product2));
        when(modelMapper.map(product1, ProductDto.class)).thenReturn(productDto1);
        when(modelMapper.map(product2, ProductDto.class)).thenReturn(productDto2);

        List<ProductDto> result = productService.getAllProducts();

        assertEquals(2, result.size());
        assertEquals(productDto1, result.get(0));
        assertEquals(productDto2, result.get(1));
        verify(iProductRepository, times(2)).findAll();
    }

    @Test
    public void testPutProduct_ProductNotFound() {
        when(iProductRepository.findById(1L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            productService.putProduct(1L, null, null);
        });

        assertEquals("El producto no fue encontrado 1", exception.getMessage());
    }

    @Test
    public void testPutProduct_Success() throws URISyntaxException, JsonProcessingException {
        MultipartFile multipartFile = mock(MultipartFile.class);
        when(iProductRepository.findById(1L)).thenReturn(Optional.of(product));
        when(objectMapper.readValue(anyString(), eq(ProductDto.class))).thenReturn(productDto);
        when(s3.saveFile(multipartFile)).thenReturn("new/path/to/image.jpg");

        Product updatedProduct = productService.putProduct(1L, multipartFile, "{\"nameProduct\":\"Updated Product\",\"description\":\"Updated Description\",\"price\":200,\"stock\":20,\"category\":\"ALIMENTACION\"}");

        verify(iProductRepository).save(product);
    }

}