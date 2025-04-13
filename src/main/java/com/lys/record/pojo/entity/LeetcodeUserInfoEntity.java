package com.lys.record.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2025/04/13 14:09
 */
@Data
@TableName("leetcode_user_info")
public class LeetcodeUserInfoEntity {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 最长连续打卡天数
     */
    private Integer streak;

    /**
     * 连续打卡天数
     */
    private Integer recentStreak;

    /**
     * 期间总活跃天数
     */
    private Integer totalActiveDays;

    /**
     * 总提交数
     */
    private Integer totalSubmissionCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
