package com.wangguangwu.springboot3_auth_jwt.security;

import com.wangguangwu.springboot3_auth_jwt.service.TokenBlacklistService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Token 验证器
 * 用于验证 JWT Token 的有效性
 *
 * @author wangguangwu
 */
@Component
@RequiredArgsConstructor
public class TokenValidator {

    private final TokenBlacklistService tokenBlacklistService;

    /**
     * 验证 Token 是否有效
     *
     * @param token JWT Token
     * @param key   密钥
     * @return 如果 Token 有效返回 true，否则返回 false
     */
    public boolean isTokenUnValid(String token, SecretKey key) {
        try {
            // 检查 Token 是否在黑名单中
            if (tokenBlacklistService.isTokenBlacklisted(token)) {
                return true;
            }

            // 解析 Token
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // 检查 Token 是否过期
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}
