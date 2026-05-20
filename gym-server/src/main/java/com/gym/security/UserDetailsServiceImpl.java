package com.gym.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * UserDetailsService 实现类
 * 登录逻辑在各 Service 中手动验证，此类仅用于满足 Spring Security 配置要求
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 实际登录逻辑在 SysUserService / MemberService 中处理
        throw new UsernameNotFoundException("请使用API登录接口");
    }
}
