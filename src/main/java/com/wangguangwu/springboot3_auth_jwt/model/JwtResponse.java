package com.wangguangwu.springboot3_auth_jwt.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * JWT 响应
 *
 * @author wangguangwu
 */
@Data
@AllArgsConstructor
public class JwtResponse {

    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * 刷新令牌
     */
    private String refreshToken;
}
