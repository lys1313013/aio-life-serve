package top.aiolife.record.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.aiolife.record.pojo.entity.TimeRecordEntity;
import top.aiolife.record.pojo.vo.RecommendNextVO;

import top.aiolife.record.pojo.req.TimeRecordReq;

import java.time.LocalDate;

/**
 * 时间记录Service接口
 *
 * @author Lys
 * @date 2026-01-10 23:55
 */
public interface ITimeRecordService extends IService<TimeRecordEntity> {

    /**
     * 保存时间记录
     *
     * @param timeRecordReq 请求参数
     */
    void saveTimeRecord(TimeRecordReq timeRecordReq);

    /**
     * 更新时间记录
     *
     * @param timeRecordReq 请求参数
     */
    void updateTimeRecord(TimeRecordReq timeRecordReq);

    void removeById(String id, long userId);

    /**
     * 根据日期删除记录及其关联的运动记录
     *
     * @param date   日期
     * @param userId 用户ID
     */
    void removeByDate(LocalDate date, long userId);

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
    TimeRecordEntity recommendType(long userId, String date, int time);

    /**
     * 推荐下一个时间块
     * @param userId 用户ID
     * @param date 日期 yyyy-MM-dd
     * @return 推荐结果及当日记录
     */
    RecommendNextVO recommendNext(long userId, String date);
}
