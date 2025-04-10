package com.wangguangwu.springboot3_auth_jwt.service;

import com.wangguangwu.springboot3_auth_jwt.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户服务
 *
 * @author wangguangwu
 */
@Service
public class UserService implements UserDetailsService {

    private final Map<String, User> userMap = new ConcurrentHashMap<>();

    public UserService(PasswordEncoder passwordEncoder) {
        // 初始化超级管理员
        User root = new User();
        root.setUsername("root");
        root.setPassword(passwordEncoder.encode("root"));
        root.setRole("ROLE_ROOT");
        root.setPermissions(Set.of(
                "ROOT",
                "ADMIN",
                "USER",
                "user:create",
                "user:read",
                "user:update",
                "user:delete",
                "system:manage"
        ));
        userMap.put(root.getUsername(), root);

        // 初始化管理员
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin"));
        admin.setRole("ROLE_ADMIN");
        admin.setPermissions(Set.of(
                "ADMIN",
                "USER",
                "user:create",
                "user:read",
                "user:update"
        ));
        userMap.put(admin.getUsername(), admin);

        // 初始化普通用户
        User user = new User();
        user.setUsername("user");
        user.setPassword(passwordEncoder.encode("user"));
        user.setRole("ROLE_USER");
        user.setPermissions(Set.of(
                "USER",
                "user:read"
        ));
        userMap.put(user.getUsername(), user);
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMap.get(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户名不存在");
        }
        return user;
    }
}
