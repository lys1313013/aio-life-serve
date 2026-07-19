package top.aiolife.feedback.pojo.req;

import lombok.Getter;
import lombok.Setter;

/**
 * 反馈状态变更请求（管理员）
 *
 * @author Ethan
 * @date 2026/07/19
 */
@Getter
@Setter
public class FeedbackStatusUpdateReq {

    /**
     * PENDING / PROCESSING / RESOLVED / CLOSED / REJECTED
     */
    private String status;
}
