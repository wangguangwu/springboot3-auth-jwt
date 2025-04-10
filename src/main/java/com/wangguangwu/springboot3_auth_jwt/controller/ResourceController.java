package com.wangguangwu.springboot3_auth_jwt.controller;

import com.wangguangwu.springboot3_auth_jwt.model.ApiResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 资源控制器
 * 用于测试不同角色的访问权限
 *
 * @author wangguangwu
 */
@RestController
@RequestMapping("/api/resources")
public class ResourceController {

    @GetMapping("/public")
    public ApiResponse<Map<String, String>> getPublicResource() {
        Map<String, String> data = new HashMap<>();
        data.put("message", "这是一个公开资源，所有已认证的用户都可以访问");
        return ApiResponse.success(data);
    }

    @GetMapping("/user")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN', 'ROOT')")
    public ApiResponse<Map<String, String>> getUserResource() {
        Map<String, String> data = new HashMap<>();
        data.put("message", "这是用户资源，USER、ADMIN 和 ROOT 角色可以访问");
        return ApiResponse.success(data);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ROOT')")
    public ApiResponse<Map<String, String>> getAdminResource() {
        Map<String, String> data = new HashMap<>();
        data.put("message", "这是管理员资源，ADMIN 和 ROOT 角色可以访问");
        return ApiResponse.success(data);
    }

    @GetMapping("/super-admin")
    @PreAuthorize("hasAuthority('ROOT')")
    public ApiResponse<Map<String, String>> getSuperAdminResource() {
        Map<String, String> data = new HashMap<>();
        data.put("message", "这是超级管理员资源，只有 ROOT 角色可以访问");
        return ApiResponse.success(data);
    }
}
