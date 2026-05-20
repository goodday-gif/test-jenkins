package com.gym.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gym.entity.SysUser;

import java.util.Map;

public interface SysUserService extends IService<SysUser> {

    /**
     * 管理员登录
     */
    Map<String, Object> login(String username, String password);

    /**
     * 根据用户名查询
     */
    SysUser getByUsername(String username);
}
