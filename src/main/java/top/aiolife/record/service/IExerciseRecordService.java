package top.aiolife.record.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.aiolife.record.pojo.entity.ExerciseRecordEntity;
import top.aiolife.record.pojo.vo.ExerciseDashboardSummaryVO;

import java.time.LocalDate;

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

    /**
     * 获取首页运动汇总（按天 × 运动类型聚合），游标分页
     *
     * @param userId   用户ID
     * @param lastDate 下一页游标，传 null 时表示从今天（含）往后开始取
     * @param limit    每页返回的不重复日期数量上限
     * @return 汇总结果（含 hasMore / 下一游标）
     */
    ExerciseDashboardSummaryVO getDashboardSummary(Long userId, LocalDate lastDate, int limit);
}