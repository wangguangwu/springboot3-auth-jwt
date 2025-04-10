package com.wangguangwu.springboot3_auth_jwt.service;

import com.wangguangwu.springboot3_auth_jwt.model.JwtResponse;
import com.wangguangwu.springboot3_auth_jwt.model.LoginRequest;
import com.wangguangwu.springboot3_auth_jwt.model.User;
import com.wangguangwu.springboot3_auth_jwt.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
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

    private final UserDetailsService userDetailsService;

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

        User user = (User) authentication.getPrincipal();
        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        // 如果选择了“记住我”，则延长令牌的过期时间
        if (loginRequest.isRememberMe()) {
            accessToken = jwtUtil.generateRememberMeAccessToken(user);
            refreshToken = jwtUtil.generateRememberMeRefreshToken(user);
        }

        return new JwtResponse(accessToken, refreshToken);
    }

    /**
     * 刷新访问令牌
     *
     * @param refreshToken 刷新令牌
     * @return JWT 响应
     */
    public JwtResponse refreshToken(String refreshToken) {
        // 验证刷新令牌
        String username = jwtUtil.validateTokenAndGetUsername(refreshToken);
        if (username == null) {
            throw new IllegalArgumentException("无效的刷新令牌");
        }

        // 获取用户信息
        User user = (User) userDetailsService.loadUserByUsername(username);

        // 将旧的刷新令牌加入黑名单
        jwtUtil.invalidateToken(refreshToken);

        // 生成新的访问令牌和刷新令牌
        String newAccessToken = jwtUtil.generateAccessToken(user);
        String newRefreshToken = jwtUtil.generateRefreshToken(user);

        return new JwtResponse(newAccessToken, newRefreshToken);
    }

    /**
     * 登出
     *
     * @param token 访问令牌
     */
    public void logout(String token) {
        // 将令牌加入黑名单
        jwtUtil.invalidateToken(token);
    }
}
