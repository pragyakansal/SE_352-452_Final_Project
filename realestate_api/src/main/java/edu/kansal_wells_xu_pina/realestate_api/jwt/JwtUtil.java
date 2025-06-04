package edu.kansal_wells_xu_pina.realestate_api.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {
    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expirationMs}")
    private long jwtExpirationMs;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(UserDetails userDetails) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        log.info("Generating JWT token for user: {}", userDetails.getUsername());
        log.debug("Token expiration: {}", expiryDate);

        // Get the role from the first authority (we only have one role per user)
        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .map(authority -> authority.replace("ROLE_", ""))
                .orElseThrow(() -> new IllegalStateException("User has no authorities"));

        String token = Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("role", role)  // Store single role instead of list
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();

        log.debug("Generated token with role: {}", role);
        return token;
    }

    public Claims extractAllClaims(String token) {
        log.debug("Extracting claims from token");
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        String role = claims.get("role", String.class);
        log.debug("Extracted role from token: {}", role);
        return List.of(role);  // Return single role as a list for compatibility
    }

    public String extractUsername(String token) {
        String username = extractAllClaims(token).getSubject();
        log.debug("Extracted username from token: {}", username);
        return username;
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        boolean isValid = (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
        log.info("Token validation result for user {}: {}", username, isValid);
        return isValid;
    }

    private boolean isTokenExpired(String token) {
        final Date expiration = extractAllClaims(token).getExpiration();
        boolean isExpired = expiration.before(new Date());
        log.debug("Token expiration check: {}", isExpired);
        return isExpired;
    }
}
