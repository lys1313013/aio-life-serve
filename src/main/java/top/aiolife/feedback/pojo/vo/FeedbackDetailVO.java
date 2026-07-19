package top.aiolife.feedback.pojo.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 反馈详情 VO（含评论时间线）
 *
 * @author Ethan
 * @date 2026/07/19
 */
@Getter
@Setter
public class FeedbackDetailVO extends FeedbackVO {

    /**
     * 完整内容（Markdown）
     */
    private String content;

    /**
     * 评论时间线（按时间正序）
     */
    private List<FeedbackCommentVO> comments;
}
