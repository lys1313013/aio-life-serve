package top.aiolife.record.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import top.aiolife.core.util.SysUtil;
import top.aiolife.record.convertor.TimeRecordConvertor;
import top.aiolife.record.mapper.ITimeRecordMapper;
import top.aiolife.record.pojo.entity.ExerciseRecordEntity;
import top.aiolife.record.pojo.entity.TimeRecordEntity;
import top.aiolife.record.pojo.req.ExerciseRecordReq;
import top.aiolife.record.pojo.req.TimeRecordReq;
import top.aiolife.record.pojo.vo.RecommendNextVO;
import top.aiolife.record.service.IExerciseRecordService;
import top.aiolife.record.service.IReadRecordService;
import top.aiolife.record.service.IMovieService;
import top.aiolife.record.pojo.entity.ReadRecordEntity;
import top.aiolife.record.pojo.entity.MovieEntity;
import top.aiolife.record.pojo.enums.ProgressStatusEnum;
import top.aiolife.record.pojo.enums.RelateTypeEnum;
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

    private final ITimeRecordMapper timeRecordMapper;
    private final IExerciseRecordService exerciseRecordService;
    private final IReadRecordService readRecordService;
    private final IMovieService movieService;

    private void updateRelateStatusIfNecessary(TimeRecordEntity entity) {
        if (entity.getRelateId() != null && entity.getRelateType() != null) {
            log.info("Check relate status, type: {}, id: {}", entity.getRelateType(), entity.getRelateId());
            if (entity.getRelateType().equals(RelateTypeEnum.READ.getValue())) {
                ReadRecordEntity readRecord = readRecordService.getById(entity.getRelateId());
                if (readRecord != null && readRecord.getStatus() != null && readRecord.getStatus().equals(ProgressStatusEnum.NOT_STARTED.getCode())) {
                    readRecord.setStatus(ProgressStatusEnum.IN_PROGRESS.getCode());
                    log.info("Updating read record {} status from NOT_STARTED to IN_PROGRESS", readRecord.getId());
                    readRecordService.updateById(readRecord);
                }
            } else if (entity.getRelateType().equals(RelateTypeEnum.MOVIE.getValue())) {
                MovieEntity movie = movieService.getById(entity.getRelateId());
                if (movie != null && movie.getStatus() != null && movie.getStatus().equals(ProgressStatusEnum.NOT_STARTED.getCode())) {
                    movie.setStatus(ProgressStatusEnum.IN_PROGRESS.getCode());
                    log.info("Updating movie {} status from NOT_STARTED to IN_PROGRESS", movie.getId());
                    movieService.updateById(movie);
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveTimeRecord(TimeRecordReq timeRecordReq) {
        TimeRecordEntity entity = TimeRecordConvertor.INSTANCE.Req2Entity(timeRecordReq);
        List<ExerciseRecordReq> exerciseRecordReqs = timeRecordReq.getExercises();

        long userId = StpUtil.getLoginIdAsLong();
        entity.setUserId(userId);
        entity.setCreateUser(StpUtil.getLoginIdAsLong());
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());

        // 限制时间最大值为 1439 (23:59)
        if (entity.getStartTime() != null && entity.getStartTime() > 1439) entity.setStartTime(1439);
        if (entity.getEndTime() != null && entity.getEndTime() > 1439) entity.setEndTime(1439);

        if (entity.getStartTime() != null && entity.getEndTime() != null) {
            entity.setDuration(entity.getEndTime() - entity.getStartTime() + 1);
        }

        this.save(entity);
        updateRelateStatusIfNecessary(entity);

        if (exerciseRecordReqs != null && !exerciseRecordReqs.isEmpty()) {
            List<ExerciseRecordEntity> validExercises = new java.util.ArrayList<>();
            for (ExerciseRecordReq exerciseReq : exerciseRecordReqs) {
                if (SysUtil.isEmpty(exerciseReq.getExerciseTypeId())) {
                    continue;
                }
                ExerciseRecordEntity exercise = new ExerciseRecordEntity();
                exercise.setUserId(userId);
                exercise.setTimeId(entity.getId());
                exercise.fillCreateCommonField(userId);
                exercise.setExerciseDate(entity.getDate());
                exercise.setExerciseTypeId(exerciseReq.getExerciseTypeId());
                exercise.setExerciseCount(exerciseReq.getExerciseCount());
                exercise.setDescription(exerciseReq.getDescription());
                validExercises.add(exercise);
            }
            if (!validExercises.isEmpty()) {
                exerciseRecordService.saveBatch(validExercises);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTimeRecord(TimeRecordReq timeRecordReq) {
        TimeRecordEntity entity = TimeRecordConvertor.INSTANCE.Req2Entity(timeRecordReq);
        List<ExerciseRecordReq> exerciseRecordReqs = timeRecordReq.getExercises();

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
        updateRelateStatusIfNecessary(entity);

        // 删除旧的运动记录
        exerciseRecordService.remove(new LambdaQueryWrapper<ExerciseRecordEntity>()
                .eq(ExerciseRecordEntity::getTimeId, entity.getId())
                .eq(ExerciseRecordEntity::getUserId, userId));

        // 添加新的运动记录
        if (exerciseRecordReqs != null && !exerciseRecordReqs.isEmpty()) {
            List<ExerciseRecordEntity> validExercises = new java.util.ArrayList<>();
            for (ExerciseRecordReq exerciseReq : exerciseRecordReqs) {
                if (SysUtil.isEmpty(exerciseReq.getExerciseTypeId())) {
                    continue;
                }
                ExerciseRecordEntity exercise = new ExerciseRecordEntity();
                exercise.setUserId(userId);
                exercise.setTimeId(entity.getId());
                exercise.fillCreateCommonField(userId);
                exercise.setId(null);
                exercise.setExerciseDate(entity.getDate());
                exercise.setExerciseTypeId(exerciseReq.getExerciseTypeId());
                exercise.setExerciseCount(exerciseReq.getExerciseCount());
                exercise.setDescription(exerciseReq.getDescription());
                validExercises.add(exercise);
            }
            if (!validExercises.isEmpty()) {
                exerciseRecordService.saveBatch(validExercises);
            }
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
    public String recommendType(long userId, String date, int time, String previousCategoryId) {
        // 0. 计算原逻辑目标日期
        // 规则：周一参考上周五的数据，周六参考上周日的数据，其他情况参考前一天的数据
        String targetDate = date;
        LocalDate localDate = LocalDate.parse(date);
        int dayOfWeek = localDate.getDayOfWeek().getValue();
        if (dayOfWeek == 6) {
            targetDate = localDate.minusDays(6).toString();
        } else if (dayOfWeek == 1) {
            targetDate = localDate.minusDays(3).toString();
        } else  {
            targetDate = localDate.minusDays(1).toString();
        }

        // 1. 优先使用原有推荐逻辑：获取过去对应日期的同时间推荐
        TimeRecordEntity originalRecommend = this.baseMapper.recommendType(userId, targetDate, time);
        String categoryId = originalRecommend != null ? originalRecommend.getCategoryId() : null;

        // 2. 优化逻辑：如果原有逻辑生成的推荐分类，与“紧邻的上一条记录分类”相同，则进行干预，避免连续出现相同分类
        if (categoryId != null && categoryId.equals(previousCategoryId)) {
            boolean isWorkday = dayOfWeek <= 5;

            // 2.1 寻找次优推荐：尝试预测其后续行为
            // 基于历史数据（区分工作日/休息日），查找历史上紧接在 previousCategoryId 之后最常发生的分类
            String nextCategory = this.baseMapper.getMostFrequentNextCategory(userId, previousCategoryId, isWorkday);
            if (nextCategory != null && !nextCategory.isEmpty()) {
                return nextCategory;
            }

            // 2.2 寻找次优推荐：如果上述预测无结果，则退阶到“历史最高频”
            // 统计历史上在当前 time 时间段发生过的所有分类，按频次降序，并强制排除掉 previousCategoryId，取最高频分类
            return this.baseMapper.getMostFrequentCategoryAtTime(userId, time, previousCategoryId, isWorkday);
        }

        return categoryId;
    }

    @Override
    public RecommendNextVO recommendNext(long userId, String date) {
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
