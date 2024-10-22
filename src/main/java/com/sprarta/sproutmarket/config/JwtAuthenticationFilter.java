package com.sprarta.sproutmarket.config;

import com.sprarta.sproutmarket.domain.user.enums.UserRole;
import com.sprarta.sproutmarket.domain.user.service.CustomUserDetailService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailService customUserDetailService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // Authorization 헤더 확인
        String authorizationHeader = request.getHeader("Authorization");

        // Authorization 헤더가 없거나, Bearer 토큰이 아닌 경우 필터 통과
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // JWT 토큰 추출 및 검증
        String jwt = jwtUtil.substringToken(authorizationHeader);
        Claims claims = jwtUtil.extractClaims(jwt);

        if (claims == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "잘못된 JWT 토큰입니다.");
            return;
        }

        String email = claims.get("email", String.class);
        Long userId = Long.parseLong(claims.getSubject());
        UserRole userRole = UserRole.of(claims.get("userRole", String.class));

        // 사용자 정보 HttpServletRequest에 저장
        request.setAttribute("userId", userId);
        request.setAttribute("email", email);
        request.setAttribute("userRole", userRole);

        // SecurityContextHolder에 인증 정보 저장
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = customUserDetailService.loadUserByUsername(email);
            if (userDetails != null) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // 필터 체인 통과
        filterChain.doFilter(request, response);
    }
}
