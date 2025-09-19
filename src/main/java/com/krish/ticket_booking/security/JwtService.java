package com.krish.ticket_booking.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {
    private static final String SECRET = "a-very-long-32+chars-secret-key-for-hs256!!"; // move to config
    private static final long EXP_MS = 1000 * 60 * 60; // 1h

    private final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    public String generateToken(String subjectEmail, Map<String, Object> claims) {
        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(subjectEmail)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXP_MS))
                .and()
                .signWith(key)
                .compact();
    }

    public Claims parse(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            throw new RuntimeException("Invalid or Expired token", e);
        }
    }

    public String extractEmail(String token) {
        return parse(token).getSubject();
    }

    public String extractRole(String token) {
        return (String) parse(token).get("role");
    }

    public Long extractUserId(String token) {
        Object userId = parse(token).get("userId");
        return userId != null ? Long.valueOf(userId.toString()) : null;
    }

}

