package com.drow.salesv.service;

import com.drow.salesv.dto.CartDto;
import com.drow.salesv.dto.ProductDto;
import com.drow.salesv.jwt.JwtUtils;
import com.drow.salesv.repository.ICartAPI;
import com.drow.salesv.repository.IProductAPI;
import com.drow.salesv.repository.ISaleRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SaleService {

    @Autowired
    private ISaleRepository iSaleRepository;

    @Autowired
    private ICartAPI iCartAPI;

    @Autowired
    private IProductAPI iProductAPI;

    @Autowired
    private JwtUtils jwtUtils;

    public Map<String, Object> successful(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        if (request.getCookies() == null) {
            throw new RuntimeException("No token found");
        }
        List<Cookie> list = List.of(request.getCookies());
        String token = list.stream()
                .filter(cookie -> cookie.getName().equals("token"))
                .findFirst().get().getValue();
        if (jwtUtils.isExpired(token)) {
            throw new RuntimeException("Token expired");
        }
        Long total = 0L;
        Long cart = jwtUtils.getCartFromRequest(token);
        CartDto cartDto = iCartAPI.findById(cart);
        Set<String> keys = cartDto.getItems().keySet();
        List<ProductDto> products = new ArrayList<>();
        for (String key : keys) {
            if (iProductAPI.getProductByName(key).getStock() < cartDto.getItems().get(key) ) {
                throw new RuntimeException("Te excediste en la cantidad de " + iProductAPI.getProductByName(key).getNameProduct() + "disponibles en stock");
            }
            Integer quantity = cartDto.getItems().get(key);
            ProductDto productDto = iProductAPI.getProductByName(key);
            total += productDto.getPrice() * quantity;
            products.add(productDto);
        }
        response.put("products", products);
        products.forEach(productDto -> iProductAPI.modifyStock(productDto.getIdProduct(), productDto.getStock() - cartDto.getItems().get(productDto.getNameProduct())));
        response.put("Total", total);
        return response;

    }
}
