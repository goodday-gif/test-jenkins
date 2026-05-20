package com.gym.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gym.entity.CourseInteract;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface CourseInteractMapper extends BaseMapper<CourseInteract> {

    /**
     * 查询互动记录（绕过@TableLogic，包含已逻辑删除的记录）
     */
    @Select("SELECT * FROM course_interact WHERE member_id = #{memberId} AND course_id = #{courseId} AND type = #{type} LIMIT 1")
    CourseInteract findByMemberAndCourseAndType(@Param("memberId") Long memberId,
                                                @Param("courseId") Long courseId,
                                                @Param("type") Integer type);

    /**
     * 直接更新deleted字段（绕过@TableLogic）
     */
    @Update("UPDATE course_interact SET deleted = #{deleted} WHERE id = #{id}")
    int updateDeletedById(@Param("id") Long id, @Param("deleted") Integer deleted);
}
