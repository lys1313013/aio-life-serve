package com.lys.record.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lys.record.pojo.entity.BVideoEntity;
import com.lys.record.pojo.vo.StatusCount;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2025/10/06 23:03
 */
public interface IBVideoMapper extends BaseMapper<BVideoEntity> {

    @Select("""
            SELECT status, count(*) AS count
            FROM bilibili_video
            WHERE is_deleted = 0 and user_id = #{userId}
            GROUP BY status
            """)
    List<StatusCount> getStatusCount(Integer userId);
}
