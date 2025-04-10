package com.wangguangwu.springboot3_auth_jwt.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * JWT 工具类
 * 用于处理 JWT 令牌的生成、解析和验证
 *
 * @author wangguangwu
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.audience}")
    private String audience;

    /**
     * 获取签名密钥
     *
     * @return 签名密钥
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 从令牌中提取用户名
     *
     * @param token JWT 令牌
     * @return 用户名
     */
    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * 生成 JWT 令牌
     *
     * @param userDetails 用户信息
     * @return JWT 令牌
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", userDetails.getAuthorities().iterator().next().getAuthority());

        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .issuer(issuer)
                .audience().add(audience).and()
                .id(java.util.UUID.randomUUID().toString())
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 验证 JWT 令牌
     *
     * @param token       JWT 令牌
     * @param userDetails 用户信息
     * @return 如果令牌有效返回 true，否则返回 false
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // 检查用户名是否匹配
            String username = claims.getSubject();
            if (!username.equals(userDetails.getUsername())) {
                return false;
            }

            // 检查令牌是否过期
            Date expiration = claims.getExpiration();
            if (expiration.before(new Date())) {
                return false;
            }

            // 检查发行者
            String tokenIssuer = claims.getIssuer();
            if (!issuer.equals(tokenIssuer)) {
                return false;
            }

            // 检查接收者
            Set<String> audiences = new HashSet<>(claims.getAudience());
            if (!audiences.contains(audience)) {
                return false;
            }

            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
