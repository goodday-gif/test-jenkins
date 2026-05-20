package com.gym.controller.front;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.common.BusinessException;
import com.gym.common.PageResult;
import com.gym.common.Result;
import com.gym.entity.CourseInteract;
import com.gym.entity.Member;
import com.gym.entity.Orders;
import com.gym.security.LoginUser;
import com.gym.service.CourseInteractService;
import com.gym.service.MemberService;
import com.gym.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/front/member")
public class MemberCenterController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private CourseInteractService courseInteractService;

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * 修改个人信息
     */
    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestBody Map<String, Object> body) {
        Long memberId = getCurrentMemberId();
        Member member = memberService.getById(memberId);
        if (member == null) {
            throw new BusinessException("用户不存在");
        }
        if (body.containsKey("nickname")) {
            member.setNickname((String) body.get("nickname"));
        }
        if (body.containsKey("avatar")) {
            member.setAvatar((String) body.get("avatar"));
        }
        if (body.containsKey("phone")) {
            member.setPhone((String) body.get("phone"));
        }
        if (body.containsKey("gender")) {
            member.setGender((Integer) body.get("gender"));
        }
        memberService.updateById(member);
        return Result.success("修改成功", null);
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result<Void> updatePassword(@RequestBody Map<String, String> body) {
        Long memberId = getCurrentMemberId();
        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");
        if (!StringUtils.hasText(oldPassword) || !StringUtils.hasText(newPassword)) {
            throw new BusinessException("旧密码和新密码不能为空");
        }
        Member member = memberService.getById(memberId);
        if (member == null) {
            throw new BusinessException("用户不存在");
        }
        if (!passwordEncoder.matches(oldPassword, member.getPassword())) {
            throw new BusinessException("旧密码错误");
        }
        member.setPassword(passwordEncoder.encode(newPassword));
        memberService.updateById(member);
        return Result.success("修改密码成功", null);
    }

    /**
     * 我的收藏/点赞的教程列表（type: 1-点赞 2-收藏）
     */
    @GetMapping("/tutorials")
    public Result<PageResult<CourseInteract>> myTutorials(
            @RequestParam Integer type,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long memberId = getCurrentMemberId();
        LambdaQueryWrapper<CourseInteract> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseInteract::getMemberId, memberId)
               .eq(CourseInteract::getType, type)
               .orderByDesc(CourseInteract::getCreateTime);
        Page<CourseInteract> page = new Page<>(pageNum, pageSize);
        IPage<CourseInteract> result = courseInteractService.page(page, wrapper);
        return Result.success(new PageResult<>(result.getTotal(), result.getRecords()));
    }

    /**
     * 我的订单列表（分页）
     */
    @GetMapping("/orders")
    public Result<PageResult<Orders>> myOrders(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long memberId = getCurrentMemberId();
        Page<Orders> page = new Page<>(pageNum, pageSize);
        IPage<Orders> result = ordersService.getOrderList(page, memberId, null, null);
        return Result.success(new PageResult<>(result.getTotal(), result.getRecords()));
    }

    private Long getCurrentMemberId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof LoginUser) {
            LoginUser loginUser = (LoginUser) auth.getPrincipal();
            if ("member".equals(loginUser.getUserType())) {
                return loginUser.getId();
            }
        }
        throw new BusinessException(401, "请先登录");
    }
}
