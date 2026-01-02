package com.lys.record.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lys.record.mapper.IExerciseRecordMapper;
import com.lys.record.pojo.entity.ExerciseRecordEntity;
import com.lys.record.service.IExerciseRecordService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * 运动记录Service实现类
 *
 * @author Lys
 * @date 2025-11-29 18:40
 */
@Service
public class ExerciseRecordServiceImpl extends ServiceImpl<IExerciseRecordMapper, ExerciseRecordEntity> implements IExerciseRecordService {

    @Override
    public int countTodayExerciseTypes(Long userId) {
        LocalDate today = LocalDate.now();
        return (int) this.lambdaQuery()
                .eq(ExerciseRecordEntity::getUserId, userId)
                .eq(ExerciseRecordEntity::getExerciseDate, today)
                .select(ExerciseRecordEntity::getExerciseTypeId)
                .list()
                .stream()
                .map(ExerciseRecordEntity::getExerciseTypeId)
                .distinct()
                .count();
    }

    @Override
    public int getConsecutiveExerciseDays(Long userId) {
        // 获取该用户所有不重复的运动日期，按日期降序排列
        List<LocalDate> dates = this.lambdaQuery()
                .eq(ExerciseRecordEntity::getUserId, userId)
                .select(ExerciseRecordEntity::getExerciseDate)
                .orderByDesc(ExerciseRecordEntity::getExerciseDate)
                .list()
                .stream()
                .map(ExerciseRecordEntity::getExerciseDate)
                .distinct()
                .toList();

        if (dates.isEmpty()) {
            return 0;
        }

        LocalDate today = LocalDate.now();
        LocalDate lastDate = dates.get(0);

        // 如果最后一次运动不是今天也不是昨天，则连续天数为0
        if (!lastDate.equals(today) && !lastDate.equals(today.minusDays(1))) {
            return 0;
        }

        int streak = 0;
        LocalDate expectedDate = lastDate;
        for (LocalDate date : dates) {
            if (date.equals(expectedDate)) {
                streak++;
                expectedDate = expectedDate.minusDays(1);
            } else {
                break;
            }
        }

        return streak;
    }
}