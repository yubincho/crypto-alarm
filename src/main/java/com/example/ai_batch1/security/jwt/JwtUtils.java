package com.example.ai_batch1.security.jwt;

import com.example.ai_batch1.exception.ResourceNotFoundException;
import com.example.ai_batch1.domain.user.UserEntity;
import com.example.ai_batch1.service.user.UserService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.List;


@RequiredArgsConstructor
@Component
public class JwtUtils {

    @Value("${auth.token.jwtSecret}")
    private String jwtSecret;

//    @Value("${auth.token.expirationInMils}")
//    private int accessExpirationTime;

    private final UserService userService;

    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofDays(1);


    // access token
    public String generateTokenForUser(Authentication authentication) {
        // OAuth2User로 변환 시도
        if (authentication.getPrincipal() instanceof DefaultOAuth2User) {
            DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();

            // 이메일로 User 를 찾음
            String email = (String) oAuth2User.getAttributes().get("email");
            UserEntity userPrincipal = userService.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

            List<String> roles = userPrincipal.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority).toList();

            return Jwts.builder()
                    .setSubject(userPrincipal.getEmail())
                    .claim("id", userPrincipal.getId())
                    .claim("roles", roles)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date((new Date()).getTime() + ACCESS_TOKEN_DURATION.toMillis()))
                    .signWith(key(), SignatureAlgorithm.HS256)
                    .compact();
        }

        throw new IllegalArgumentException("Authentication principal is not an instance of DefaultOAuth2User");
    }

    // refresh token
    public String generateToken(UserEntity user, Duration expirationTime) {
        List<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("id", user.getId())
                .claim("roles", roles)  // 사용자 역할 추가
                .setIssuedAt(new Date())  // 현재 시간
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime.toMillis()))  // 만료 시간 설정
                .signWith(key(), SignatureAlgorithm.HS256)  // 서명
                .compact();
    }


    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            throw new JwtException(e.getMessage());
        }
    }

}
