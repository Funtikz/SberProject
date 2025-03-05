package org.example.sberproject.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Set;

@Component
@Slf4j
public class JwtService {
    @Value("dd5b30ce47f168b479f31e4feeee9fa0a1288a68973595954992d9db8909dd7e")
    private String jwtSecret;

    public String getEmailFromToken(String token){
        Claims claims = Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    public boolean validateJwtToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSignKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return true;
        } catch (ExpiredJwtException ex){
            log.error("Expired exception", ex);
        }
        catch (UnsupportedJwtException ex){
            log.error("Unsupported exception", ex);
        }
        catch (MalformedJwtException ex){
            log.error("Malformed exception", ex);
        }
        catch (SecurityException ex){
            log.error("Security exception", ex);
        }
        catch (Exception ex){
            log.error("Invalid token", ex);
        }
        return false;
    }

    public String generateJwtToken(String number, Set<String> role) {
        Date date = Date.from(LocalDateTime.now().plusDays(180).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .subject(number)
                .claim("roles", role)
                .expiration(date)
                .signWith(getSignKey())
                .compact();
    }


    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    private Claims extractAllClaims(String token) {
        return  Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getBody();
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    public Set<String> extractRoles(String token) {
        return (Set<String>) extractAllClaims(token).get("roles");
    }
    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
