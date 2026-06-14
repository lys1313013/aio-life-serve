package top.aiolife.record.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 首页运动汇总 - 按天聚合的一行记录
 *
 * @author Lys
 * @date 2026/06/14
 */
@Data
public class ExerciseDashboardDayVO {

    /**
     * 运动日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    /**
     * 当日所有运动的总次数
     */
    private Integer totalCount;

    /**
     * 当日按运动类型聚合后的子项列表
     */
    private List<ExerciseDashboardItemVO> items;
}