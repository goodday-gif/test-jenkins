package com.gym.controller.front;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.common.BusinessException;
import com.gym.common.PageResult;
import com.gym.common.Result;
import com.gym.entity.ClassReserve;
import com.gym.entity.GymClass;
import com.gym.security.LoginUser;
import com.gym.service.ClassReserveService;
import com.gym.service.GymClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController("frontGymClassController")
@RequestMapping("/api/front/class")
public class GymClassController {

    @Autowired
    private GymClassService gymClassService;

    @Autowired
    private ClassReserveService classReserveService;

    /**
     * 课程列表（分页，支持筛选，只返回状态正常的）
     */
    @GetMapping("/list")
    public Result<PageResult<GymClass>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer classType,
            @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<GymClass> wrapper = new LambdaQueryWrapper<>();
        wrapper.ne(GymClass::getStatus, 0); // 排除已取消
        wrapper.eq(classType != null, GymClass::getClassType, classType);
        if (StringUtils.hasText(keyword)) {
            wrapper.like(GymClass::getName, keyword);
        }
        wrapper.orderByDesc(GymClass::getClassTime);
        Page<GymClass> page = new Page<>(pageNum, pageSize);
        IPage<GymClass> result = gymClassService.page(page, wrapper);
        return Result.success(new PageResult<>(result.getTotal(), result.getRecords()));
    }

    /**
     * 课程详情
     */
    @GetMapping("/{id}")
    public Result<GymClass> detail(@PathVariable Long id) {
        GymClass gymClass = gymClassService.getById(id);
        if (gymClass == null) {
            throw new BusinessException("课程不存在");
        }
        return Result.success(gymClass);
    }

    /**
     * 预约课程（需登录）
     */
    @PostMapping("/{id}/reserve")
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> reserve(@PathVariable Long id) {
        Long memberId = getCurrentMemberId();

        GymClass gymClass = gymClassService.getById(id);
        if (gymClass == null) {
            throw new BusinessException("课程不存在");
        }
        if (gymClass.getStatus() != 1) {
            throw new BusinessException("该课程当前不可预约");
        }
        // 检查名额
        if (gymClass.getCurrentCount() != null && gymClass.getMaxCapacity() != null
                && gymClass.getCurrentCount() >= gymClass.getMaxCapacity()) {
            throw new BusinessException("课程名额已满");
        }
        // 检查重复预约
        long count = classReserveService.count(new LambdaQueryWrapper<ClassReserve>()
                .eq(ClassReserve::getMemberId, memberId)
                .eq(ClassReserve::getClassId, id)
                .eq(ClassReserve::getStatus, 1));
        if (count > 0) {
            throw new BusinessException("您已预约该课程，请勿重复预约");
        }
        // 创建预约记录
        ClassReserve reserve = new ClassReserve();
        reserve.setMemberId(memberId);
        reserve.setClassId(id);
        reserve.setReserveTime(LocalDateTime.now());
        reserve.setCheckIn(0);
        reserve.setStatus(1);
        reserve.setDeleted(0);
        reserve.setCreateTime(LocalDateTime.now());
        reserve.setUpdateTime(LocalDateTime.now());
        classReserveService.save(reserve);
        // 更新课程已预约人数 +1
        gymClassService.update(new LambdaUpdateWrapper<GymClass>()
                .eq(GymClass::getId, id)
                .setSql("current_count = current_count + 1"));
        return Result.success("预约成功", null);
    }

    /**
     * 取消预约（需登录）
     */
    @DeleteMapping("/reserve/{id}")
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> cancelReserve(@PathVariable Long id) {
        Long memberId = getCurrentMemberId();
        ClassReserve reserve = classReserveService.getById(id);
        if (reserve == null) {
            throw new BusinessException("预约记录不存在");
        }
        if (!reserve.getMemberId().equals(memberId)) {
            throw new BusinessException("无权操作");
        }
        if (reserve.getStatus() != 1) {
            throw new BusinessException("该预约状态不允许取消");
        }
        // 更新预约状态为已取消
        reserve.setStatus(0);
        classReserveService.updateById(reserve);
        // 更新课程当前人数 -1
        gymClassService.update(new LambdaUpdateWrapper<GymClass>()
                .eq(GymClass::getId, reserve.getClassId())
                .setSql("current_count = GREATEST(current_count - 1, 0)"));
        return Result.success("取消预约成功", null);
    }

    /**
     * 我的预约列表（需登录，分页）
     */
    @GetMapping("/my-reserves")
    public Result<PageResult<ClassReserve>> myReserves(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long memberId = getCurrentMemberId();
        Page<ClassReserve> page = new Page<>(pageNum, pageSize);
        IPage<ClassReserve> result = classReserveService.getReserveList(page, null, memberId, null);
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
