package com.gym.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gym.entity.*;
import com.gym.mapper.*;
import com.gym.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired(required = false)
    private MemberMapper memberMapper;

    @Autowired(required = false)
    private OrdersMapper ordersMapper;

    @Autowired(required = false)
    private GymClassMapper gymClassMapper;

    @Autowired(required = false)
    private ClassReserveMapper classReserveMapper;

    @Autowired(required = false)
    private FitnessCourseMapper fitnessCourseMapper;

    @Autowired(required = false)
    private CoachMapper coachMapper;

    @Override
    public Map<String, Object> getOverview() {
        Map<String, Object> result = new HashMap<>();

        // 总会员数
        long totalMembers = memberMapper != null ? memberMapper.selectCount(null) : 0;
        result.put("totalMembers", totalMembers);

        // 活跃会员数（状态正常且未过期）
        long activeMembers = 0;
        if (memberMapper != null) {
            LambdaQueryWrapper<Member> activeWrapper = new LambdaQueryWrapper<>();
            activeWrapper.eq(Member::getStatus, 1)
                    .ge(Member::getExpireTime, LocalDateTime.now());
            activeMembers = memberMapper.selectCount(activeWrapper);
        }
        result.put("activeMembers", activeMembers);

        // 总课程数
        long totalClasses = gymClassMapper != null ? gymClassMapper.selectCount(null) : 0;
        result.put("totalClasses", totalClasses);

        // 总营收（已支付订单总额）
        BigDecimal totalRevenue = BigDecimal.ZERO;
        if (ordersMapper != null) {
            QueryWrapper<Orders> revenueWrapper = new QueryWrapper<>();
            revenueWrapper.select("IFNULL(SUM(amount), 0) as total_amount")
                    .eq("pay_status", 1)
                    .eq("deleted", 0);
            Map<String, Object> revenueMap = ordersMapper.selectMaps(revenueWrapper).stream().findFirst().orElse(null);
            if (revenueMap != null && revenueMap.get("total_amount") != null) {
                totalRevenue = new BigDecimal(revenueMap.get("total_amount").toString());
            }
        }
        result.put("totalRevenue", totalRevenue);

        // 总教练数
        long totalCoaches = coachMapper != null ? coachMapper.selectCount(null) : 0;
        result.put("totalCoaches", totalCoaches);

        // 总教程数
        long totalCourses = fitnessCourseMapper != null ? fitnessCourseMapper.selectCount(null) : 0;
        result.put("totalCourses", totalCourses);

        return result;
    }

    @Override
    public List<Map<String, Object>> getMemberGrowth(Integer days) {
        if (days == null) {
            days = 7;
        }
        List<Map<String, Object>> result = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

            long count = 0;
            if (memberMapper != null) {
                LambdaQueryWrapper<Member> wrapper = new LambdaQueryWrapper<>();
                wrapper.between(Member::getCreateTime, startOfDay, endOfDay);
                count = memberMapper.selectCount(wrapper);
            }

            Map<String, Object> item = new HashMap<>();
            item.put("date", date.format(formatter));
            item.put("count", count);
            result.add(item);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getRevenue(String type) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (ordersMapper == null) {
            return result;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        int days = "month".equals(type) ? 30 : 7;

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

            QueryWrapper<Orders> wrapper = new QueryWrapper<>();
            wrapper.select("IFNULL(SUM(amount), 0) as total_amount")
                    .eq("pay_status", 1)
                    .eq("deleted", 0)
                    .between("pay_time", startOfDay, endOfDay);
            Map<String, Object> revenueMap = ordersMapper.selectMaps(wrapper).stream().findFirst().orElse(null);

            BigDecimal amount = BigDecimal.ZERO;
            if (revenueMap != null && revenueMap.get("total_amount") != null) {
                amount = new BigDecimal(revenueMap.get("total_amount").toString());
            }

            Map<String, Object> item = new HashMap<>();
            item.put("date", date.format(formatter));
            item.put("amount", amount);
            result.add(item);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getCourseReserve() {
        if (classReserveMapper == null || gymClassMapper == null) {
            return new ArrayList<>();
        }

        QueryWrapper<ClassReserve> wrapper = new QueryWrapper<>();
        wrapper.select("class_id, COUNT(*) as reserve_count")
                .eq("deleted", 0)
                .groupBy("class_id")
                .orderByDesc("reserve_count");
        List<Map<String, Object>> reserveList = classReserveMapper.selectMaps(wrapper);

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> item : reserveList) {
            Map<String, Object> map = new HashMap<>();
            Long classId = Long.valueOf(item.get("class_id").toString());
            GymClass gymClass = gymClassMapper.selectById(classId);
            map.put("classId", classId);
            map.put("className", gymClass != null ? gymClass.getName() : "未知课程");
            map.put("reserveCount", item.get("reserve_count"));
            result.add(map);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getCoursePlayTop10() {
        if (fitnessCourseMapper == null) {
            return new ArrayList<>();
        }

        QueryWrapper<FitnessCourse> wrapper = new QueryWrapper<>();
        wrapper.select("id, title, play_count")
                .eq("deleted", 0)
                .orderByDesc("play_count")
                .last("LIMIT 10");
        List<Map<String, Object>> courseList = fitnessCourseMapper.selectMaps(wrapper);

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> item : courseList) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", item.get("id"));
            map.put("title", item.get("title"));
            map.put("playCount", item.get("play_count"));
            result.add(map);
        }
        return result;
    }
}
