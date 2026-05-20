package com.gym.controller.front;

import com.gym.common.BusinessException;
import com.gym.common.Result;
import com.gym.entity.Member;
import com.gym.security.LoginUser;
import com.gym.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/front")
public class MemberLoginController {

    @Autowired
    private MemberService memberService;

    /**
     * 会员登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> loginForm) {
        String username = loginForm.get("username");
        String password = loginForm.get("password");
        if (username == null || password == null) {
            throw new BusinessException("用户名和密码不能为空");
        }
        Map<String, Object> result = memberService.login(username, password);
        return Result.success("登录成功", result);
    }

    /**
     * 会员注册
     */
    @PostMapping("/register")
    public Result<Void> register(@RequestBody Map<String, String> registerForm) {
        String username = registerForm.get("username");
        String password = registerForm.get("password");
        String nickname = registerForm.get("nickname");
        String phone = registerForm.get("phone");

        if (username == null || password == null) {
            throw new BusinessException("用户名和密码不能为空");
        }

        Member member = new Member();
        member.setUsername(username);
        member.setPassword(password);
        member.setNickname(nickname != null ? nickname : username);
        member.setPhone(phone);

        memberService.register(member);
        return Result.success("注册成功", null);
    }

    /**
     * 获取当前会员信息
     */
    @GetMapping("/info")
    public Result<Map<String, Object>> info() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof LoginUser)) {
            throw new BusinessException(401, "未登录");
        }
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();

        Member member = memberService.getByUsername(loginUser.getUsername());
        if (member == null) {
            throw new BusinessException("用户不存在");
        }

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", member.getId());
        userInfo.put("username", member.getUsername());
        userInfo.put("nickname", member.getNickname());
        userInfo.put("avatar", member.getAvatar());
        userInfo.put("phone", member.getPhone());
        userInfo.put("gender", member.getGender());
        userInfo.put("memberLevel", member.getMemberLevel());
        userInfo.put("balance", member.getBalance());
        userInfo.put("expireTime", member.getExpireTime());

        return Result.success(userInfo);
    }
}
