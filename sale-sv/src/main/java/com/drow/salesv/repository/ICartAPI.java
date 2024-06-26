package com.drow.salesv.repository;

import com.drow.salesv.dto.CartDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "cart/cart")
public interface ICartAPI {

    @GetMapping("/v1/get-cart")
    CartDto findById(@RequestParam Long idCart);

    @PutMapping("/v1/empty-cart")
    void emptyCart(@RequestParam String token);
}
