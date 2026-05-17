package top.aiolife.record.pojo.csdn;

import lombok.Data;

/**
 * CSDN 最近文章列表
 */
@Data
public class CsdnArticleVO {
    /**
     * 文章 ID
     */
    private String id;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章链接
     */
    private String url;

    /**
     * 文章摘要/描述
     */
    private String description;

    /**
     * 发布/更新时间
     */
    private String postTime;

    /**
     * 阅读数
     */
    private Integer viewCount;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 收藏数
     */
    private Integer collectCount;
}