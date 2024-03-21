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
import java.util.Date;

@Component
public class JwtUtils {

    private final SecretKey key;


    @Autowired
    public JwtUtils(@Value("${secretKey}") String secret) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public Long getCartFromRequest(String token) {
        Claims accessClaims = this.getClaims(token);
        return accessClaims.get("cart", Long.class);
    }

    public String getUserEmailFromRequest(String token) {
        Claims accessClaims = this.getClaims(token);
        return accessClaims.get("userEmail", String.class);
    }

    public String getDniFromRequest(String token) {
        Claims accessClaims = this.getClaims(token);
        return accessClaims.get("dni", String.class);
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
}
