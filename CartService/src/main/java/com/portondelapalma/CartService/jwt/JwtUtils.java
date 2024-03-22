package com.portondelapalma.CartService.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {

    private final SecretKey key;

    @Autowired
    public JwtUtils(@Value("${secretKey}") String secret) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith((key))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Boolean isExpired(String token) {
        Claims accessClaims = Jwts.parser()
                .verifyWith((key))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return accessClaims.getExpiration().before(new Date());
    }

    public Long getCartFromToken(String token) {
        Claims accessClaims = getClaims(token);
        return accessClaims.get("cart", Long.class);
    }
}
