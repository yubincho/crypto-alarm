package com.example.ai_batch1.security.oauth;

import com.example.ai_batch1.exception.ResourceNotFoundException;
import com.example.ai_batch1.domain.user.Role;
import com.example.ai_batch1.domain.user.RoleRepository;
import com.example.ai_batch1.domain.user.UserEntity;
import com.example.ai_batch1.security.util.CookieUtil;
import com.example.ai_batch1.security.jwt.JwtUtils;
import com.example.ai_batch1.security.token.RefreshToken;
import com.example.ai_batch1.security.token.RefreshTokenRepository;
import com.example.ai_batch1.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;


@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14);

//    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofDays(1);  // // jwtUtils 에 정의되어 있음
    public static final String REDIRECT_PATH = "/";

    private final JwtUtils jwtUtils;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;
    private final UserService userService;
    private final RoleRepository roleRepository;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        // 이메일로 User 조회
        UserEntity user = userService.findByEmail((String) oAuth2User.getAttributes().get("email"))
                .orElseGet(() -> {
                    UserEntity newUser = new UserEntity();
                    newUser.setEmail((String) oAuth2User.getAttributes().get("email"));
                    newUser.setNickname((String) oAuth2User.getAttributes().get("name"));

                    Role defaultRole = roleRepository.findByName("ROLE_USER").orElseThrow(() -> new ResourceNotFoundException("Default role not found"));
                    newUser.getRoles().add(defaultRole);

                    return userService.oauthSave(newUser);
                    // 이 프로젝트는 oauth 로만 회원가입 및 로그인 가능
                });

        String refreshToken = jwtUtils.generateToken(user, REFRESH_TOKEN_DURATION);
        saveRefreshToken(user.getId(), refreshToken);
        addRefreshTokenToCookie(request, response, refreshToken);

        String accessToken = jwtUtils.generateTokenForUser(authentication);
        String targetUrl = getTargetUrl(accessToken);

        clearAuthenticationAttributes(request, response);

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private void saveRefreshToken(Long userId, String newRefreshToken) {
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                .map(entity -> entity.update(newRefreshToken))
                .orElse(new RefreshToken(userId, newRefreshToken));

        refreshTokenRepository.save(refreshToken);
    }

    private void addRefreshTokenToCookie(HttpServletRequest request, HttpServletResponse response, String refreshToken) {
        int cookieMaxAge = (int) REFRESH_TOKEN_DURATION.toSeconds();

        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN_COOKIE_NAME);
        CookieUtil.addCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, cookieMaxAge);
    }

    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    private String getTargetUrl(String token) {
        return UriComponentsBuilder.fromUriString(REDIRECT_PATH)
                .queryParam("token", token)
                .build()
                .toUriString();
    }

}
