package top.aiolife.record.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 首页运动汇总 - 游标分页结果
 *
 * <p>基于 lastDate 游标向前翻页，每页返回 limit 个不重复的日期分组，
 * 适合无限滚动场景，避免按页码分页导致同一天被截断在两页的边界问题。</p>
 *
 * @author Lys
 * @date 2026/06/14
 */
@Data
public class ExerciseDashboardSummaryVO {

    /**
     * 下一页请求时使用的 lastDate（当前结果中最早一天的前一天）；
     * 为 null 时表示已是最后一页
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastDate;

    /**
     * 是否还有更多历史数据
     */
    private Boolean hasMore;

    /**
     * 按日期降序排列的汇总列表
     */
    private List<ExerciseDashboardDayVO> days;
}