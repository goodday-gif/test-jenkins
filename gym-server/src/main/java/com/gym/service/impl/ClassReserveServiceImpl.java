package com.gym.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gym.common.BusinessException;
import com.gym.entity.ClassReserve;
import com.gym.entity.GymClass;
import com.gym.mapper.ClassReserveMapper;
import com.gym.service.ClassReserveService;
import com.gym.service.GymClassService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class ClassReserveServiceImpl extends ServiceImpl<ClassReserveMapper, ClassReserve> implements ClassReserveService {

    @Resource
    private GymClassService gymClassService;

    @Override
    public IPage<ClassReserve> getReserveList(Page<ClassReserve> page, Long classId, Long memberId, Integer status) {
        LambdaQueryWrapper<ClassReserve> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(classId != null, ClassReserve::getClassId, classId);
        wrapper.eq(memberId != null, ClassReserve::getMemberId, memberId);
        wrapper.eq(status != null, ClassReserve::getStatus, status);
        wrapper.orderByDesc(ClassReserve::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    public void checkIn(Long id) {
        ClassReserve reserve = getById(id);
        if (reserve == null) {
            throw new BusinessException("预约记录不存在");
        }
        if (reserve.getStatus() != 1) {
            throw new BusinessException("该预约状态不允许签到");
        }
        if (reserve.getCheckIn() == 1) {
            throw new BusinessException("已签到，无需重复操作");
        }
        reserve.setCheckIn(1);
        updateById(reserve);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelReserve(Long id) {
        ClassReserve reserve = getById(id);
        if (reserve == null) {
            throw new BusinessException("预约记录不存在");
        }
        if (reserve.getStatus() != 1) {
            throw new BusinessException("该预约状态不允许取消");
        }
        // 更新预约状态为已取消
        reserve.setStatus(0);
        updateById(reserve);

        // 更新课程当前人数 -1
        GymClass gymClass = gymClassService.getById(reserve.getClassId());
        if (gymClass != null && gymClass.getCurrentCount() != null && gymClass.getCurrentCount() > 0) {
            gymClass.setCurrentCount(gymClass.getCurrentCount() - 1);
            gymClassService.updateById(gymClass);
        }
    }
}
