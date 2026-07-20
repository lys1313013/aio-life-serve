package top.aiolife.feedback.api;

import cn.dev33.satoken.stp.StpUtil;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.aiolife.config.MinioConfig;
import top.aiolife.core.query.CommonQuery;
import top.aiolife.core.resq.ApiResponse;
import top.aiolife.core.resq.PageResp;
import top.aiolife.feedback.pojo.query.FeedbackAdminQuery;
import top.aiolife.feedback.pojo.req.FeedbackCommentCreateReq;
import top.aiolife.feedback.pojo.req.FeedbackCreateReq;
import top.aiolife.feedback.pojo.vo.FeedbackCommentVO;
import top.aiolife.feedback.pojo.vo.FeedbackDetailVO;
import top.aiolife.feedback.pojo.vo.FeedbackVO;
import top.aiolife.feedback.service.IFeedbackService;
import top.aiolife.record.pojo.vo.FileVO;
import top.aiolife.record.service.IFileService;

import java.util.UUID;

/**
 * 用户反馈控制器（用户侧）
 *
 * @author Ethan
 * @date 2026/07/19
 */
@RestController
@RequestMapping("/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final IFeedbackService feedbackService;
    private final IFileService fileService;

    @Resource
    private MinioConfig minioConfig;

    /**
     * 提交反馈
     */
    @PostMapping
    public ApiResponse<FeedbackVO> create(@RequestBody FeedbackCreateReq req) {
        long userId = StpUtil.getLoginIdAsLong();
        return ApiResponse.success(feedbackService.create(userId, req));
    }

    /**
     * 我的反馈列表
     */
    @GetMapping("/my")
    public ApiResponse<PageResp<FeedbackVO>> listMy(CommonQuery<FeedbackAdminQuery> query) {
        long userId = StpUtil.getLoginIdAsLong();
        return ApiResponse.success(feedbackService.listMy(userId, query));
    }

    /**
     * 我的反馈详情
     */
    @GetMapping("/my/{id}")
    public ApiResponse<FeedbackDetailVO> getMyDetail(@PathVariable("id") long id) {
        long userId = StpUtil.getLoginIdAsLong();
        return ApiResponse.success(feedbackService.getMyDetail(userId, id));
    }

    /**
     * 追加评论
     */
    @PostMapping("/{id}/comment")
    public ApiResponse<FeedbackCommentVO> addComment(@PathVariable("id") long id,
                                                     @RequestBody FeedbackCommentCreateReq req) {
        long userId = StpUtil.getLoginIdAsLong();
        return ApiResponse.success(feedbackService.addComment(userId, id, req));
    }

    /**
     * 撤销反馈（仅 PENDING）
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> cancel(@PathVariable("id") long id) {
        long userId = StpUtil.getLoginIdAsLong();
        feedbackService.cancelMy(userId, id);
        return ApiResponse.success();
    }

    /**
     * 上传图片附件
     */
    @PostMapping("/upload-attachment")
    public ApiResponse<FileVO> uploadAttachment(@RequestParam("file") MultipartFile file,
                                                @RequestParam(value = "bizType", defaultValue = "feedback") String bizType) {
        try {
            String fileName = file.getOriginalFilename();
            String extension = StringUtils.isNotEmpty(fileName) && fileName.contains(".")
                    ? fileName.substring(fileName.lastIndexOf('.')) : "";
            String objectName = StpUtil.getLoginIdAsLong() + "/feedback/" + UUID.randomUUID() + extension;
            String bucketName = StringUtils.isNotEmpty(minioConfig.getBucketName()) ? minioConfig.getBucketName() : "aiolife";
            var entity = fileService.uploadAndSave(file, bizType, bucketName, objectName, 0);
            return ApiResponse.success(fileService.toVO(entity));
        } catch (Exception e) {
            return ApiResponse.error("113000", "上传失败: " + e.getMessage());
        }
    }
}
