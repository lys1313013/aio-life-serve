package top.aiolife.feedback.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import top.aiolife.feedback.pojo.req.FeedbackBatchReq;
import top.aiolife.feedback.pojo.req.FeedbackCommentCreateReq;
import top.aiolife.feedback.pojo.req.FeedbackCreateReq;
import top.aiolife.feedback.pojo.req.FeedbackStatusUpdateReq;
import top.aiolife.feedback.pojo.vo.FeedbackCommentVO;
import top.aiolife.feedback.pojo.vo.FeedbackDetailVO;
import top.aiolife.feedback.pojo.vo.FeedbackVO;

/**
 * 反馈服务接口
 *
 * @author Ethan
 * @date 2026/07/19
 */
public interface IFeedbackService {

    /**
     * 用户提交反馈
     *
     * @param userId 当前用户 ID
     * @param req    创建请求
     * @return 创建后的 VO
     */
    FeedbackVO create(long userId, FeedbackCreateReq req);

    /**
     * 我的反馈列表（分页）
     *
     * @param userId       当前用户 ID
     * @param status       状态筛选（可空）
     * @param feedbackType 类型筛选（可空）
     * @param page         页码
     * @param size         每页大小
     * @return 分页结果
     */
    Page<FeedbackVO> listMy(long userId, String status, String feedbackType, int page, int size);

    /**
     * 我的反馈详情（含评论时间线）
     *
     * @param userId     当前用户 ID
     * @param feedbackId 反馈 ID
     * @return 详情 VO
     */
    FeedbackDetailVO getMyDetail(long userId, long feedbackId);

    /**
     * 用户追加评论
     *
     * @param userId     当前用户 ID
     * @param feedbackId 反馈 ID
     * @param req        评论请求
     * @return 评论 VO
     */
    FeedbackCommentVO addComment(long userId, long feedbackId, FeedbackCommentCreateReq req);

    /**
     * 用户撤销反馈（仅 PENDING 可撤）
     *
     * @param userId     当前用户 ID
     * @param feedbackId 反馈 ID
     */
    void cancelMy(long userId, long feedbackId);

    /**
     * 管理员：全部反馈列表（分页 + 多条件筛选）
     */
    Page<FeedbackVO> listAdmin(String status, String feedbackType, Long userId, String keyword,
                               String startTime, String endTime, int page, int size);

    /**
     * 管理员：反馈详情
     */
    FeedbackDetailVO getAdminDetail(long feedbackId);

    /**
     * 管理员：回复反馈
     */
    FeedbackCommentVO adminReply(long adminId, long feedbackId, FeedbackCommentCreateReq req);

    /**
     * 管理员：变更状态
     */
    FeedbackVO adminUpdateStatus(long adminId, long feedbackId, FeedbackStatusUpdateReq req);

    /**
     * 管理员：批量操作
     */
    void adminBatch(long adminId, FeedbackBatchReq req);
}
