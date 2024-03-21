package com.drow.salesv.service;

import com.drow.salesv.jwt.JwtUtils;
import com.drow.salesv.repository.ISaleRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SaleService {

    @Autowired
    private ISaleRepository iSaleRepository;

    @Autowired
    private JwtUtils jwtUtils;

    public String successful(HttpServletRequest request) {
        List<Cookie> list = List.of(request.getCookies());
        String token = list.stream()
                .filter(cookie -> cookie.getName().equals("token"))
                .findFirst().get().getValue();
        Long cart = jwtUtils.getCartFromRequest(token);
    }
}
