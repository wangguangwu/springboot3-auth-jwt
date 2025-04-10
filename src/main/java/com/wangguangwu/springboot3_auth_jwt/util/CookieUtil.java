package com.wangguangwu.springboot3_auth_jwt.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Cookie 工具类
 * 用于管理 HTTP Cookie
 *
 * @author wangguangwu
 */
@Component
public class CookieUtil {

    @Value("${jwt.cookie.domain}")
    private String domain;

    @Value("${jwt.cookie.access-token-name}")
    private String accessTokenName;

    @Value("${jwt.cookie.refresh-token-name}")
    private String refreshTokenName;

    /**
     * 创建访问令牌 Cookie
     *
     * @param token JWT Token
     */
    public String createAccessTokenCookie(String token) {
        return createCookie(accessTokenName, token, 3600);
    }

    /**
     * 创建刷新令牌 Cookie
     *
     * @param token    JWT Token
     */
    public String createRefreshTokenCookie(String token) {
        return createCookie(refreshTokenName, token, 86400);
    }


    /**
     * 从请求中获取访问令牌
     *
     * @param request HTTP 请求
     * @return 访问令牌，如果不存在返回 null
     */
    public Optional<String> getAccessTokenFromCookie(HttpServletRequest request) {
        return Optional.ofNullable(getCookieValue(request, accessTokenName));
    }

    /**
     * 从请求中获取刷新令牌
     *
     * @param request HTTP 请求
     * @return 刷新令牌，如果不存在返回 null
     */
    public Optional<String> getRefreshTokenFromCookie(HttpServletRequest request) {
        return Optional.ofNullable(getCookieValue(request, refreshTokenName));
    }

    /**
     * 删除访问令牌 Cookie
     *
     */
    public String deleteAccessTokenCookie() {
        return deleteCookie(accessTokenName);
    }

    /**
     * 删除刷新令牌 Cookie
     *
     */
    public String deleteRefreshTokenCookie() {
        return deleteCookie(refreshTokenName);
    }

    /**
     * 创建 Cookie
     *
     * @param name     Cookie 名称
     * @param value    Cookie 值
     * @param maxAge   Cookie 有效期（秒）
     */
    private String createCookie(String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setDomain(domain);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        return String.format("%s=%s; Domain=%s; Path=/; Max-Age=%d; HttpOnly; Secure",
                name, value, domain, maxAge);
    }

    /**
     * 获取 Cookie 值
     *
     * @param request    HTTP 请求
     * @param cookieName Cookie 名称
     * @return Cookie 值，如果不存在返回 null
     */
    private String getCookieValue(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    /**
     * 删除 Cookie
     *
     * @param name     Cookie 名称
     */
    private String deleteCookie(String name) {
        return String.format("%s=; Domain=%s; Path=/; Max-Age=0; HttpOnly; Secure",
                name, domain);
    }
}
