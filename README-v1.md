# Spring Boot 3 JWT Authentication Demo (v1)

这是 Spring Boot 3 JWT 认证授权示例项目的基础版本，实现了最基本的 JWT 认证和授权功能。

## 特性

- JWT Token 的生成和验证
- 基于 Token 的认证
- 基于角色的权限控制（RBAC）

## 配置说明

主要配置项（application.yml）：

```yaml
jwt:
  # JWT 签名密钥
  secret: 404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
  # 访问令牌过期时间：30分钟
  access-token-expiration: 1800000
  # JWT 发行者
  issuer: wangguangwu
  # JWT 接收者
  audience: springboot3-auth-jwt
```

## 核心功能实现

### 1. JWT Token 生成和验证

- 使用 HMAC-SHA512 算法签名
- Token 包含用户信息和权限
- 支持 Token 有效性验证

### 2. 基于 Token 的认证

- 实现 JWT 认证过滤器
- 从请求头获取 Token
- 验证 Token 并设置认证信息

### 3. 基于角色的权限控制

- 使用 Spring Security 注解控制权限
- 支持多角色授权
- 灵活的权限配置

### 4. 错误处理

- 401：Token 无效或过期
- 403：权限不足
- 统一的错误响应格式
