package top.aiolife.record.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.aiolife.record.mapper.ITimeRecordEntity;
import top.aiolife.record.pojo.entity.TimeRecordEntity;
import top.aiolife.record.service.ITimeRecordService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;

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
}
