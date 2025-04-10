package com.wangguangwu.springboot3_auth_jwt.service;

import com.wangguangwu.springboot3_auth_jwt.model.JwtResponse;
import com.wangguangwu.springboot3_auth_jwt.model.LoginRequest;
import com.wangguangwu.springboot3_auth_jwt.model.User;
import com.wangguangwu.springboot3_auth_jwt.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * 认证服务
 *
 * @author wangguangwu
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    /**
     * 认证管理器
     */
    private final AuthenticationManager authenticationManager;

    /**
     * JWT 工具类
     */
    private final JwtUtil jwtUtil;

    /**
     * 登录
     *
     * @param loginRequest 登录请求
     * @return JWT 响应
     */
    public JwtResponse login(LoginRequest loginRequest) {
        // 认证
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        // 生成访问令牌
        User user = (User) authentication.getPrincipal();
        String accessToken = jwtUtil.generateToken(user);

        // 返回 JWT 响应
        return new JwtResponse(accessToken);
    }
}
