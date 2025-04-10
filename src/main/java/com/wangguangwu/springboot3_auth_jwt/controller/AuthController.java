package com.wangguangwu.springboot3_auth_jwt.controller;

import com.wangguangwu.springboot3_auth_jwt.model.ApiResponse;
import com.wangguangwu.springboot3_auth_jwt.model.JwtResponse;
import com.wangguangwu.springboot3_auth_jwt.model.LoginRequest;
import com.wangguangwu.springboot3_auth_jwt.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证控制器
 * 处理用户登录、登出等认证相关的请求
 *
 * @author wangguangwu
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    /**
     * 认证服务
     */
    private final AuthService authService;

    /**
     * 处理用户登录请求
     *
     * @param request 登录请求对象
     * @return 包含访问令牌的响应
     */
    @PostMapping("/login")
    public ApiResponse<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }
}
