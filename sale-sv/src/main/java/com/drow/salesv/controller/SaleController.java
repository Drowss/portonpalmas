package com.drow.salesv.controller;

import com.drow.salesv.service.SaleService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/v1")
public class SaleController {

    @Autowired
    private SaleService saleService;

    @PostMapping("/successful")
    public Map<String, Object> successful(HttpServletRequest request) {
        return saleService.successful(request);
    }
}
