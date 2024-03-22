package com.drow.salesv.repository;

import com.drow.salesv.dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "product/product")
public interface IProductAPI {

    @GetMapping("/v1/getName")
    ProductDto getProductByName(@RequestParam String nameProduct);

    @PutMapping("/v1/modify-stock")
    void modifyStock(@RequestParam Long idProduct, @RequestParam Integer stock);
}
