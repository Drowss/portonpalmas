package com.portondelapalma.apigateway.configuration;

import com.portondelapalma.apigateway.filter.AuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class ApiGatewayConfiguration {

//    @Autowired
//    private AuthenticationFilter authenticationFilter;
//
//    @Bean
//    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
//        return builder.routes()
//                .route("product-service", r -> r.path("/product/v1/**").filters(f -> f.filter(authenticationFilter)).uri("lb://product"))
//                .route("horse-service", r -> r.path("/horse/v1/**").filters(f -> f.filter(authenticationFilter)).uri("lb://horse"))
//                .route("cart-service", r -> r.path("/cart/v1/**").filters(f -> f.filter(authenticationFilter)).uri("lb://cart"))
//                .build();
//    }

}
