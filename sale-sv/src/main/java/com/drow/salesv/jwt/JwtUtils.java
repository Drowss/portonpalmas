package com.drow.salesv.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class JwtUtils {

    private final SecretKey key;


    @Autowired
    public JwtUtils(@Value("${secretKey}") String secret) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public Long getCartFromRequest(String token) {
        Claims accessClaims = Jwts.parser()
                .verifyWith((key))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return accessClaims.get("cart", Long.class);
    }
}
