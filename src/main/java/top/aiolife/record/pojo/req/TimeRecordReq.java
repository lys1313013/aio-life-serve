package top.aiolife.record.pojo.req;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import top.aiolife.mcp.annotation.McpField;

import java.time.LocalDate;
import java.util.List;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2026-02-22 17:34
 */
@Data
@TableName("time_record")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TimeRecordReq {

    /**
     * 主键ID
     */
    @TableId
    @McpField(description = "主键ID")
    private String id;

    /**
     * 分类id
     */
    @McpField(description = "分类id")
    private String categoryId;

    /**
     * 日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @McpField(description = "日期，格式：yyyy-MM-dd")
    private LocalDate date;

    /**
     * 开始时间 （分钟）包含开始时间
     */
    @McpField(description = "开始时间（从 0:00 开始的分钟数）")
    private Integer startTime;
    /**
     * 结束时间 （分钟） 包含结束时间
     */
    @McpField(description = "结束时间（从 0:00 开始的分钟数）")
    private Integer endTime;
    /**
     * 标题
     */
    @McpField(description = "标题")
    private String title;
    /**
     * 描述
     */
    @McpField(description = "描述")
    private String description;
    /**
     * 时长（分钟）
     */
    @McpField(description = "时长（分钟）")
    private Integer duration;

    /**
     * 关联的练习记录
     */
    @McpField(description = "关联的练习记录列表")
    List<ExerciseRecordReq> exercises;
}
