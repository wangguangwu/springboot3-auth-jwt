package com.wangguangwu.springboot3_auth_jwt.controller;

import com.wangguangwu.springboot3_auth_jwt.model.ApiResponse;
import com.wangguangwu.springboot3_auth_jwt.model.JwtResponse;
import com.wangguangwu.springboot3_auth_jwt.model.LoginRequest;

import com.wangguangwu.springboot3_auth_jwt.service.AuthService;
import com.wangguangwu.springboot3_auth_jwt.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * 认证控制器
 * 处理用户登录、登出等认证相关的请求
 *
 * @author wangguangwu
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    /**
     * 认证服务
     */
    private final AuthService authService;
    private final CookieUtil cookieUtil;

    /**
     * 处理用户登录请求
     *
     * @param request 登录请求对象
     * @return 包含访问令牌的响应
     */
    @PostMapping("/login")
    public ApiResponse<JwtResponse> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        JwtResponse jwtResponse = authService.login(request);
        response.addHeader(HttpHeaders.SET_COOKIE, cookieUtil.createAccessTokenCookie(jwtResponse.getAccessToken()));
        response.addHeader(HttpHeaders.SET_COOKIE, cookieUtil.createRefreshTokenCookie(jwtResponse.getRefreshToken()));
        return ApiResponse.success(jwtResponse);
    }

    /**
     * 刷新访问令牌
     *
     * @param request 刷新令牌请求
     * @return 新的访问令牌
     */
    @PostMapping("/refresh")
    public ApiResponse<JwtResponse> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            String refreshToken = cookieUtil.getRefreshTokenFromCookie(request)
                    .orElseThrow(() -> new IllegalArgumentException("未找到刷新令牌"));
            log.debug("开始刷新令牌: {}", refreshToken);
            JwtResponse jwtResponse = authService.refreshToken(refreshToken);
            log.debug("令牌刷新成功");
            response.addHeader(HttpHeaders.SET_COOKIE, cookieUtil.createAccessTokenCookie(jwtResponse.getAccessToken()));
            response.addHeader(HttpHeaders.SET_COOKIE, cookieUtil.createRefreshTokenCookie(jwtResponse.getRefreshToken()));
            return ApiResponse.success(jwtResponse);
        } catch (Exception e) {
            log.error("刷新令牌失败", e);
            return ApiResponse.error(500, e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            log.debug("开始处理登出请求");
            // 获取并失效访问令牌
            Optional<String> accessToken = cookieUtil.getAccessTokenFromCookie(request);
            accessToken.ifPresent(token -> {
                log.debug("将令牌加入黑名单: {}", token);
                authService.logout(token);
            });

            // 清除 Cookie
            response.addHeader(HttpHeaders.SET_COOKIE, cookieUtil.deleteAccessTokenCookie());
            response.addHeader(HttpHeaders.SET_COOKIE, cookieUtil.deleteRefreshTokenCookie());
            log.debug("登出成功");

            return ApiResponse.success(null);
        } catch (Exception e) {
            log.error("登出失败", e);
            return ApiResponse.error(500, e.getMessage());
        }
    }
}
