package top.aiolife.record.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.aiolife.record.mapper.ITimeRecordEntity;
import top.aiolife.record.pojo.entity.TimeRecordEntity;
import top.aiolife.record.pojo.vo.RecommendNextVO;
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
@Service
public class TimeRecordServiceImpl extends ServiceImpl<ITimeRecordEntity, TimeRecordEntity> implements ITimeRecordService {

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
