package com.portondelapalma.productsv.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.portondelapalma.productsv.dto.ProductDto;
import com.portondelapalma.productsv.model.Product;
import com.portondelapalma.productsv.service.IProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("product/v1")
public class ProductController {

    @Autowired
    private IProductService iProductService;

    @GetMapping() //Endpoint para cliente
    public List<Product> getAllProducts() {
        return iProductService.findAllProductORM();
    }

    @GetMapping("/search") //Endpoint para cliente
    public List<ProductDto> getAllByCategory(@RequestParam String category) {
        return iProductService.getAllByCategory(category);
    }

    @PostMapping("/upload") //Endpoint para admin
    public void upload(@RequestPart("file") MultipartFile file, @Valid @RequestPart("product") String productJson) throws JsonProcessingException {
        iProductService.saveProductORM(file, productJson);
    }

    @DeleteMapping("/delete/{idProduct}") //Endpoint para admin
    public void deleteProduct(@PathVariable Long idProduct) throws MalformedURLException, URISyntaxException {
        iProductService.deleteProductORM(idProduct);
    }

    @PutMapping(value = "/put/{idProduct}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE) //Endpoint para admin
    public void putProduct(@PathVariable("idProduct") Long idProduct, @RequestPart(value = "file", required = false) MultipartFile file,
                            @RequestPart(value = "product", required = false) String productJson) throws JsonProcessingException, URISyntaxException {
        iProductService.updateProductORM(idProduct, file, productJson);
    }

    @PutMapping("/modify-stock") //Endpoint consumido por cart
    public void modifyStock(@RequestParam Long idProduct, @RequestParam Integer stock) {
        iProductService.modifyStock(idProduct, stock);
    }

    @GetMapping("/getById") //Endpoint consumido por cart
    private ProductDto getById(@RequestParam Long idProduct) {
        return iProductService.getById(idProduct);
    }

    @GetMapping("/getByName") //Endpoint consumido por cart
    private ProductDto getByName(@RequestParam String nameProduct) {
        return iProductService.getByName(nameProduct);
    }

    @GetMapping("/getName")
    public ProductDto getName(@RequestParam String nameProduct) {
        return iProductService.getByName(nameProduct);
    }

}
