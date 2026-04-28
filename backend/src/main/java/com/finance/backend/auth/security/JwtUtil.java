package com.finance.backend.auth.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;

@Component
public class JwtUtil {

    private final SecretKey signingKey;
    private final long expirationInMillis;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationInMillis) {
        this.signingKey = buildSigningKey(secret);
        this.expirationInMillis = expirationInMillis;
    }

    public String generateToken(Long userId, String email) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationInMillis);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("email", email)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(signingKey)
                .compact();
    }

    public Long extractUserId(String token) {
        return Long.valueOf(extractAllClaims(token).getSubject());
    }

    public String extractEmail(String token) {
        return extractAllClaims(token).get("email", String.class);
    }

    public boolean isTokenValid(String token, AuthenticatedUserPrincipal userPrincipal) {
        Long userId = extractUserId(token);
        String email = extractEmail(token);
        return userId.equals(userPrincipal.getId())
                && email.equals(userPrincipal.getEmail())
                && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey buildSigningKey(String secret) {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(secret);
            if (keyBytes.length >= 32) {
                return Keys.hmacShaKeyFor(keyBytes);
            }
        } catch (RuntimeException exception) {
            // Fall back to raw secret bytes when the configured value is not base64-encoded.
        }

        byte[] rawBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (rawBytes.length < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 32 bytes long");
        }
        return Keys.hmacShaKeyFor(rawBytes);
    }
}
