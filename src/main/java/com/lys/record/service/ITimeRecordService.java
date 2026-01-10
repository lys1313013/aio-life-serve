package com.lys.record.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lys.record.pojo.entity.TimeRecordEntity;

/**
 * 时间记录Service接口
 *
 * @author Lys
 * @date 2026-01-10 23:55
 */
public interface ITimeRecordService extends IService<TimeRecordEntity> {

    /**
     * 获取当天最后一条记录到现在的时间（分钟）
     *
     * @param userId 用户ID
     * @return 时间差（分钟），如果没有记录则返回从当天 00:00 开始的时长
     */
    String getLastRecordTimeDiff(Long userId);

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
