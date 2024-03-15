package com.portondelapalma.CartService.repository;

import com.portondelapalma.CartService.dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "product")
public interface IProductAPI {

    @GetMapping("/v1/getById")
    ProductDto getProductById(@RequestParam Long idProduct);

    @GetMapping("/v1/getByName")
    ProductDto getProductByName(@RequestParam String nameProduct);

    @PutMapping("/v1/modify-stock")
    void modifyStock(@RequestParam Long idProduct, @RequestParam Integer stock);
}
