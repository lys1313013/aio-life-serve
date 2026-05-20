package top.aiolife.record.mcp.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MCP 时迹分类视图对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeTrackerCategoryMcpVO {

    /**
     * 分类ID
     */
    private Long id;

    /**
     * 分类名称
     */
    private String name;
}
