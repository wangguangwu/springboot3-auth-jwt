package com.wangguangwu.springboot3_auth_jwt.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Token 黑名单服务
 * 用于管理已失效的 Token
 *
 * @author wangguangwu
 */
@Service
public class TokenBlacklistService {

    private final Map<String, Long> blacklist = new ConcurrentHashMap<>();

    public TokenBlacklistService() {
        // 每小时清理一次过期的 token
        ScheduledExecutorService cleanupExecutor = Executors.newSingleThreadScheduledExecutor();
        cleanupExecutor.scheduleAtFixedRate(this::cleanupExpiredTokens, 1, 1, TimeUnit.HOURS);
    }

    /**
     * 将 Token 加入黑名单
     *
     * @param token    JWT Token
     * @param timeout  超时时间
     * @param timeUnit 时间单位
     */
    public void addToBlacklist(String token, long timeout, TimeUnit timeUnit) {
        long expirationTime = System.currentTimeMillis() + timeUnit.toMillis(timeout);
        blacklist.put(token, expirationTime);
    }

    /**
     * 检查 Token 是否在黑名单中
     *
     * @param token JWT Token
     * @return 如果在黑名单中返回 true，否则返回 false
     */
    public boolean isTokenBlacklisted(String token) {
        Long expirationTime = blacklist.get(token);
        if (expirationTime == null) {
            return false;
        }
        if (System.currentTimeMillis() > expirationTime) {
            blacklist.remove(token);
            return false;
        }
        return true;
    }

    private void cleanupExpiredTokens() {
        long now = System.currentTimeMillis();
        blacklist.entrySet().removeIf(entry -> entry.getValue() <= now);
    }
}
