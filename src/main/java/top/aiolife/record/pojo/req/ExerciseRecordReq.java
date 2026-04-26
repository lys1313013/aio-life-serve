package top.aiolife.record.pojo.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import top.aiolife.mcp.annotation.McpField;

import java.time.LocalDate;

/**
 * 练习记录请求类
 *
 * @author Lys
 * @date 2026/04/26
 */
@Data
public class ExerciseRecordReq {

    /**
     * 主键ID
     */
    @McpField(description = "主键ID，更新时必传")
    private String id;

    /**
     * 运动类型
     */
    @McpField(description = "运动类型ID")
    private String exerciseTypeId;

    /**
     * 运动日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @McpField(description = "运动日期，格式：yyyy-MM-dd")
    private LocalDate exerciseDate;

    /**
     * 运动次数
     */
    @McpField(description = "运动次数")
    private Integer exerciseCount;

    /**
     * 运动描述
     */
    @McpField(description = "运动描述")
    private String description;
}
