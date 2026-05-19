package top.aiolife.record.mcp.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.langchain4j.model.output.structured.Description;
import lombok.Data;
import top.aiolife.record.pojo.req.ExerciseRecordReq;

import java.time.LocalDate;
import java.util.List;

/**
 * MCP 时间记录保存请求体（AI 专用）
 */
@Data
public class TimeRecordSaveMcpReq {

    @Description("分类id")
    private String categoryId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Description("日期，格式：yyyy-MM-dd")
    private LocalDate date;

    @Description("开始时间（从 0:00 开始的分钟数，必须是计算好的整数，如 1218，不能使用数学表达式）")
    private Integer startTime;

    @Description("结束时间（从 0:00 开始的分钟数，必须是计算好的整数，如 1274，不能使用数学表达式）")
    private Integer endTime;

    @Description("标题")
    private String title;

    @Description("描述")
    private String description;

    @Description("时长（分钟，必须是整数，不能使用数学表达式）")
    private Integer duration;

    @Description("关联的练习记录列表")
    private List<ExerciseRecordReq> exercises;
}