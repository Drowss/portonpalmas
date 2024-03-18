package com.portondelapalma.apigateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@RefreshScope
@Component
@RequiredArgsConstructor
public class AuthenticationFilter implements GatewayFilter {

    private JwtParser jwtParser;
    private final SecretKey key;


    @Autowired
    public AuthenticationFilter(@Value("${secretKey}") String secret) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.jwtParser = Jwts.parser()
                .verifyWith(key)
                .build();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        System.out.println("HOLAAAAAAAAA");
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        if (this.isSecured(request)) {
            if (!request.getCookies().containsKey("token")) {
                System.out.println("KEY NOT FOUND");
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }

            HttpCookie cookie = request.getCookies().get("token").get(0);
            String accessToken = cookie.getValue();

            if (this.isExpired(accessToken)) {
                System.out.println("EXPIRED");
                response.setStatusCode(HttpStatus.UNAUTHORIZED);

                return response.setComplete();
            }
            System.out.println("PASSED");
        }
        return chain.filter(exchange);
    }

    private boolean isSecured(ServerHttpRequest request) {

        String path = request.getURI().getPath();
        return !path.contains("/user/auth/login") && !path.contains("/user/auth/register");
    }

    public boolean isExpired(String accessToken) {
        try {
            Claims accessClaims = Jwts.parser()
                    .verifyWith((key))
                    .build()
                    .parseSignedClaims(accessToken)
                    .getPayload();

            return accessClaims.getExpiration().before(new Date());
        } catch (JwtException ex) {
            System.out.println("EXCEPTION");
            ex.printStackTrace();
            return true;
        }
    }
}
