package com.example.demo.security;


import com.example.demo.exceptions.SpringRedditException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateException;
import java.time.Instant;
import java.util.Date;

import static io.jsonwebtoken.Jwts.parser;
import static java.util.Date.from;

@Service
public class JwtTokenProvider {
    private Key key;
    //public KeyStore keystore;
    @Value("${jwt.expiration.time}")
    private Long jwtExpirationInMillis;
    public String generateToken(Authentication authentication)
    {
        User principal=(User)authentication.getPrincipal();

        return Jwts.builder().setSubject(principal.getUsername()).setExpiration(from(Instant.now().plusMillis(jwtExpirationInMillis))).signWith(key).compact();
    }

    public String generateTokenWithUserName(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(from(Instant.now()))
                .signWith(key)
                .setExpiration(from(Instant.now().plusMillis(jwtExpirationInMillis)))
                .compact();
    }
    @PostConstruct
    public void init()
    {
        key= Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }

    public boolean validateToken(String jwt)
    {
        Jwts.parser().setSigningKey(key).parseClaimsJws(jwt);
        return true;
    }

    public String getUsernameFromJwt(String token)
    {

        Claims claims= Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
        return  claims.getSubject();
    }
    public Long getJwtExpirationInMillis() {
        return jwtExpirationInMillis;
    }
}
