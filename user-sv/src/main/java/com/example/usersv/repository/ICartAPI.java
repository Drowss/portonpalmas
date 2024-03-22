package com.example.usersv.repository;

import com.example.usersv.dto.CartDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "cart")
public interface ICartAPI {

    @PostMapping("/v1/create")
    Long createCart(@RequestBody CartDto cartDto);
}
