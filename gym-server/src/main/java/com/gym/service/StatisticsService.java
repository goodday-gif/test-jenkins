package com.gym.service;

import java.util.List;
import java.util.Map;

public interface StatisticsService {

    /**
     * 获取总览数据
     */
    Map<String, Object> getOverview();

    /**
     * 会员增长趋势
     */
    List<Map<String, Object>> getMemberGrowth(Integer days);

    /**
     * 营收统计
     */
    List<Map<String, Object>> getRevenue(String type);

    /**
     * 课程预约量统计
     */
    List<Map<String, Object>> getCourseReserve();

    /**
     * 教程播放量排行Top10
     */
    List<Map<String, Object>> getCoursePlayTop10();
}
