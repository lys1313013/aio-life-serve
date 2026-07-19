package top.aiolife.feedback.pojo.query;

import lombok.Data;

/**
 * 管理员侧反馈查询条件
 *
 * @author Ethan
 * @date 2026/07/19
 */
@Data
public class FeedbackAdminQuery {

    /**
     * 状态筛选
     */
    private String status;

    /**
     * 类型筛选
     */
    private String feedbackType;

    /**
     * 反馈人 ID 精确筛选
     */
    private Long userId;

    /**
     * 关键字（标题 / 内容模糊匹配）
     */
    private String keyword;

    /**
     * 开始时间（yyyy-MM-dd HH:mm:ss）
     */
    private String startTime;

    /**
     * 结束时间（yyyy-MM-dd HH:mm:ss）
     */
    private String endTime;
}
