package com.gym.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gym.entity.Member;

import java.math.BigDecimal;
import java.util.Map;

public interface MemberService extends IService<Member> {

    /**
     * 会员登录
     */
    Map<String, Object> login(String username, String password);

    /**
     * 会员注册
     */
    void register(Member member);

    /**
     * 根据用户名查询
     */
    Member getByUsername(String username);

    /**
     * 分页查询会员
     */
    IPage<Member> getMemberList(Page<Member> page, String keyword, Integer status);

    /**
     * 会员充值
     */
    void recharge(Long id, BigDecimal amount);

    /**
     * 会员续费
     */
    void renew(Long id, Integer months);

    /**
     * 禁用/解封会员
     */
    void toggleStatus(Long id);
}
