package com.example.ai_batch1.security.config;

import com.example.ai_batch1.domain.user.RoleRepository;
import com.example.ai_batch1.domain.user.UserRepository;
import com.example.ai_batch1.security.jwt.AuthTokenFilter;
import com.example.ai_batch1.security.jwt.JwtAuthEntryPoint;
import com.example.ai_batch1.security.jwt.JwtUtils;
import com.example.ai_batch1.security.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.example.ai_batch1.security.oauth.OAuth2SuccessHandler;
import com.example.ai_batch1.security.token.RefreshTokenRepository;
import com.example.ai_batch1.security.user.CustomUserDetailsService;
import com.example.ai_batch1.security.oauth.OAuth2UserCustomService;
import com.example.ai_batch1.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;


@RequiredArgsConstructor
@Configuration
public class WebOAuthSecurityConfig {

    private final OAuth2UserCustomService oAuth2UserCustomService;
    private final JwtUtils jwtUtils;
    private final JwtAuthEntryPoint authEntryPoint;

    private final RefreshTokenRepository refreshTokenRepository;
    private final CustomUserDetailsService userDetailsService;
    private final UserService userService;

    private final RoleRepository roleRepository;


    private static final List<String> SECURED_URLS =
            List.of("/api/v1/carts/**", "/api/v1/cartItems/**");


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthTokenFilter authTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        var authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
    }

    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler() {
        return new OAuth2SuccessHandler(jwtUtils,
                refreshTokenRepository,
                oAuth2AuthorizationRequestBasedOnCookieRepository(),
                userService,
                roleRepository //
        );
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable)  // 배포시 ???
                .exceptionHandling(exception -> exception.authenticationEntryPoint(authEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authorizeHttpRequests(auth -> auth.requestMatchers(SECURED_URLS.toArray(String[]::new)).authenticated()
                        .anyRequest().permitAll());

        // OAuth2 로그인 설정 추가
        http.oauth2Login(oauth2 -> oauth2
                .loginPage("/login") // 사용자 정의 로그인 페이지 경로
                .authorizationEndpoint(authorization -> authorization
                        .authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository())
                )
                .successHandler(oAuth2SuccessHandler())  // 성공 핸들러
                .userInfoEndpoint(userInfo -> userInfo
                        .userService(oAuth2UserCustomService)  // 사용자 정보 가져오기 서비스
                )
        );

        // 로그아웃 설정
        http.logout(logout -> logout
                .logoutSuccessUrl("/login")  // 로그아웃 후 리다이렉트
        );

        http.authenticationProvider(daoAuthenticationProvider());
        http.addFilterBefore(authTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }





}
