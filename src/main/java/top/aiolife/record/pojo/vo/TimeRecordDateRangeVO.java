package top.aiolife.record.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 时间记录查询结果 VO（日期范围查询专用）
 * startTime 和 endTime 格式化为 HH:mm
 *
 * @author Lys
 * @date 2026-04-26
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TimeRecordDateRangeVO {
    private String id;
    private String categoryName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @JsonFormat(pattern = "HH:mm")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalDateTime endTime;

    private String title;
}