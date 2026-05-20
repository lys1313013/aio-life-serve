package top.aiolife.record.mcp.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

import java.time.LocalDate;

/**
 * MCP 时间记录保存请求体
 */
@Data
public class TimeRecordSaveMcpReq {

    @Description("分类id")
    private String categoryId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Description("日期，格式：yyyy-MM-dd")
    private LocalDate date;

    @Description("标题（与标题不同时传入）")
    private String title;

    @Description("描述")
    private String description;
}
