package com.jesus_dev.arena_hub.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtGenerator {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    // Clave criptográfica válida para trabajar con JWT
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    // Generar el token
    public String generateToken(Authentication authentication) {
        // Obtenemos el usuario
        String email = authentication.getName();

        // Fecha de expedición del token
        Date currentDate = new Date();

        // Fecha de expiración del token
        Date expireDate = new Date(currentDate.getTime() + jwtExpiration);

        // Construimos el token
        String token = Jwts.builder()
                .subject(email)
                .issuedAt(currentDate)
                .expiration(expireDate)
                .signWith(getSigningKey(), Jwts.SIG.HS512)
                .compact();

        return token;
    }

    // Obtenemos el nombre del usuario del token
    public String getUsernameFromJwt(String token) {
        // La forma moderna
        Claims claims = Jwts.parser()
                .verifyWith((SecretKey) getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }


    // Validar el token
    public boolean validateToken(String token) {
        try {
            // La forma moderna para validación también
            Jwts.parser()
                    .verifyWith((SecretKey) getSigningKey())
                    .build()
                    .parseSignedClaims(token);

            return true;
        } catch (MalformedJwtException e) {
            System.out.println("Invalid JWT token: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            System.out.println("JWT token is expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.out.println("JWT token is unsupported: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("JWT claims string is empty: " + e.getMessage());
        } catch (SignatureException e) {
            System.out.println("Signature validation failed: " + e.getMessage());
        }
        return false;
    }
}
