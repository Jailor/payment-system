package com.team1.paymentsystem.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * the jwt service implementation
 */
@Component
public class JwtService {
    /**
     * get username from token
     * @param token
     * @return
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * generate a token for a username
     * @param username
     * @return
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    /**
     * create a token
     * @param claims
     * @param username
     * @return
     */
    private String createToken(Map<String, Object> claims, String username) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60* 24 * 21)) //1000 * 60 * 60 * 24
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * decodes a base64-encoded string representing a key, converts it into key bytes, and returns a secret key suitable for HMAC-based digital signatures using the SHA algorithm
     * @return
     */
    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode("38792F423F4528482B4B6250655368566D597133743677397A24432646294A40");
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * get expiration date from a token
     * @param token
     * @return
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * facilitates the extraction of a specific claim from a JWT token
     * @param token
     * @param claimsResolver
     * @return
     * @param <T>
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * parses and verifies a JWT token using a signing key, and then returns the claims (payload) contained within the token
     * @param token
     * @return
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * check if a token is expired or not
     * @param token
     * @return
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * check if a token is valid or not
     * @param token
     * @param userDetails
     * @return
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}