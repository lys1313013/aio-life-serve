package com.lys.record.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lys.record.pojo.entity.BVideoEntity;
import com.lys.record.pojo.vo.StatusCount;

import java.util.List;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2025/10/06 23:03
 */
public interface IBVideoMapper extends BaseMapper<BVideoEntity> {

    /**
     * 获取状态和数量
     */
    List<StatusCount> getStatusCount(Integer userId);

    /**
     * 获取已学习时长
     */
    Integer getWatchTime(Integer userId);

    /**
     * 获取总时长
     */
    Integer getTotalTime(Integer userId);
}
