package com.drow.salesv.controller;

import com.drow.salesv.model.Sale;
import com.drow.salesv.service.SaleService;
import com.stripe.exception.StripeException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1")
public class SaleController {

    @Autowired
    private SaleService saleService;

    @GetMapping("/successful")
    public String successful(HttpServletRequest request) throws StripeException {
        return saleService.createCheckoutSession(request);
    }

    @GetMapping("/history")
    public List<Sale> history(HttpServletRequest request) {
        return saleService.history(request);
    }

    @GetMapping("/done")
    public RedirectView done(HttpServletRequest request) {
        return saleService.successful(request);
    }
}
