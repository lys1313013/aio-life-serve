package top.aiolife.feedback.pojo.req;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 批量操作请求（管理员）
 *
 * @author Ethan
 * @date 2026/07/19
 */
@Getter
@Setter
public class FeedbackBatchReq {

    /**
     * 反馈 ID 列表
     */
    private List<Long> idList;

    /**
     * 动作：CLOSE
     */
    private String action;
}
