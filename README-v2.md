# Spring Boot 3 JWT Authentication Demo (v2)

这是 Spring Boot 3 JWT 认证授权示例项目的扩展版本，在基础版本上增加了更多功能。

## 新增特性

- 刷新 Token 机制
- Token 黑名单
- Cookie 管理
- 记住我功能

## 配置说明

主要配置项（application.yml）：

```yaml
jwt:
  # JWT 签名密钥
  secret: your-secret-key
  # JWT 访问令牌过期时间（毫秒）
  access-token:
    expiration: 604800000  # 7天
  # JWT 刷新令牌过期时间（毫秒）
  refresh-token:
    expiration: 1209600000  # 14天
  # JWT 记住我访问令牌过期时间（毫秒）
  remember-me:
    access-token:
      expiration: 604800000  # 7天
    refresh-token:
      expiration: 1209600000  # 14天
  # JWT 发行者
  issuer: wangguangwu
  # JWT 受众
  audience: jwt-demo
  # Cookie 配置
  cookie:
    domain: localhost
    access-token-name: access_token
    refresh-token-name: refresh_token

spring:
  redis:
    host: localhost
    port: 6379
```

## 核心功能实现

### 1. 刷新 Token 机制

- 支持访问令牌和刷新令牌
- 自动刷新过期的访问令牌
- 刷新令牌的安全处理

### 2. Token 黑名单

- 使用 Redis 存储已失效的 Token
- 定时清理过期的 Token
- 支持主动使 Token 失效

### 3. Cookie 管理

- 使用 `CookieUtil` 工具类管理 Cookie
- 自动在响应中设置 Cookie
- 支持 Cookie 属性配置（domain、httpOnly 等）

### 4. 记住我功能

- 使用 Spring Security 的 Remember Me 功能
- 支持延长 Token 过期时间
- 记住我状态持久化
