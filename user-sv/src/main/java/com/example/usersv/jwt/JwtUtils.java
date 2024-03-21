package com.example.usersv.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
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

    public SecretKey getSignatureKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
