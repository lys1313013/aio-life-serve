package com.lys.record.pojo.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 任务实体类
 *
 * @author Lys
 * @date 2025/04/10 22:45
 */
@Data
public class TaskEntity {
    /**
     * 任务ID
     */
    private Integer id;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 任务内容
     */
    private String content;

    /**
     * 目标完成时间
     */
    private LocalDateTime deadline;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
