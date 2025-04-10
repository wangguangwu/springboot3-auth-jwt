package com.wangguangwu.springboot3_auth_jwt.security;

import com.wangguangwu.springboot3_auth_jwt.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 认证过滤器
 * 用于处理 JWT 令牌的验证和用户认证
 *
 * @author wangguangwu
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // 如果请求头中没有 JWT 令牌，直接放行
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 提取 JWT 令牌
        jwt = authHeader.substring(7);
        try {
            // 从 JWT 令牌中提取用户名
            username = jwtUtil.extractUsername(jwt);
            // 如果用户名存在且当前上下文中没有认证信息
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // 加载用户详细信息
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                // 验证 JWT 令牌
                if (jwtUtil.validateToken(jwt, userDetails)) {
                    // 创建认证令牌
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    // 设置认证详细信息
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // 将认证信息设置到 Spring Security 的上下文中
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e);
            // 设置 401 响应
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"message\":\"Token 已过期或无效，请重新登录\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
