package com.lys.record.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lys.record.pojo.entity.ExerciseRecordEntity;

/**
 * 运动记录Service接口
 *
 * @author Lys
 * @date 2025-11-29 18:40
 */
public interface IExerciseRecordService extends IService<ExerciseRecordEntity> {

    /**
     * 获取今日运动种类
     *
     * @param userId 用户ID
     * @return 运动种类数量
     */
    int countTodayExerciseTypes(Long userId);

    /**
     * 获取连续运动天数
     *
     * @param userId 用户ID
     * @return 连续运动天数
     */
    int getConsecutiveExerciseDays(Long userId);
}