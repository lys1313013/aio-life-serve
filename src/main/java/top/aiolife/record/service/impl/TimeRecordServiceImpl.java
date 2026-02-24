package top.aiolife.record.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import top.aiolife.record.convertor.TimeRecordConvertor;
import top.aiolife.record.mapper.ITimeRecordMapper;
import top.aiolife.record.pojo.entity.ExerciseRecordEntity;
import top.aiolife.record.pojo.entity.TimeRecordEntity;
import top.aiolife.record.pojo.req.TimeRecordReq;
import top.aiolife.record.pojo.vo.RecommendNextVO;
import top.aiolife.record.service.IExerciseRecordService;
import top.aiolife.record.service.ITimeRecordService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

/**
 * 时间记录Service实现类
 *
 * @author Lys
 * @date 2026-01-10 23:55
 */
@Slf4j
@Service
@AllArgsConstructor
public class TimeRecordServiceImpl extends ServiceImpl<ITimeRecordMapper, TimeRecordEntity> implements ITimeRecordService {

    private ITimeRecordMapper timeRecordMapper;

    private final IExerciseRecordService exerciseRecordService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveTimeRecord(TimeRecordReq timeRecordReq) {
        TimeRecordEntity entity = TimeRecordConvertor.INSTANCE.Req2Entity(timeRecordReq);
        List<ExerciseRecordEntity> exerciseRecordEntities = timeRecordReq.getExercises();

        long userId = StpUtil.getLoginIdAsLong();
        entity.setUserId(userId);
        entity.setCreateUser(StpUtil.getLoginIdAsInt());
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());

        // 限制时间最大值为 1439 (23:59)
        if (entity.getStartTime() != null && entity.getStartTime() > 1439) entity.setStartTime(1439);
        if (entity.getEndTime() != null && entity.getEndTime() > 1439) entity.setEndTime(1439);

        if (entity.getStartTime() != null && entity.getEndTime() != null) {
            entity.setDuration(entity.getEndTime() - entity.getStartTime() + 1);
        }

        this.save(entity);

        if (exerciseRecordEntities != null && !exerciseRecordEntities.isEmpty()) {
            for (ExerciseRecordEntity exercise : exerciseRecordEntities) {
                exercise.setUserId(userId);
                exercise.setTimeId(entity.getId());
                exercise.fillCreateCommonField(userId);
                exercise.setExerciseDate(entity.getDate());
            }
            exerciseRecordService.saveBatch(exerciseRecordEntities);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTimeRecord(TimeRecordReq timeRecordReq) {
        TimeRecordEntity entity = TimeRecordConvertor.INSTANCE.Req2Entity(timeRecordReq);
        List<ExerciseRecordEntity> exerciseRecordEntities = timeRecordReq.getExercises();

        long userId = StpUtil.getLoginIdAsLong();
        entity.setUserId(userId);
        entity.setUpdateTime(LocalDateTime.now());

        // 限制时间最大值为 1439 (23:59)
        if (entity.getStartTime() != null && entity.getStartTime() > 1439) entity.setStartTime(1439);
        if (entity.getEndTime() != null && entity.getEndTime() > 1439) entity.setEndTime(1439);

        if (entity.getStartTime() != null && entity.getEndTime() != null) {
            entity.setDuration(entity.getEndTime() - entity.getStartTime() + 1);
        }

        this.updateById(entity);

        // 删除旧的运动记录
        exerciseRecordService.remove(new LambdaQueryWrapper<ExerciseRecordEntity>()
                .eq(ExerciseRecordEntity::getTimeId, entity.getId())
                .eq(ExerciseRecordEntity::getUserId, userId));

        // 添加新的运动记录
        if (exerciseRecordEntities != null && !exerciseRecordEntities.isEmpty()) {
            for (ExerciseRecordEntity exercise : exerciseRecordEntities) {
                exercise.setUserId(userId);
                exercise.setTimeId(entity.getId());

                // 新增记录，设置创建信息
                exercise.fillCreateCommonField(userId);
                exercise.setId(null);
                exercise.setExerciseDate(entity.getDate());
            }
            exerciseRecordService.saveBatch(exerciseRecordEntities);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeById(String id, long userId) {
        LambdaQueryWrapper<TimeRecordEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TimeRecordEntity::getId, id);
        queryWrapper.eq(TimeRecordEntity::getUserId, userId);
        this.remove(queryWrapper);

        // 删除关联的运动记录
        exerciseRecordService.remove(new LambdaQueryWrapper<ExerciseRecordEntity>()
                .eq(ExerciseRecordEntity::getTimeId, id)
                .eq(ExerciseRecordEntity::getUserId, userId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeByDate(LocalDate date, long userId) {
        // 1. 查询该日期下的所有时间记录ID
        List<TimeRecordEntity> list = this.lambdaQuery()
                .select(TimeRecordEntity::getId)
                .eq(TimeRecordEntity::getDate, date)
                .eq(TimeRecordEntity::getUserId, userId)
                .list();

        if (list.isEmpty()) {
            return;
        }

        List<String> ids = list.stream().map(TimeRecordEntity::getId).toList();

        // 2. 删除关联的运动记录
        exerciseRecordService.remove(new LambdaQueryWrapper<ExerciseRecordEntity>()
                .in(ExerciseRecordEntity::getTimeId, ids)
                .eq(ExerciseRecordEntity::getUserId, userId));

        // 3. 删除时间记录
        this.removeByIds(ids);
    }

    @Override
    public String getLastRecordTimeDiff(Long userId) {
        LocalDate today = LocalDate.now();
        
        // 获取当天最后一条记录
        TimeRecordEntity lastRecord = this.lambdaQuery()
                .eq(TimeRecordEntity::getUserId, userId)
                .eq(TimeRecordEntity::getDate, today)
                .list()
                .stream()
                .max(Comparator.comparing(TimeRecordEntity::getEndTime))
                .orElse(null);

        LocalTime now = LocalTime.now();
        int currentMinutes = now.getHour() * 60 + now.getMinute();
        
        int diffMinutes;
        if (lastRecord != null) {
            diffMinutes = currentMinutes - lastRecord.getEndTime();
        } else {
            // 如果当天没有记录，则计算从 00:00 到现在的时长
            diffMinutes = currentMinutes;
        }

        if (diffMinutes < 0) {
            return "0分";
        }

        if (diffMinutes < 60) {
            return diffMinutes + "分";
        } else {
            int hours = diffMinutes / 60;
            int minutes = diffMinutes % 60;
            if (minutes == 0) {
                return hours + "小时";
            }
            return hours + "小时" + minutes + "分";
        }
    }

    @Override
    public TimeRecordEntity recommendType(int userId, String date, int time) {
        return this.baseMapper.recommendType(userId, date, time);
    }

    @Override
    public RecommendNextVO recommendNext(int userId, String date) {
        LocalDate targetDate = LocalDate.parse(date);
        LambdaQueryWrapper<TimeRecordEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(TimeRecordEntity::getStartTime, TimeRecordEntity::getEndTime)
                .eq(TimeRecordEntity::getUserId, userId)
                .eq(TimeRecordEntity::getDate, targetDate);
        List<TimeRecordEntity> records = this.list(queryWrapper);
        
        TimeRecordEntity recommend = calculateRecommendNext(records, targetDate);
        return RecommendNextVO.builder()
                .recommend(recommend)
                .records(records)
                .build();
    }

    /**
     * 计算推荐的下一个时间块
     */
    public TimeRecordEntity calculateRecommendNext(List<TimeRecordEntity> records, LocalDate targetDate) {
        records.sort(Comparator.comparingInt(TimeRecordEntity::getStartTime));

        boolean isToday = LocalDate.now().equals(targetDate);
        int lastEndTime = -1;
        int startTime = 0;
        int endTime = 0;
        boolean foundGap = false;

        for (TimeRecordEntity record : records) {
            if (record.getStartTime() > lastEndTime + 1) {
                startTime = lastEndTime + 1;
                endTime = record.getStartTime() - 1;
                foundGap = true;
                break;
            }
            lastEndTime = Math.max(lastEndTime, record.getEndTime());
        }

        if (!foundGap) {
            startTime = lastEndTime + 1;
            if (isToday) {
                LocalDateTime now = LocalDateTime.now();
                endTime = now.getHour() * 60 + now.getMinute();
            } else {
                endTime = startTime + 30;
            }
        }

        if (endTime < startTime) {
            endTime = startTime;
        }

        // 限制最大值为 1439 (23:59)
        if (startTime > 1439) {
            startTime = 1439;
        }
        if (endTime > 1439) {
            endTime = 1439;
        }

        TimeRecordEntity result = new TimeRecordEntity();
        result.setStartTime(startTime);
        result.setEndTime(endTime);
        result.setDate(targetDate);
        result.setDuration(endTime - startTime + 1);

        return result;
    }
}
