package top.aiolife.record.pojo.csdn;

import lombok.Data;

/**
 * CSDN 用户数据统计
 */
@Data
public class CsdnStatsVO {
    /**
     * 总访问量/阅读数
     */
    private Integer viewCount;

    /**
     * 原创文章数
     */
    private Integer originalCount;

    /**
     * 全站排名
     */
    private Integer rank;

    /**
     * 粉丝数
     */
    private Integer fansCount;

    /**
     * 获赞数
     */
    private Integer likeCount;

    /**
     * 评论数
     */
    private Integer commentCount;
}