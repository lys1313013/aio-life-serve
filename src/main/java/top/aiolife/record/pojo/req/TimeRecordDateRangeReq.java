package top.aiolife.record.pojo.req;

import lombok.Data;

import java.time.LocalDate;

/**
 * 时间记录日期范围查询请求（AI接口专用）
 *
 * @author Lys
 * @date 2026/04/26
 */
@Data
public class TimeRecordDateRangeReq {
    private LocalDate startDate;
    private LocalDate endDate;
}
