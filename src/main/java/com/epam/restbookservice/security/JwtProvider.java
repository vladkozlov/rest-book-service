package com.epam.restbookservice.security;

import com.epam.restbookservice.domain.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtProvider {

    public static final String ROLES_CLAIM = "roles";
    @Value("${jwt.token.secret}")
    private String secret;
    @Value("${jwt.token.validity}")
    private long validityInMilliseconds;

    public String createToken(String username, List<Role> roles) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put(ROLES_CLAIM, roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getAuthority()))
                .collect(Collectors.toList())
        );
        Date now = new Date();
        Date expire = new Date(now.getTime() + validityInMilliseconds);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expire)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public boolean isValidToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUsername(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public List<GrantedAuthority> getRoles(String token) {
        List<Map<String, String>> roleClaims =  Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody()
                .get(ROLES_CLAIM, List.class);

        return roleClaims.stream()
                .map(roleClaim -> new SimpleGrantedAuthority(roleClaim.get("authority")))
                .collect(Collectors.toList());
    }

}
