package top.aiolife.record.mcp.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * MCP 任务视图对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskMcpVO {

    /**
     * 任务ID
     */
    private Long id;

    /**
     * 任务内容/名称
     */
    private String content;

    /**
     * 任务明细列表
     */
    private List<TaskDetailMcpVO> details;
}
