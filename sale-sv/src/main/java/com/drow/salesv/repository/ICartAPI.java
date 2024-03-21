package com.drow.salesv.repository;

import com.drow.salesv.dto.CartDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "cart")
public interface ICartAPI {

    @GetMapping("/v1/get-cart")
    CartDto findById(Long idCart);
}
