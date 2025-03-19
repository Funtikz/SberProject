package org.example.sberproject.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.crypto.SecretKey;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest
class JwtServiceTest {

    @Autowired
    private JwtService jwtService;
    private String jwtSecret = "dd5b30ce47f168b479f31e4feeee9fa0a1288a68973595954992d9db8909dd7e";

    @Test
    void generateJwtToken_ReturnsNonNullToken() {
        String number = "1234567890";
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");

        String token = jwtService.generateJwtToken(number, roles);

        assertNotNull(token);
    }

    @Test
    void generateJwtToken_IsValid(){
        String number = "1234567890";
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");

        String token = jwtService.generateJwtToken(number, roles);
        String decodeNumber = Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload().getSubject();

        assertEquals(true, decodeNumber.equals(number));
    }

    @Test
    void getEmailFromToken_ValidToken_ReturnsCorrectEmail() {
        String email = "test@example.com";
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");

        String token = jwtService.generateJwtToken(email, roles);

        String extractedEmail = jwtService.getEmailFromToken(token);

        assertEquals(true, extractedEmail.equals(email));
    }


    @Test
    void getEmailFromToken() {
    }

    @Test
    void validateJwtToken() {
    }

    @Test
    void generateJwtToken() {
    }

    @Test
    void extractUsername() {
    }

    @Test
    void extractRoles() {
    }


    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}