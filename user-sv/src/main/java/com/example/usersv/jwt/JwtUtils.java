package com.example.usersv.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {

    @Value("${secretKey}")
    private String secretKey;

    @Value("${expiration}")
    private String timeExpiration;

    private final SecretKey key;

    @Autowired
    public JwtUtils(@Value("${secretKey}") String secret) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccesToken(String Role, String username, Long idCart, String email, String dni) {
        return Jwts.builder()
                .claim("role", Role)
                .claim("userEmail", email)
                .claim("dni", dni)
                .claim("cart", idCart)
                .claim("sub", username)
                .claim("iat", new Date(System.currentTimeMillis()))
                .claim("exp", new Date(System.currentTimeMillis() + Long.parseLong(timeExpiration)))
                .signWith(getSignatureKey())
                .compact(); //Firma
    }

    public String getUserEmailFromRequest(String token) {
        Claims accessClaims = this.getClaims(token);
        return accessClaims.get("userEmail", String.class);
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith((key))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Boolean isExpired(String token) {
        Claims accessClaims = this.getClaims(token);
        return accessClaims.getExpiration().before(new Date());
    }

    public SecretKey getSignatureKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
