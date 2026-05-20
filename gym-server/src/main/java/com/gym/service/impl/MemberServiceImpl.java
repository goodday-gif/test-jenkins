package com.gym.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gym.common.BusinessException;
import com.gym.entity.Member;
import com.gym.mapper.MemberMapper;
import com.gym.security.JwtUtils;
import com.gym.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements MemberService {

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public Map<String, Object> login(String username, String password) {
        Member member = memberMapper.selectByUsername(username);
        if (member == null) {
            throw new BusinessException("用户名或密码错误");
        }
        if (member.getStatus() != 1) {
            throw new BusinessException("账号已被禁用");
        }
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        String token = jwtUtils.generateToken(member.getUsername(), "member", member.getId());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);

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
        result.put("userInfo", userInfo);

        return result;
    }

    @Override
    public void register(Member member) {
        // 检查用户名是否已存在
        Member existing = memberMapper.selectByUsername(member.getUsername());
        if (existing != null) {
            throw new BusinessException("用户名已存在");
        }

        // 加密密码
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        // 设置默认值
        member.setGender(0);
        member.setMemberLevel(0);
        member.setBalance(BigDecimal.ZERO);
        member.setStatus(1);
        member.setDeleted(0);
        member.setCreateTime(LocalDateTime.now());
        member.setUpdateTime(LocalDateTime.now());

        memberMapper.insert(member);
    }

    @Override
    public Member getByUsername(String username) {
        return memberMapper.selectByUsername(username);
    }

    @Override
    public IPage<Member> getMemberList(Page<Member> page, String keyword, Integer status) {
        LambdaQueryWrapper<Member> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Member::getNickname, keyword)
                    .or().like(Member::getPhone, keyword)
                    .or().like(Member::getUsername, keyword));
        }
        wrapper.eq(status != null, Member::getStatus, status);
        wrapper.orderByDesc(Member::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recharge(Long id, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("充值金额必须大于0");
        }
        Member member = getById(id);
        if (member == null) {
            throw new BusinessException("会员不存在");
        }
        member.setBalance(member.getBalance().add(amount));
        updateById(member);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void renew(Long id, Integer months) {
        if (months == null || months <= 0) {
            throw new BusinessException("续费月数必须大于0");
        }
        Member member = getById(id);
        if (member == null) {
            throw new BusinessException("会员不存在");
        }
        LocalDateTime expireTime = member.getExpireTime();
        if (expireTime == null || expireTime.isBefore(LocalDateTime.now())) {
            expireTime = LocalDateTime.now();
        }
        member.setExpireTime(expireTime.plusMonths(months));
        updateById(member);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void toggleStatus(Long id) {
        Member member = getById(id);
        if (member == null) {
            throw new BusinessException("会员不存在");
        }
        member.setStatus(member.getStatus() == 1 ? 0 : 1);
        updateById(member);
    }
}
