package com.sprarta.sproutmarket.config;

import com.sprarta.sproutmarket.domain.user.service.CustomUserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FilterConfig {

    private final JwtUtil jwtUtil;
    private CustomUserDetailService customUserDetailService;

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilter() {
        FilterRegistrationBean<JwtAuthenticationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new JwtAuthenticationFilter(jwtUtil, customUserDetailService));
        registrationBean.addUrlPatterns("/*"); // 필터를 적용할 URL 패턴을 지정

        return registrationBean;
    }
}
