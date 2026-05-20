package com.gym.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.common.PageResult;
import com.gym.common.Result;
import com.gym.entity.Member;
import com.gym.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.metadata.IPage;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/member")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @GetMapping("/list")
    public Result<PageResult<Member>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        Page<Member> pageParam = new Page<>(page, pageSize);
        IPage<Member> pageResult = memberService.getMemberList(pageParam, keyword, status);
        return Result.success(new PageResult<>(pageResult.getTotal(), pageResult.getRecords()));
    }

    @GetMapping("/{id}")
    public Result<Member> detail(@PathVariable Long id) {
        return Result.success(memberService.getById(id));
    }

    @PostMapping("/recharge")
    public Result<Void> recharge(@RequestBody Map<String, Object> params) {
        Long memberId = Long.valueOf(params.get("memberId").toString());
        BigDecimal amount = new BigDecimal(params.get("amount").toString());
        memberService.recharge(memberId, amount);
        return Result.success();
    }

    @PostMapping("/renew")
    public Result<Void> renew(@RequestBody Map<String, Object> params) {
        Long memberId = Long.valueOf(params.get("memberId").toString());
        Integer months = Integer.valueOf(params.get("months").toString());
        memberService.renew(memberId, months);
        return Result.success();
    }

    @PutMapping("/status/{id}")
    public Result<Void> toggleStatus(@PathVariable Long id) {
        memberService.toggleStatus(id);
        return Result.success();
    }
}
