package top.aiolife.record.mcp.req;

import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

/**
 * MCP 任务明细保存请求体
 */
@Data
public class TaskDetailSaveMcpReq {

    @Description("主任务ID")
    private Long taskId;

    @Description("明细内容")
    private String content;

    @Description("优先级: 1-高, 10-中, 20-低。如果不传默认10")
    private Integer priority;

    @Description("是否关注/收藏: 0-未关注, 1-已关注。如果不传默认0")
    private Integer isStarred;
}
