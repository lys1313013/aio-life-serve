package top.aiolife.record.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 消息通知VO
 *
 * @author Lys
 * @date 2025/04/19 10:52
 */
@Data
public class NotificationVo {

    /**
     * 头像url
     */
    private String avatar;

    /**
     * 标题
     */
    private String title;

    /**
     * 消息
     */
    private String message;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
