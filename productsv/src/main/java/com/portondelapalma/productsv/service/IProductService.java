package com.portondelapalma.productsv.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.portondelapalma.productsv.dto.ProductDto;
import com.portondelapalma.productsv.model.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.List;

public interface IProductService {
    ProductDto createProduct(MultipartFile multipartFile, String productJson) throws JsonProcessingException;
    ResponseEntity<String> deleteProduct(Long idProduct) throws MalformedURLException, URISyntaxException;
    List<ProductDto> getAllProducts();
    List<ProductDto> getAllByCategory(String category);
    Product putProduct(Long id, MultipartFile multipartFile, String productJson) throws URISyntaxException, JsonProcessingException;
    public void deleteImage(String url) throws URISyntaxException;
    ProductDto getById(Long idProduct);

    ProductDto getByName(String nameProduct);
    void modifyStock(Long idProduct, Integer stock);
}
