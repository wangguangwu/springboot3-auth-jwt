# Spring Boot 3 JWT Authentication Demo

这是一个使用 Spring Boot 3 和 JWT（JSON Web Token）实现的认证授权示例项目。项目展示了如何在 Spring Boot 应用中实现基于 JWT 的用户认证和基于角色的访问控制。

## 技术栈

- **Spring Boot**: 3.2.3
- **Spring Security**: 6.2.2
- **Java**: 17
- **JWT**: 0.12.6
- **前端**: HTML5, CSS3, JavaScript

## 功能特性

- 用户认证（登录）
- JWT Token 生成和验证
- 基于角色的访问控制（RBAC）
- Token 过期处理
- 全局异常处理
- 响应式前端界面

## 版本说明

项目分为两个版本：

1. [v1 基础版本](README-v1.md)
   - JWT Token 的生成和验证
   - 基于 Token 的认证
   - 基于角色的权限控制

2. [v2 扩展版本](README-v2.md)
   - 刷新 Token 机制
   - Token 黑名单
   - Cookie 管理
   - 记住我功能

## 快速开始

### 前置条件

- JDK 17 或更高版本
- Maven 3.6 或更高版本

### 运行项目

1. 克隆项目

   ```bash
   git clone https://github.com/wangguangwu/springboot3-auth-jwt.git
   ```

2. 进入项目目录

   ```bash
   cd springboot3-auth-jwt
   ```

3. 编译运行

   ```bash
   mvn spring-boot:run
   ```

4. 访问应用

   ```text
   http://localhost:8080
   ```

## 项目结构

```
src/main/java/com/wangguangwu/springboot3_auth_jwt/
├── config/          # 配置类
├── controller/      # 控制器
├── exception/       # 异常处理
├── model/          # 数据模型
├── security/       # 安全相关
├── service/        # 服务层
└── util/           # 工具类
```

## 测试账号

项目内置了以下测试账号：

1. 普通用户
   - 用户名：`user`
   - 密码：`password`
   - 权限：USER

2. 管理员
   - 用户名：`admin`
   - 密码：`password`
   - 权限：USER, ADMIN

3. 超级管理员
   - 用户名：`root`
   - 密码：`password`
   - 权限：USER, ADMIN, ROOT

## API 接口和调用方式

### 认证接口

#### 用户登录

- 请求：`POST /api/auth/login`
- 请求体：
  ```json
  {
    "username": "user",
    "password": "password"
  }
  ```
- 响应：
  ```json
  {
    "code": 200,
    "message": "成功",
    "data": {
      "token": "eyJhbGciOiJIUzI1NiJ9...",
      "username": "user"
    }
  }
  ```

### 资源接口

所有资源接口都需要在请求头中包含 JWT Token：

```http
Authorization: Bearer your-jwt-token
```

#### 公共资源

- 请求：`GET /api/resources/public`
- 权限要求：需要认证
- 响应示例：

  ```json
  {
    "code": 200,
    "message": "成功",
    "data": {
      "message": "这是一个公开资源，所有已认证的用户都可以访问"
    }
  }
  ```

#### 用户资源

- 请求：`GET /api/resources/user`
- 权限要求：需要 USER 角色

#### 管理员资源

- 请求：`GET /api/resources/admin`
- 权限要求：需要 ADMIN 角色

#### 超级管理员资源

- 请求：`GET /api/resources/super-admin`
- 权限要求：需要 ROOT 角色

## 核心功能实现

### 1. JWT 认证流程

1. 用户登录
   - 用户提交用户名和密码
   - 后端验证凭证
   - 生成 JWT Token，包含用户信息和权限

2. 请求鉴权
   - 前端在请求头中添加 Token
   - JWT 过滤器拦截请求
   - 验证 Token 的有效性
   - 解析用户信息和权限

### 2. 权限控制

1. 基于角色的访问控制（RBAC）
   - 使用 Spring Security 的注解进行权限控制
   - 支持多角色授权
   - 灵活的权限配置

2. JWT Token 安全特性
   - 包含用户信息和权限
   - 数字签名防止篡改
   - 过期时间控制
   - 发行者和接收者验证

### 3. 异常处理

1. 全局异常处理
   - 认证异常（401）
   - 权限不足（403）
   - 参数验证异常（400）
   - 系统异常（500）

2. 统一响应格式
   - 标准的响应结构
   - 清晰的错误信息
   - 友好的错误提示

## 安全特性

- 使用 BCrypt 加密密码
- JWT Token 包含用户信息和权限
- Token 过期机制
- 基于角色的访问控制
- 全局异常处理

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
  # JWT 发行者
  issuer: wangguangwu
  # JWT 受众
  audience: jwt-demo
```

## 贡献

欢迎提交 Issue 和 Pull Request。

## 许可证

[MIT License](LICENSE)
