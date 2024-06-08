package com.drow.salesv.repository;

import com.drow.salesv.dto.UserdataDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user/user")
public interface IUserdataAPI {

    @GetMapping("/auth/get-user")
    UserdataDto getUser(@RequestParam String email);
}
