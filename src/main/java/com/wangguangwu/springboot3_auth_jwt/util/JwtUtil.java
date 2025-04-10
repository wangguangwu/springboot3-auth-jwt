package com.wangguangwu.springboot3_auth_jwt.util;

import com.wangguangwu.springboot3_auth_jwt.security.TokenValidator;
import com.wangguangwu.springboot3_auth_jwt.service.TokenBlacklistService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * JWT 工具类
 * 用于处理 JWT 令牌的生成、解析和验证
 *
 * @author wangguangwu
 */
@Component
public class JwtUtil {

    private static final long STARTUP_TIMESTAMP = System.currentTimeMillis();

    private final SecretKey key;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;
    private final long rememberMeAccessTokenExpiration;
    private final Long rememberMeRefreshTokenExpiration;
    private final String issuer;
    private final String audience;
    private final TokenValidator tokenValidator;
    private final TokenBlacklistService tokenBlacklistService;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token.expiration}") Long accessTokenExpiration,
            @Value("${jwt.refresh-token.expiration}") Long refreshTokenExpiration,
            @Value("${jwt.remember-me.access-token.expiration}") Long rememberMeAccessTokenExpiration,
            @Value("${jwt.remember-me.refresh-token.expiration}") Long rememberMeRefreshTokenExpiration,
            @Value("${jwt.issuer}") String issuer,
            @Value("${jwt.audience}") String audience,
            TokenValidator tokenValidator,
            TokenBlacklistService tokenBlacklistService
    ) {
        // 使用启动时间作为盐值，确保每次重启后生成的密钥都不同
        String saltedSecret = secret + "-" + STARTUP_TIMESTAMP;
        this.key = Keys.hmacShaKeyFor(saltedSecret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
        this.rememberMeAccessTokenExpiration = rememberMeAccessTokenExpiration;
        this.rememberMeRefreshTokenExpiration = rememberMeRefreshTokenExpiration;
        this.issuer = issuer;
        this.audience = audience;
        this.tokenValidator = tokenValidator;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .requireIssuer(issuer)
                    .requireAudience(audience)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 验证令牌并获取用户名
     *
     * @param token 令牌
     * @return 用户名，如果令牌无效则返回 null
     */
    public String validateTokenAndGetUsername(String token) {
        try {
            // 验证令牌是否有效
            if (tokenValidator.isTokenUnValid(token, key)) {
                return null;
            }

            Claims claims = extractAllClaims(token);
            if (claims == null) {
                return null;
            }

            // 检查令牌是否过期
            if (claims.getExpiration().before(new Date())) {
                return null;
            }

            return claims.getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 使令牌失效
     *
     * @param token 令牌
     */
    public void invalidateToken(String token) {
        Claims claims = extractAllClaims(token);
        if (claims != null) {
            long expirationTime = claims.getExpiration().getTime();
            long timeToLive = expirationTime - System.currentTimeMillis();
            tokenBlacklistService.addToBlacklist(token, timeToLive, TimeUnit.MILLISECONDS);
        }
    }


    /**
     * 生成访问令牌
     *
     * @param user 用户信息
     * @return JWT Token
     */
    public String generateAccessToken(UserDetails user) {
        List<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(user.getUsername())
                .claim("roles", roles)
                .claim("type", "access")
                .issuer(issuer)
                .audience().add(audience).and()
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(key)
                .compact();
    }

    /**
     * 生成刷新令牌
     *
     * @return 刷新令牌字符串
     */
    public String generateRefreshToken(UserDetails userDetails) {
        return generateToken(userDetails, refreshTokenExpiration);
    }

    /**
     * 生成记住我访问令牌
     *
     * @param userDetails 用户信息
     * @return 访问令牌
     */
    public String generateRememberMeAccessToken(UserDetails userDetails) {
        return generateToken(userDetails, rememberMeAccessTokenExpiration);
    }

    /**
     * 生成记住我刷新令牌
     *
     * @param userDetails 用户信息
     * @return 刷新令牌
     */
    public String generateRememberMeRefreshToken(UserDetails userDetails) {
        return generateToken(userDetails, rememberMeRefreshTokenExpiration);
    }

    private String generateToken(UserDetails userDetails, long expiration) {
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        String tokenId = UUID.randomUUID().toString();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .id(tokenId)
                .subject(userDetails.getUsername())
                .claim("roles", roles)
                .claim("type", expiration == refreshTokenExpiration || expiration == rememberMeRefreshTokenExpiration ? "refresh" : "access")
                .issuer(issuer)
                .audience().add(audience).and()
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    /**
     * 验证 JWT 令牌
     *
     * @param token       JWT 令牌
     * @param userDetails 用户信息
     * @return 如果令牌有效返回 true，否则返回 false
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            // 验证令牌是否有效
            if (tokenValidator.isTokenUnValid(token, key)) {
                return false;
            }

            Claims claims = extractAllClaims(token);
            if (claims == null) {
                return false;
            }

            // 检查令牌是否过期
            if (claims.getExpiration().before(new Date())) {
                return false;
            }

            // 检查用户名是否匹配
            return claims.getSubject().equals(userDetails.getUsername());
        } catch (Exception e) {
            return false;
        }
    }
}
