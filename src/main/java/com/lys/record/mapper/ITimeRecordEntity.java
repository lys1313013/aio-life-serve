package com.lys.record.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lys.record.pojo.entity.TimeRecordEntity;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2025/10/26 15:32
 */
public interface ITimeRecordEntity extends BaseMapper<TimeRecordEntity> {

    /**
     * 推荐分类
     *
     * @param userId 用户id
     * @param date   日期
     * @param time   时间
     * @return 分类id
     */
    TimeRecordEntity recommendType(int userId, String date, int time);
}
