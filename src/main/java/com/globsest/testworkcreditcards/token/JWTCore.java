package com.globsest.testworkcreditcards.token;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JWTCore {

    private final SecretKey accessSecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    private final SecretKey refreshSecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    @Value("${jwt.access.expiration}")
    private long accessExpirationMs;

    @Value("${jwt.refresh.expiration}")
    private long refreshExpirationMs;

    public String generateAccessToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessExpirationMs))
                .signWith(accessSecretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpirationMs))
                .signWith(refreshSecretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public String getUsernameFromToken(String token, boolean isRefresh) {
        return Jwts.parser()
                .setSigningKey(isRefresh ? refreshSecretKey : accessSecretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token, boolean isRefresh) {
        try {
            Jwts.parser()
                    .setSigningKey(isRefresh ? refreshSecretKey : accessSecretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}