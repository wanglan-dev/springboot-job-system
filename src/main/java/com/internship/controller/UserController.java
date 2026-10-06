package com.internship.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.internship.entity.User;
import com.internship.entity.Role;
import com.internship.service.UserService;
import com.internship.service.RoleService;
import com.internship.util.JwtUtil;
import com.internship.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 登录接口
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");

        if (username == null || password == null) {
            return Result.error("用户名或密码不能为空");
        }

        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        User user = userService.getOne(wrapper);

        if (user == null) {
            return Result.error("用户不存在");
        }

        // 明文密码比对（为了演示方便，生产环境请使用BCrypt）
        if (!password.equals(user.getPassword())) {
            return Result.error("密码错误");
        }

        if (user.getStatus() == 0) {
            return Result.error("账号已被禁用");
        }

        // 查询角色
        Role role = roleService.getById(user.getRoleId());
        String roleCode = role != null ? role.getRoleCode() : "USER";

        // 生成JWT Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), roleCode);

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userId", user.getId());
        data.put("username", user.getUsername());
        data.put("realName", user.getRealName());
        data.put("roleCode", roleCode);
        data.put("roleName", role != null ? role.getRoleName() : "");

        return Result.success(data);
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/info")
    public Result<Map<String, Object>> getUserInfo(@RequestAttribute("userId") Long userId) {
        User user = userService.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        Role role = roleService.getById(user.getRoleId());
        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("realName", user.getRealName());
        data.put("avatar", user.getAvatar());
        data.put("roleCode", role != null ? role.getRoleCode() : "");
        data.put("roleName", role != null ? role.getRoleName() : "");
        return Result.success(data);
    }

    /**
     * 注册（简化版）
     */
    @PostMapping("/register")
    public Result<String> register(@RequestBody User user) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", user.getUsername());
        if (userService.count(wrapper) > 0) {
            return Result.error("用户名已存在");
        }
        user.setStatus(1);
        userService.save(user);
        return Result.success("注册成功");
    }
}
