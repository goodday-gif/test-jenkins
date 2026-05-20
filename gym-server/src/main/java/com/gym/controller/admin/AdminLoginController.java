package com.gym.controller.admin;

import com.gym.common.BusinessException;
import com.gym.common.Result;
import com.gym.entity.SysUser;
import com.gym.security.LoginUser;
import com.gym.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminLoginController {

    @Autowired
    private SysUserService sysUserService;

    /**
     * 管理员登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> loginForm) {
        String username = loginForm.get("username");
        String password = loginForm.get("password");
        if (username == null || password == null) {
            throw new BusinessException("用户名和密码不能为空");
        }
        Map<String, Object> result = sysUserService.login(username, password);
        return Result.success("登录成功", result);
    }

    /**
     * 获取当前管理员信息
     */
    @GetMapping("/info")
    public Result<Map<String, Object>> info() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();

        SysUser user = sysUserService.getByUsername(loginUser.getUsername());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("realName", user.getRealName());
        userInfo.put("avatar", user.getAvatar());
        userInfo.put("roleType", user.getRoleType());
        userInfo.put("phone", user.getPhone());

        return Result.success(userInfo);
    }
}
