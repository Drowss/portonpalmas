package com.drow.salesv.service;

import com.drow.salesv.dto.CartDto;
import com.drow.salesv.dto.ProductDto;
import com.drow.salesv.jwt.JwtUtils;
import com.drow.salesv.model.Sale;
import com.drow.salesv.model.SaleInf;
import com.drow.salesv.repository.ICartAPI;
import com.drow.salesv.repository.IProductAPI;
import com.drow.salesv.repository.ISaleRepository;
import com.drow.salesv.repository.IUserdataAPI;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDate;
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

    @Value("${stripeSecretKey}")
    private String stripeSecretKey;

    @Autowired
    private IUserdataAPI iUserdataAPI;

    public RedirectView successful(HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        validateToken(token);
        Long cartId = jwtUtils.getCartFromRequest(token);
        CartDto cartDto = getCartDto(cartId);
        List<ProductDto> products = getProductsFromCart(cartDto);
        updateProductStocks(cartDto, products);
        Long total = calculateTotal(products);
        Sale sale = createSale(token, cartDto, total);
        iSaleRepository.save(sale);
        iCartAPI.emptyCart(token);
        return new RedirectView("http://34.202.233.54:80");
    }

    public List<SaleInf> history() {
        Set<String> keys = new HashSet<>();
        List<SaleInf> saleInfs = new ArrayList<>();
        List<Sale> sales = iSaleRepository.findAll();
        for (Sale sale : sales) {
            keys = sale.getItems().keySet();
            SaleInf saleInf = SaleInf.builder()
                    .id(sale.getId())
                    .date(sale.getDate())
                    .userEmail(sale.getUserEmail())
                    .address(sale.getAddress())
                    .dni(sale.getDni())
                    .total(sale.getTotal())
                    .items(new ArrayList<>())
                    .build();
            for (String key : keys) {
                ProductDto productDto = iProductAPI.getProductByName(key);
                productDto.setQuantity(sale.getItems().get(key));
                saleInf.getItems().add(productDto);
            }
            saleInfs.add(saleInf);
        }
        return saleInfs;
    }

    public String createCheckoutSession(HttpServletRequest request) throws StripeException {
        Stripe.apiKey = stripeSecretKey;

        String token = getTokenFromRequest(request);
        validateToken(token);
        Long cartId = jwtUtils.getCartFromRequest(token);
        CartDto cartDto = getCartDto(cartId);
        List<ProductDto> products = getProductsFromCart(cartDto);

        String YOUR_DOMAIN = "http://34.202.233.54:443/sale/v1";
        SessionCreateParams.Builder paramsBuilder = SessionCreateParams.builder()
                .setCustomerEmail(jwtUtils.getUserEmailFromRequest(token))
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(YOUR_DOMAIN + "/done")
                .setCancelUrl(YOUR_DOMAIN + "?canceled=true");
        for (ProductDto product : products) {
            paramsBuilder.addLineItem(
                    SessionCreateParams.LineItem.builder()
                            .setPriceData(
                                    SessionCreateParams.LineItem.PriceData.builder()
                                            .setCurrency("cop")
                                            .setProductData(
                                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                            .setName(product.getNameProduct())
                                                            .setDescription(product.getDescription())
                                                            .addImage(product.getImagePath())
                                                            .build())
                                            .setUnitAmount(product.getPrice() * 100)
                                            .build())
                            .setQuantity(Long.valueOf(product.getQuantity()))
                            .build());
        }
        SessionCreateParams params = paramsBuilder.build();

        Session session = Session.create(params);
        System.out.println(session.getUrl());
        return session.getUrl();
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        if (request.getCookies() == null) {
            throw new RuntimeException("No token found");
        }
        List<Cookie> list = List.of(request.getCookies());
        return list.stream()
                .filter(cookie -> cookie.getName().equals("token"))
                .findFirst().get().getValue();
    }

    private void validateToken(String token) {
        if (jwtUtils.isExpired(token)) {
            throw new RuntimeException("Token expired");
        }
    }

    private CartDto getCartDto(Long cartId) {
        CartDto cartDto = iCartAPI.findById(cartId);
        if (cartDto.getTotal() == 0L) {
            throw new RuntimeException("Cart is empty");
        }
        return cartDto;
    }

    private List<ProductDto> getProductsFromCart(CartDto cartDto) {
        Set<String> keys = cartDto.getItems().keySet();
        List<ProductDto> products = new ArrayList<>();
        for (String key : keys) {
            ProductDto productDto = iProductAPI.getProductByName(key);
            if (productDto.getStock() < cartDto.getItems().get(key)) {
                throw new RuntimeException("Te excediste en la cantidad de " + productDto.getNameProduct() + " disponibles en stock");
            }
            productDto.setQuantity(cartDto.getItems().get(productDto.getNameProduct()));
            products.add(productDto);
        }
        return products;
    }

    private void updateProductStocks(CartDto cartDto, List<ProductDto> products) {
        products.forEach(productDto ->
                iProductAPI.modifyStock(productDto.getIdProduct(),
                        productDto.getStock() - cartDto.getItems().get(productDto.getNameProduct())));
    }

    private Long calculateTotal(List<ProductDto> products) {
        return products.stream()
                .mapToLong(productDto -> productDto.getPrice() * productDto.getQuantity())
                .sum();
    }

    private Sale createSale(String token, CartDto cartDto, Long total) {
        return Sale.builder()
                .date(LocalDate.now())
                .userEmail(jwtUtils.getUserEmailFromRequest(token))
                .items(cartDto.getItems())
                .total(total)
                .address(iUserdataAPI.getUser(jwtUtils.getUserEmailFromRequest(token)).getAddress())
                .dni(jwtUtils.getDniFromRequest(token))
                .build();
    }

    public List<SaleInf> myHistory(HttpServletRequest request) {
        Set<String> keys = new HashSet<>();
        List<SaleInf> products = new ArrayList<>();
        String token = getTokenFromRequest(request);
        validateToken(token);
        List<Sale> sales = iSaleRepository.findAllByDni(jwtUtils.getDniFromRequest(token));
        for (Sale sale : sales) {
            keys = sale.getItems().keySet();
            SaleInf saleInf = SaleInf.builder()
                    .id(sale.getId())
                    .date(sale.getDate())
                    .userEmail(sale.getUserEmail())
                    .address(sale.getAddress())
                    .dni(sale.getDni())
                    .total(sale.getTotal())
                    .items(new ArrayList<>())
                    .build();
            for (String key : keys) {
                ProductDto productDto = iProductAPI.getProductByName(key);
                productDto.setQuantity(sale.getItems().get(key));
                saleInf.getItems().add(productDto);
            }
            products.add(saleInf);
        }
        return products;
    }
}
