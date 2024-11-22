package com.example.learning_api.service.common;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.learning_api.config.JwtProperties;
import com.example.learning_api.entity.sql.database.UserEntity;
import com.example.learning_api.enums.RoleEnum;
import com.example.learning_api.repository.database.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtService {
    public static final String EMAIL_CLAIM_KEY = "email";
    public static final String ROLES_CLAIM_KEY = "role";
    private static final Logger log = LoggerFactory.getLogger(JwtService.class);
    private final JwtProperties properties;
    UserRepository userRepository;

    public String issueAccessToken(String userId, String email, RoleEnum role) {
        return JWT.create()
                .withSubject(String.valueOf(userId))
                .withExpiresAt(Date.from(Instant.now().plus(Duration.of(10, ChronoUnit.MINUTES))))
                .withClaim(EMAIL_CLAIM_KEY, email)
                .withClaim(ROLES_CLAIM_KEY, role.name())
                .sign(Algorithm.HMAC256(properties.getAccessTokenKey()));
    }
    public String issueRefreshToken(String userId, String email,RoleEnum role) {
        return JWT.create()
                .withSubject(String.valueOf(userId))
                .withExpiresAt(Date.from(Instant.now().plus(Duration.of(7, ChronoUnit.DAYS))))
                .withClaim(EMAIL_CLAIM_KEY, email)
                .withClaim(ROLES_CLAIM_KEY, role.name())
                .sign(Algorithm.HMAC256(properties.getRefreshTokenKey()));
    }

    
    public boolean isValid(String token, UserDetails user) {
        var jwt = extractUsername(token);
        var temp = user.getUsername();
        return user != null && !isTokenExpired(token) && user.getUsername().equals(jwt);
    }
    private List<SimpleGrantedAuthority> extractAuthoritiesFromClaim(DecodedJWT jwt) {
        var claim = jwt.getClaim(ROLES_CLAIM_KEY);
        if(claim.isNull()) return List.of();
        return claim.asList(SimpleGrantedAuthority.class);
    }
    public String extractRole(String token) {
        return JWT.require(Algorithm.HMAC256(properties.getAccessTokenKey()))
                .build()
                .verify(token)
                .getClaim(ROLES_CLAIM_KEY)
                .asString();
    }
    public String extractUsername(String token) {
        return JWT.require(Algorithm.HMAC256(properties.getAccessTokenKey()))
                .build()
                .verify(token)
                .getClaim(EMAIL_CLAIM_KEY)
                .asString();
    }
    public String extractUserId(String token) {
        return JWT.require(Algorithm.HMAC256(properties.getAccessTokenKey()))
                .build()
                .verify(token)
                .getSubject();
    }
    public DecodedJWT decodeAccessToken(String token) {
        return JWT.require(Algorithm.HMAC256(properties.getAccessTokenKey()))
                .build()
                .verify(token);
    }
    public DecodedJWT decodeRefreshToken(String token) {
        return JWT.require(Algorithm.HMAC256(properties.getRefreshTokenKey()))
                .build()
                .verify(token);
    }
    public boolean isTokenExpired(String token) {
        return JWT.require(Algorithm.HMAC256(properties.getAccessTokenKey()))
                .build()
                .verify(token)
                .getExpiresAt()
                .before(new Date());
    }
}